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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.get
import de.gematik.gsia.data.GSIAViewModel
import io.ktor.http.Url
import kotlinx.coroutines.launch

fun getAppRedirectUri(url: Url): String {
    return decomposeIntent(url).third
}

fun checkIntentCorrectness(url: Url?): Boolean {

    url ?: return false;

    return url.parameters.contains("request_uri") && (url.parameters.contains("client_id") || url.parameters.contains("user_id"))
}

fun decomposeIntent(url: Url): Triple<String, String, String> {
    return Triple(
        first = url.toString().split("?")[0],
        second = if (url.parameters.contains("request_uri")) url.parameters["request_uri"]!! else "",
        third = if (url.parameters.contains("client_id"))
            url.parameters["client_id"]!!
        else if (url.parameters.contains("user_id"))
            url.parameters["user_id"]!!
        else
            ""
        )
}

@Composable
fun getClaims() {

    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    val scope = rememberCoroutineScope()
    val (redirectUrl, requestUri, _) = decomposeIntent(viewModel.intent.value)


    scope.launch {
        try {
            val claims = HttpController(viewModel.settings.value["auth_key", ""]).authorizationRequestGetClaims(
                redirectUrl,
                requestUri
            ).associateWith { true }.toMutableMap()

            viewModel.setSelectedClaims(claims)

            println("App-App-Flow Nr 6a RX: (${viewModel.claims.size} Claims received) ${viewModel.claims}")
        } catch (e: Exception) {
            viewModel.resetClaims()
            // throw e
            // executeDeeplink(context, "$intent&error_msg=$e")
        }
    }
}