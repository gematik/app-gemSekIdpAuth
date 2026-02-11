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

package de.gematik.gsia.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.data.GSIAViewModel

@Composable
fun DefaultScreen() {
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("GSIA wurde nicht durch korrekten Deeplink gestartet. Authentisierung nicht möglich!")
            Spacer(Modifier.height(20.dp))
            Text("Hier kann der X-Auth Key für folgende Authentisierungen gesetzt werden")

            SetAuthKeyMax()
            Spacer(Modifier.height(20.dp))
            TextFieldKVNR()
            Spacer(Modifier.height(10.dp))
            SetAutoAuthenticate()
        }

        Text("v 2.1.29", Modifier.padding(5.dp))
    }
}


@Composable
fun SetAuthKeyMax() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    var authkey by remember { mutableStateOf(TextFieldValue(viewModel.settings.get("auth_key", ""))) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        OutlinedTextField(
            value = authkey,
            onValueChange = {
                authkey = it
            },
            minLines = 4,
            maxLines = 4,
            label = { Text(text = "X-Auth Key") },
            modifier = Modifier
                .height(120.dp)
                .width(225.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        FilledButton(
            "Set X-AuthKey",
            modifier = Modifier
                .size(width = 125.dp, height = 120.dp),
            onClick = {
                viewModel.setXAuthKeyInAuthentication(authkey.text, false)
            },
            fontSize = 17.sp,
        )
    }
}