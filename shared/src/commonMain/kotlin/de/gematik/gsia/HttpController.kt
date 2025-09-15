/*
 * Copyright (Date see Readme), gematik GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ******
 *
 * For additional notes and disclaimer from gematik and in case of changes by gematik find details in the "Readme" file.
 */

package de.gematik.gsia

import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.GemSekIdpForbidden
import de.gematik.gsia.data.GemSekIdpTimeout
import de.gematik.gsia.data.GemSekIdpUnexpectedStatusCode
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.Url


class HttpController(private val x_auth: String) {

    private val client = PlatformHttpEngine

    private fun formatClaims(claims: List<String>): String {
        var out = ""
        for (claim in claims) {
            out += "$claim "
        }
        out.removeSuffix(" ")

        return out
    }

    suspend fun authorizationRequestGetClaims(redirectUrl: String, requestUri: String): List<String> {

        val url = Url("$redirectUrl?device_type=android&request_uri=$requestUri")  // get claims

        try {
            val response: HttpResponse = client.get(url) {
                header("X-Authorization", x_auth)
            }
            println("App-App-Flow Nr 6 TX: $url")

            if (debug) {
                println("Response Body: ${response.bodyAsText()}")
                println("Response Header: ${response.headers.entries()}")
                println("Response: $response")
            }

            when (response.status) {
                HttpStatusCode.OK -> Unit
                HttpStatusCode.Forbidden -> throw GemSekIdpForbidden()
                else -> throw GemSekIdpUnexpectedStatusCode(status = response.status.value, message = response.bodyAsText())
            }

            var claims: String = response.bodyAsText().dropWhile { it != '[' }
            claims = claims.replace("[","")
                .replace("]","")
                .replace("}","")
                .replace("\"","")

            return claims.split(",")
        } catch (_: SocketTimeoutException) {
            throw GemSekIdpTimeout()
        }
    }

    suspend fun authorizationRequestSendClaims(
        redirectUrl: String,
        requestUri: String,
        userId: String = "12345678",
        claims: List<String>
    ): String {
        println(claims)

        val url = Url("$redirectUrl?user_id=$userId&request_uri=$requestUri&selected_claims=${formatClaims(claims)}")

        try {
            val response: HttpResponse = client.get(url) {
                header("X-Authorization", x_auth)
            }
            println("App-App-Flow Nr 6b TX: $url")

            if (debug) {
                println("Response Body: ${response.bodyAsText()}")
                println("Response Header: ${response.headers.entries()}")
                println("Response: $response")
            }

            when (response.status) {
                HttpStatusCode.Found -> Unit
                HttpStatusCode.Forbidden -> throw GemSekIdpForbidden()
                else -> {
                    println("App-App Nr 6b RX: ${response.status.value}, ${response.bodyAsText()}")
                    throw GemSekIdpUnexpectedStatusCode(status = response.status.value, message = response.bodyAsText())
                }
            }

            val redirect = URLBuilder(response.headers["Location"] ?: "").build()

            redirect.parameters["code"] ?: throw Exception("No AuthCode Recevied from gemSekIdp")
            redirect.parameters["state"] ?: throw Exception("No State Recevied")

            println("App-App-Flow Nr 7 RX: $response")

            return redirect.toString()
        } catch (_: SocketTimeoutException) {
            throw GemSekIdpTimeout()
        }

    }
}