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

package de.gematik.gsia.data

import io.ktor.http.Url

class GSIAIntentStep5(val string: String) {

    val intent: Url = Url(string)
    val location: String = intent.toString().split("?").first()
    val user_id: String = intent.parameters["user_id"] ?: ""
    val client_id: String = intent.parameters["client_id"] ?: ""
    val request_uri: String = intent.parameters["request_uri"] ?: ""

    init {
        println("Intent: $intent")
        if (string != "") {
            require(intent.parameters.contains("request_uri")) { "No request_uri in Intent found!" }
            require(intent.parameters.contains("client_id")) { "No client_id in Intent found!" } // TODO in which cases is there a user_id instead of client_id?
        }
    }
}