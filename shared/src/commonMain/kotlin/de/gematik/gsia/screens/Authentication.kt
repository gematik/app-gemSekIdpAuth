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
import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.core.screen.Screen
import com.russhwolf.settings.Settings
import de.gematik.gsia.Body
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.StateData
import de.gematik.gsia.getAppRedirectUri
import de.gematik.gsia.getClaims
import io.ktor.http.Url

class Authentication(
    val intent: Url,
    val context: Any,
    val data: MutableState<StateData>,
    val settings: Settings
): Screen {
    @Composable
    override fun Content() {

        if (debug) {
            println("Intent valid")
        }

        if (data.value.claims.isEmpty()) {
            println("App-App-Flow Nr 5 RX: $intent")
            getClaims(intent, context, data, settings)
        }

        Body(intent, context, Url(getAppRedirectUri(intent)), intent.parameters["error_msg"], data, settings)
    }
}
