package com.ktor.security


import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*


import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.delay
import kotlinx.serialization.json.*
import model.ebay.RefreshTokenResponse
import model.ebay.UserAccessTokenResponse
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import storage.kvstore
import java.awt.Desktop
import java.net.URI
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


class EbayAuthController() : KoinComponent {


    val store: kvstore by inject<kvstore>()
    val settings = store.settings


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
        val server = embeddedServer(Netty, port = 5000, module = fun Application.() {
            routing {
                get("/") {
                    authToken = call.parameters["code"].toString()
                    println(authToken)
                    call.respondText("Authentifizierung erfolgreich")
                    val client = HttpClient(CIO) {

                    }
                    val response = client.request("https://api.ebay.com/identity/v1/oauth2/token") {
                        method = HttpMethod.Post
                        headers {
                            append(HttpHeaders.ContentType, "application/x-www-form-urlencoded")
                            append(
                                HttpHeaders.Authorization,
                                "Basic TWF4SGFuZGstTGV4b2ZmaWMtUFJELThkNGE4ODZmNS0zYWQ4NjdiODpQUkQtZDRhODg2ZjVkMjJjLWEzZjktNGVkYS1hYWY4LTgzN2Q="
                            )
                        }
                        contentType(ContentType.Application.FormUrlEncoded)
                        setBody(FormDataContent(Parameters.build {
                            append("grant_type", "authorization_code")
                            append("code", authToken)
                            append("redirect_uri", "Max_Handke-MaxHandk-Lexoff-nolrf")

                        }
                        ))

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


    suspend fun getResponse(url: String): HttpResponse {
        val client = HttpClient(CIO) {



            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Logging " + message)
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


                        val refreshTokenResponse = Json.decodeFromString<RefreshTokenResponse>(client.submitForm(
                            url = "https://api.ebay.com/identity/v1/oauth2/token",
                            formParameters = parameters {
                                append("grant_type", "refresh_token")
                                append("refresh_token", oldTokens?.refreshToken ?: "")


                            }) {
                            headers {
                                append(
                                    HttpHeaders.Authorization,
                                    "Basic TWF4SGFuZGstTGV4b2ZmaWMtUFJELThkNGE4ODZmNS0zYWQ4NjdiODpQUkQtZDRhODg2ZjVkMjJjLWEzZjktNGVkYS1hYWY4LTgzN2Q="
                                )
                            }
                            markAsRefreshTokenRequest()
                        }.bodyAsText())

                            println("Ausgeführt und Antwort erhalten $refreshTokenResponse")
                            settings.putString("access_token", refreshTokenResponse.access_token)
                            settings.putInt("expires_in", refreshTokenResponse.expires_in)
                        }
                        catch (e: Exception){
                            //Todo neuen Login Flow
                        }

                        BearerTokens(
                            settings.getStringOrNull("access_token").orEmpty(),
                            oldTokens?.refreshToken!!
                        )
                    }
                    sendWithoutRequest { request -> request.url.toString() != "https://api.ebay.com/identity/v1/oauth2/token" }


                }
            }
        }
        val response = client.request(url) {
            method = HttpMethod.Get
        }

        return response

    }

}


