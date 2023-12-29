package controller

import com.sletmoe.bucket4k.SuspendingBucket
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.TimeMeter
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.cio.*

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.ebay.Orders
import model.lexoffice.*


import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.file.Path
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration


class LexofficeController : KoinComponent {
    private val store: StorageController by inject()
    private val settings = store.settings
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val tokenBucket = SuspendingBucket.build {
        addLimit(Bandwidth.simple(2, (1).seconds.toJavaDuration()))
        timeMeter = TimeMeter.SYSTEM_MILLISECONDS
    }

    private val lexofficeClient = HttpClient(CIO) {
        install(Auth) {
            bearer {
                loadTokens {
                    BearerTokens(settings.getString("apiKey", ""), "")
                }
            }
        }
        install(ContentNegotiation) {
            json()
        }
        /*install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.HEADERS
        }*/
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = 5)
            exponentialDelay()
            retryIf{request,response->
                response.status.value==429
            }
            this.modifyRequest { println("Retry Request") }
        }
    }


    suspend fun getContactOrCreateNew(
        lastName: String,
        firstName: String,
        email: String,
        city: String,
        countryCode: String,
        street: String,
        supplement: String,
        zip: String
    ): String {

        tokenBucket.consume(1)

        try {


            val responseFilterUser =
                json.decodeFromString<FilterContactsResponse>(lexofficeClient.get("https://api.lexoffice.io/v1/contacts") {
                    url {
                        if (email.isNotEmpty()) parameters.append("email", email)
                        parameters.append("name", lastName)
                    }
                }.bodyAsText())

            println(responseFilterUser.toString())
            if (responseFilterUser.numberOfElements == 0) {
                println("Neuer Kontakt wird erstellt")
                //Create new Contact
                val newUserRequest = CreateUserRequest(
                    addresses = Addresses(
                        billing = listOf(
                            Billing(
                                city = city,
                                countryCode = countryCode,
                                street = street,
                                supplement = supplement,
                                zip = zip
                            )
                        )
                    ), archived = false,
                    person = Person(firstName, lastName),
                    roles = Roles(Customer(null)),
                    version = 0
                )
                if (email.isNotEmpty()) newUserRequest.emailAddresses = EmailAddresses(listOf(email))
                println(newUserRequest.toString())

                tokenBucket.consume(1)

                val responseCreatedUser =
                    json.decodeFromString<CreationResponse>(
                        lexofficeClient.post("https://api.lexoffice.io/v1/contacts") {
                            contentType(ContentType.Application.Json)
                            setBody(Json.encodeToJsonElement(newUserRequest))
                        }
                            .bodyAsText())
                return responseCreatedUser.id!!


            } else {
                return responseFilterUser.content?.first()?.id!!
            }
        } catch (ex: Exception) {
            println("Exception: " + ex.message)
        }


        return "Fehler"
    }

    suspend fun createInvoiceFromOrder(order: Orders, contactId: String): CreationResponse {

        val lineItemList = ArrayList<LineItem>()
        order.lineItems.forEach { it ->
            lineItemList.add(
                LineItem(
                    name = it.title, quantity = it.quantity,
                    discountPercentage = calcDiscountPercentage(
                        it.total?.value,
                        (it.lineItemCost?.value?.toFloat()!! + it.deliveryCost?.shippingCost?.value!!.toFloat()).toString(),
                    ),
                    type = "custom",
                    description = null,
                    unitName = "Stück",  //Todo in Settings hauen
                    unitPrice = UnitPrice(
                        currency = it.total?.currency,
                        grossAmount = (Math.round((it.lineItemCost?.value?.toFloat()!! / it.quantity?.toFloat()!!) * 100.0) / 100.0).toFloat(),
                        taxRatePercentage = 19      //Todo Percentage aus Ebay ziehen oder in Settings
                    )
                )
            )
        }

        //If deliviery Cost, then add to lineItemList
        if (order.pricingSummary?.deliveryCost?.value?.toFloat()!! > 0F) {
            lineItemList.add(
                LineItem(
                    description = "Versandkosten",//Todo Settings
                    discountPercentage = null,
                    name = "Versand",
                    quantity = 1,
                    type = "custom",
                    unitName = "Anzahl",
                    unitPrice = UnitPrice(
                        currency = order.lineItems.first().total?.currency,
                        grossAmount = order.pricingSummary!!.deliveryCost?.value?.toFloat(),
                        taxRatePercentage = 19
                    ) //Todo Settings

                )

            )
        }

        val newInvoice = CreateInvoiceRequest(
            address = Address(
                contactId = contactId
            ),
            archived = false,
            introduction = "Hier steht ein Rechnungsstring der variable Introduction",
            lineItems = lineItemList,
            taxConditions = TaxConditions(taxType = "gross"),
            voucherDate = order.creationDate,
            title = "Rechnung",   //Todo Settings
            remark = "Fußbereich, Variable remark", //Todo Settings
            totalPrice = TotalPrice(currency = order.totalFeeBasisAmount?.currency),
            paymentConditions =
            PaymentConditions(
                paymentTermLabel = "Sobald der Gesamtbetrag bei uns eingegangen ist, wird die Ware von uns verschickt.", //Todo Settings
                paymentDiscountConditions = null,//Todo Settings
                paymentTermDuration = 14 //Todo Settings
            ),
            shippingConditions = ShippingConditions(
                shippingType = "delivery", shippingDate = ZonedDateTime.parse(
                    order.creationDate,
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX")
                ).plusDays(5L)//Todo Settings
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")).toString()
            )
        )

        tokenBucket.consume(1)

        val createInvoiceResponse =
            json.decodeFromString<CreationResponse>(lexofficeClient.post("https://api.lexoffice.io/v1/invoices") {
                url { parameters.append("finalize", "true") }
                //Todo parameter to settings
                contentType(ContentType.Application.Json)
                setBody(Json.encodeToJsonElement(newInvoice))
            }.bodyAsText())


        return createInvoiceResponse;
    }

    private fun calcDiscountPercentage(totalPrice: String?, totalPriceWithDiscount: String?): Float {
        if (totalPrice == totalPriceWithDiscount) return 0F
        var ret: Float? = totalPrice?.toFloatOrNull()?.div(totalPriceWithDiscount?.toFloatOrNull()!!)
        ret = ret?.times(100)
        ret = 100 - ret!!
        ret = (Math.round(ret * 100.0) / 100.0).toFloat()
        return ret
    }

    suspend fun renderInvoice(url: String): String? {
        tokenBucket.consume(1)

        val renderInvoiceResponse=
            json.decodeFromString<RenderInvoiceResponse>(lexofficeClient.get(url).bodyAsText())
        println("RenderInvoiceResponse = $renderInvoiceResponse")
        return renderInvoiceResponse.documentFileId
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun downloadInvoiceAsPdf(documentFileId: String): Path {
       val path= kotlin.io.path.createTempFile(prefix = "temp-invoice", suffix = ".pdf")
        tokenBucket.consume(1)
        path.toFile().writeBytes(Base64.decode(lexofficeClient.get("https://api.lexoffice.io/v1/files/$documentFileId").bodyAsText()))
        println("Writing to file done. Path = $path")
        return path
    }

    fun deletePdf(path:Path){
        path.toFile().delete()
    }


//Todo Shipping Cost

}