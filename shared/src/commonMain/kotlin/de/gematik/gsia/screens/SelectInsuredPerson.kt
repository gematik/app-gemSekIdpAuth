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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.gematik.gsia.FilledButton
import de.gematik.gsia.data.StateData
import de.gematik.gsia.insuredPersons
import io.ktor.http.Url

data class SelectInsuredPerson(
    val intent: Url,
    val context: Any,
    val data: MutableState<StateData>
) : Screen {

    @Composable
    override fun Content() {
        var kvnrLocal by remember { mutableStateOf(TextFieldValue(data.value.kvnr)) }
        val navigator = LocalNavigator.currentOrThrow

        return Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxHeight()
        ) {
            Text(
                fontSize = 20.sp,
                text = "Select a test identity"
            )
            Spacer(
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Column(
                modifier = Modifier
                    // .size(width = 100.dp, height = 400.dp)
                    .fillMaxHeight(0.75f)
                    .verticalScroll(rememberScrollState())
            ) {
                insuredPersons.forEach { insured ->
                    Column(
                        modifier = Modifier
                            .clickable {
                                kvnrLocal = TextFieldValue(insured.kvnr)
                            }
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            fontSize = 6.em,
                            text = insured.name
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            modifier = Modifier.padding(horizontal = 10.dp),
                            fontSize = 4.em,
                            color = Color.Gray,
                            text = insured.kvnr
                        )
                    }
                    Divider(modifier = Modifier.padding(vertical = 5.dp))
                }
            }

            TextField(
                value = kvnrLocal,
                onValueChange = {
                    kvnrLocal = it
                    data.value = data.value.copy(
                        kvnr = it.toString()
                    )
                },
                label = { Text(text = "KVNR") },
                placeholder = { Text(text = "X123456784") },
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
            ) {
                FilledButton("Set KVNR", onClick = {
                    data.value = data.value.copy(
                        kvnr = kvnrLocal.text
                    )
                    navigator.pop()
                })
                FilledButton("Abort", onClick = {
                    navigator.pop()
                    // Behaviour for declining authentication
                })
            }
        }
    }
}