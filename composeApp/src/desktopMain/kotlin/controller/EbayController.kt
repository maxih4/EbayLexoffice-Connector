package com.ktor.security


import controller.StorageController
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import model.ebay.OrderResponse
import model.ebay.RefreshTokenResponse
import model.ebay.UserAccessTokenResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Desktop
import java.net.URI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


class EbayController() : KoinComponent {


    private val store: StorageController by inject<StorageController>()
    private val settings = store.settings
    private val backendUrl = "https://ebaylexofficeconnector.azurewebsites.net"


    fun openBrowser() {
        println("Open Browser Funktion")
        val desktop = Desktop.getDesktop()
        desktop.browse(
            URI.create(
                "https://auth.ebay.com/oauth2/authorize?client_id=MaxHandk-Lexoffic-PRD-8d4a886f5-3ad867b8&response_type=code&redirect_uri=Max_Handke-MaxHandk-Lexoff-nolrf&scope=https://api.ebay.com/oauth/api_scope+https://api.ebay.com/oauth/api_scope/sell.marketing.readonly+https://api.ebay.com/oauth/api_scope/sell.marketing+https://api.ebay.com/oauth/api_scope/sell.inventory.readonly+https://api.ebay.com/oauth/api_scope/sell.inventory+https://api.ebay.com/oauth/api_scope/sell.account.readonly+https://api.ebay.com/oauth/api_scope/sell.account+https://api.ebay.com/oauth/api_scope/sell.fulfillment.readonly+https://api.ebay.com/oauth/api_scope/sell.fulfillment+https://api.ebay.com/oauth/api_scope/sell.analytics.readonly+https://api.ebay.com/oauth/api_scope/sell.finances+https://api.ebay.com/oauth/api_scope/sell.payment.dispute+https://api.ebay.com/oauth/api_scope/commerce.identity.readonly+https://api.ebay.com/oauth/api_scope/commerce.notification.subscription+https://api.ebay.com/oauth/api_scope/commerce.notification.subscription.readonly"
            )
        )

    }


    suspend fun openServer(): UserAccessTokenResponse {
        var authToken: String
        var userToken = UserAccessTokenResponse()
        println("Jetzt sind wir in der openServer funktion")
        val server = embeddedServer(Netty, port = 51234, module = fun Application.() {
            routing {
                get("/") {
                    authToken = call.parameters["code"].toString()
                    println(authToken)
                    call.respondText("Authentification done. You can close this window and go back to the application")
                    val client = HttpClient(CIO) {

                    }
                    val response = client.get("$backendUrl/auth") {

                        url{
                            parameters.append("code",authToken)
                        }

                    }

                    userToken = Json.decodeFromString<UserAccessTokenResponse>(response.bodyAsText())

                }

            }
        }).start()

        //Solange der Token noch nicht da ist, warten
        while (userToken.access_token == "") {
            delay(1000)
        }
        server.stop()
        println("Usertoken: " + userToken.access_token)
        println("Refresh Token expires in: " + userToken.refresh_token_expires_in)
        settings.putString("access_token", userToken.access_token)
        settings.putString("refresh_token", userToken.refresh_token)
        settings.putInt("refresh_token_expires_in", userToken.refresh_token_expires_in)
        settings.putInt("expires_in", userToken.expires_in)
        settings.putString(
            "refresh_token_expires_at",
            LocalDateTime.now().plus(userToken.refresh_token_expires_in.toLong(), ChronoUnit.SECONDS).toString()
        )
        println("Refresh Token expires At: " + settings.getString("refresh_token_expires_at", "kein Datum"))
        return userToken
    }


    suspend fun getResponse(url: String, startDate: String?, endDate: String?): OrderResponse {
        val starttime=Clock.System.now()
        val client = HttpClient(CIO) {


            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Logging $message")
                    }
                }
                level = LogLevel.HEADERS
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(
                            settings.getStringOrNull("access_token").orEmpty(),
                            settings.getStringOrNull("refresh_token").orEmpty()
                        )


                    }
                    refreshTokens {

                        try {



                            val refreshTokenResponse = Json.decodeFromString<RefreshTokenResponse>(client.get("$backendUrl/refresh"){
                                url{
                                    parameters.append("refreshToken",oldTokens!!.refreshToken)
                                }
                                markAsRefreshTokenRequest()
                            }.bodyAsText())

                            println("Ausgeführt und Antwort erhalten $refreshTokenResponse")
                            settings.putString("access_token", refreshTokenResponse.access_token)
                            settings.putInt("expires_in", refreshTokenResponse.expires_in)
                        } catch (e: Exception) {
                            //Todo neuen Login Flow
                        }

                        BearerTokens(
                            settings.getStringOrNull("access_token").orEmpty(),
                            oldTokens?.refreshToken!!
                        )
                    }
                    //sendWithoutRequest { request -> request.url.toString() != "https://api.ebay.com/identity/v1/oauth2/token" }


                }
            }
        }
        val json = Json { ignoreUnknownKeys = true }
        var response = json.decodeFromString<OrderResponse>(client.get(url) {
            url {
                parameters.append("limit", "200")
                if (!startDate.isNullOrEmpty() && !endDate.isNullOrEmpty()) {
                    encodedParameters.append(
                        "filter",
                        "creationdate:%5B" + LocalDateTime.parse(
                            startDate + "T00:00:00",
                            DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss")
                        ).format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                        ) + ".." + LocalDateTime.parse(
                            endDate + "T00:00:00",
                            DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss")
                        ).format(
                            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

                        ) + "%5D"
                    )
                }

            }
        }.bodyAsText())



        if (!response.next.isNullOrEmpty()) {
            val count = (response.total!!.toInt() / 200) + 1
            val rest = response.total!!.toInt().mod(200)


            val resultList = mutableListOf<Deferred<OrderResponse>>()
            for (i in 1..count) {

                resultList.add(CoroutineScope(Dispatchers.IO).async {
                    if (i == count) {
                        json.decodeFromString<OrderResponse>(
                            client.get(response.href!!.dropLast(1).plus(rest)).bodyAsText()
                        )
                    } else {

                        json.decodeFromString<OrderResponse>(
                            client.get(response.href!!.dropLast(1).plus(200 * i)).bodyAsText()
                        )
                    }
                })

            }
            val awaitedList = resultList.awaitAll()
            awaitedList.forEach {
                response.orders.addAll(it.orders)
            }
            response.orders.sortBy {
                LocalDateTime.parse(it.creationDate, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
            }


        }
        val time = starttime-Clock.System.now()
        println("IT took me $time")
        return response

    }
}




