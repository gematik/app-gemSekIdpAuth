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

package de.gematik.gsia.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.Body
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.GSIAViewModel
import de.gematik.gsia.getAppRedirectUri
import de.gematik.gsia.getClaims
import io.ktor.http.Url


@Composable
fun Authentication() {

    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    if (debug) {
        println("Intent valid")
        println("${viewModel.intent.value}")
    }

    if (viewModel.intent.value.parameters.contains("error_msg")) {
        println("Error: " + viewModel.intent.value.parameters["error_msg"])
        println("possible reasons: no claim accepted, kvnr changed")
    }

    if (viewModel.claims.isEmpty()) {
        println("App-App-Flow Nr 5 RX: ${viewModel.intent.value}")
        getClaims()
    }

    Body(Url(getAppRedirectUri(viewModel.intent.value)), viewModel.intent.value.parameters["error_msg"])
}

