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

import io.ktor.http.Url

fun getAppRedirectUri(url: Url): String {
    return decomposeIntent(url).third
}

fun checkIntentCorrectness(url: Url?): Boolean {

    url ?: return false;

    return url.parameters.contains("request_uri") && url.parameters.contains("client_id")
}

fun decomposeIntent(url: Url): Triple<String, String, String> {
    return Triple(
        first = url.toString().split("?")[0],
        second = url.parameters["request_uri"]!!,
        third = url.parameters["client_id"]!!)
}