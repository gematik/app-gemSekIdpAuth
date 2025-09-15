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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.GSIAIntentStep5
import de.gematik.gsia.data.GSIAViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch


@Composable
fun AuthenticationScreen() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    if (debug) {
        println("Intent valid")
        println("${viewModel.intent}")
    }

    if (viewModel.claims.isEmpty()) {
        println("App-App-Flow Nr 5 RX: ${viewModel.intent}")
        CoroutineScope(Dispatchers.IO).launch {
            viewModel.viewmodelGetClaims()
        }
    }


    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.height(100.dp)
        ) {
            RequestInfo(viewModel.intent)
        }

        ListClaims(modifier = Modifier.fillMaxHeight().weight(1f))

        Column(
            modifier = Modifier.height(320.dp),
            verticalArrangement = Arrangement.Bottom
        ){
            SetAutoAuthenticate()
            SetAuthKey()
            TextFieldKVNR()
            BtnAuthentication()
        }
    }
}


@Composable
private fun RequestInfo(intent: GSIAIntentStep5) {

    Column(
        modifier = Modifier
            .padding(5.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(5.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "Authorization Request!",
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp
        )
        Row {
            Column {
                Text("sekIDP")
                Text("RP")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(intent.location, maxLines = 1)
                Text(intent.client_id, maxLines = 1)
            }
        }
    }
}

@Composable
private fun ListClaims(modifier: Modifier) {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    val claims by remember { mutableStateOf(viewModel.claims.keys) }
    val isLoading by viewModel.isLoading.collectAsState(false)

    Column(
        modifier = modifier.then(Modifier.verticalScroll(rememberScrollState())),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("available Claims", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        if (isLoading) {
            Spacer(Modifier.height(40.dp))
            CircularProgressIndicator()
        } else {
            Column {
                if (viewModel.claims.isEmpty())
                    Text(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                        text = "Empty list of claims might be caused by empty/wrong X-Auth Key"
                    )
                for (claim in claims) {
                    RequestedClaimCard(claim)
                }
            }
        }
    }
}

@Composable
fun SetAutoAuthenticate() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Auto Authenticate")
        Switch(
            modifier = Modifier.padding(start = 5.dp, end = 20.dp),
            checked = viewModel.autoAuthenticate.value,
            onCheckedChange = { it ->
                viewModel.setAutoAuthenticate(it)
            }
        )
    }
}

@Composable
fun SetAuthKey() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    var authkey by remember { mutableStateOf(TextFieldValue(viewModel.settings.get("auth_key", ""))) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        OutlinedTextField(
            value = authkey,
            onValueChange = {
                authkey = it
            },
            singleLine = true,
            label = { Text(text = "X-Auth Key") },
            modifier = Modifier
                .height(60.dp)
                .width(225.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        FilledButton(
            "Set X-AuthKey",
            modifier = Modifier
                .size(width = 125.dp, height = 52.dp),
            onClick = {
                viewModel.setXAuthKeyInAuthentication(authkey.text.trim(), true)
            },
            fontSize = 16.sp,
        )
    }
}

@Composable
fun TextFieldKVNR() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    OutlinedTextField(
        value = viewModel.kvnr,
        onValueChange = {
            viewModel.setKVNR(it)
        },
        label = { Text(text = "KVNR") },
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
            .height(60.dp),
        singleLine = true
    )
}

@Composable
fun BtnAuthentication() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            FilledButton("Accept", onClick = {
                viewModel.acceptAuthentication()
            },
                modifier = Modifier.height(50.dp).fillMaxWidth().padding(horizontal = 20.dp)
            )
            /*
            FilledButton("Decline", onClick = {
                viewModel.declineAuthentication()
            })
             */
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RequestedClaimCard(claim: String) {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .padding(10.dp)
            .height(70.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = 5.dp,
        onClick = {
            viewModel.toggleClaim(claim)
        },
        content = {
            Row(
                modifier = Modifier.fillMaxSize()
                    .padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(claim, fontSize = 17.sp)
                Checkbox(
                    checked = viewModel.claims[claim]!!,
                    onCheckedChange = {
                        viewModel.toggleClaim(claim)
                    }
                )
            }
        }
    )
}