package controller

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement
import model.ebay.Orders
import model.lexoffice.*


import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import storage.kvstore
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class LexofficeController : KoinComponent {
    private val store: kvstore by inject()
    private val settings = store.settings

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
        val responseFilterUser =
            Json.decodeFromString<filterContactsResponse>(lexofficeClient.get("https://api.lexoffice.io/v1/contacts") {
                url {
                    parameters.append("email", email)
                    parameters.append("name", lastName)
                }
            }.bodyAsText())

        if (responseFilterUser.numberOfElements == 0) {
            //Create new Contact
            val newUserRequest = createUserRequest(
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
                ), archived = false, emailAddresses = EmailAddresses(listOf(email)),
                person = Person(firstName, lastName),
                roles = Roles(Customer(null)),
                version = 0
            )

            val responseCreatedUser =
                Json.decodeFromString<creationResponse>(
                    lexofficeClient.post("https://api.lexoffice.io/v1/contacts") {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToJsonElement(newUserRequest))
                    }
                        .bodyAsText())
            return responseCreatedUser.id!!

        } else {
            return responseFilterUser.content?.first()?.id!!
        }


    }

    suspend fun createInvoiceFromOrder(order: Orders, contactId: String): String {

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

        val newInvoice = createInvoiceRequest(
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

        val createInvoiceResponse = lexofficeClient.post("https://api.lexoffice.io/v1/invoices") {
            url { parameters.append("finalize", "false") }
            contentType(ContentType.Application.Json)
            setBody(Json.encodeToJsonElement(newInvoice))
        }.bodyAsText()


        return createInvoiceResponse;
    }

    private fun calcDiscountPercentage(totalPrice: String?, totalPriceWithDiscount: String?): Float {
        if (totalPrice == totalPriceWithDiscount) return 0F
        var ret: Float? = totalPrice?.toFloatOrNull()?.div(totalPriceWithDiscount?.toFloatOrNull()!!)
        ret = ret?.times(100)
        ret = 100 - ret!!
        if (ret != null) {
            ret = (Math.round(ret * 100.0) / 100.0).toFloat()
        }
        return ret!!
    }


    //Todo Shipping Cost

}