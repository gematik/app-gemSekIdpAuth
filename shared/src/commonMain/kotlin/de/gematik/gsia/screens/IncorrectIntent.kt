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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import de.gematik.gsia.Constants.debug
import io.ktor.http.Url

data class IncorrectIntent(val intent: Url, val context: Any?): Screen {
    @Composable
    override fun Content() {

        if (debug) {
            println("invalid intent: $intent")
        }

        return Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "This application is the authentication module gemSekIdp.",
                textAlign = TextAlign.Center
            )

            if (intent.parameters.contains("error_msg")) {
                Column(
                    modifier = Modifier.padding(10.dp)
                ) {
                    Text(
                        text = "An error occurred",
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        fontSize = 18.sp
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 5.dp),
                        text = intent.parameters["error_msg"].toString(),
                    )
                }
            }
        }
    }

}
