/*
 *  Copyright 2023 gematik GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package de.gematik.gsia

import io.ktor.client.HttpClient
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.http.URLBuilder
import io.ktor.http.Url


class HttpController {
    private val X_AUTH_HEADER = "hidden/hidden"  // enter X_AUTH_HEADER here

    private val client = HttpClient() {
        followRedirects = false
    }

    /**
     * sends Authorization Request to sektoraler IDP (GSI). (see App-App Flow 6)
     * @param redirectUrl Url to the sektoraler IDP
     * @param requestUri Identifier for sektoraler IDP to map our authorization request to the first one initialized by the Anwendungsfrontend (Callee App)
     * @param userId userId is optional because the userId is constant for GSI
     * @return AUTH_CODE (can later be redeemed for an ACCESS_TOKEN)
     */
    suspend fun authorizationRequest(redirectUrl: String, requestUri: String, userId: String = "12345678"): Pair<String, String> {

        val url = Url("$redirectUrl?user_id=$userId&request_uri=$requestUri")
        println("App-App-Flow Nr 6 TX: $url")
        val response: HttpResponse = client.get(url) {
                header("X-Authorization", X_AUTH_HEADER)
        }

        val redirect = URLBuilder(response.headers["Location"] ?: "").build()

        redirect.parameters["code"] ?: throw Exception("No AuthCode Recevied")
        redirect.parameters["state"] ?: throw Exception("No State Recevied")

        return Pair(redirect.parameters["code"].toString(), redirect.parameters["state"].toString())
    }

    suspend fun sendRequest(request: Url): HttpResponse {
        val response = client.get(request) {
                header("X-Authorization", X_AUTH_HEADER)
        }

        return response
    }
}