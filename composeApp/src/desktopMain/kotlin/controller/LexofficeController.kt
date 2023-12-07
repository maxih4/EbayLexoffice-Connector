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
import model.lexoffice.*


import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import storage.kvstore


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
                Json.decodeFromString<createUserResponse>(
                    lexofficeClient.post("https://api.lexoffice.io/v1/contacts") {
                        contentType(ContentType.Application.Json)
                        setBody(Json.encodeToJsonElement(newUserRequest))
                    }
                        .bodyAsText())
            return responseCreatedUser.resourceUri!!

        }else{
            return responseFilterUser.content?.first()?.emailAddresses?.private.toString()
        }


    }


}