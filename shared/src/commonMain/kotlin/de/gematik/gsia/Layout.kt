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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.StateData
import de.gematik.gsia.screens.Authentication
import de.gematik.gsia.screens.IncorrectIntent
import de.gematik.gsia.screens.SelectInsuredPerson
import io.ktor.http.Url
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource


@Composable
fun FilledButton(label: String, onClick: () -> Unit = ({ })) {
    Button(
        modifier = Modifier
            .height(50.dp)
            .width(150.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1, 14, 82, 255)),
        onClick = { onClick() })
    {
        Text(
            text = label,
            color = Color(0, 255, 101, 255),
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FilledButton(label: String, onClick: () -> Unit = ({ }), modifier: Modifier) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1, 14, 82, 255)),
        onClick = { onClick() })
    {
        Text(
            text = label,
            color = Color(0, 255, 101, 255),
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RequestInfo(appRedirectUri: Url) {
    Text(
        modifier = Modifier
            .padding(5.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(5.dp),
        text = "Authorization Request incomming.\nFrom: ${appRedirectUri.host}",
        fontFamily = FontFamily.SansSerif,
        fontSize = 20.sp,
    )
}

@Composable
private fun DisplayError(errorMsg: String?) {
    if (errorMsg != null) {
        Text(
            text = errorMsg,
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun ListClaims(data: MutableState<StateData>) {
    val selectedClaims = remember { mutableStateMapOf<String, Boolean>() }

    data.value.claims.forEach { (key, value) ->
        selectedClaims[key] = value
    }

    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("available Claims", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Column(
            modifier = Modifier
        ) {
            selectedClaims.forEach { (claim, value) ->
                Row(
                    modifier = Modifier
                        .clickable {
                            selectedClaims[claim] = !selectedClaims[claim]!!
                            if (Constants.debug) {
                                println(data.value.claims)
                                println("$claim ${selectedClaims[claim]}")
                            }
                            data.value.claims = selectedClaims
                        }
                        .padding(12.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(color = if (value) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f))
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        fontSize = 4.em,
                        text = claim,
                    )
                    Text(
                        fontSize = 4.em,
                        text = if (value) "granted" else "denied"
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

@Composable
private fun TextFieldKVNR(data: MutableState<StateData>) {
    TextField(
        value = data.value.kvnr,
        onValueChange = {
            data.value = data.value.copy(
                kvnr = it
            )
        },
        label = { Text(text = "KVNR") },
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    )
}

@Composable
fun SetAuthKey(settings: Settings) {

    var authkey by remember { mutableStateOf(TextFieldValue(settings["auth_key", ""])) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalArrangement = Arrangement.Start,
    ) {
        TextField(
            value = authkey,
            onValueChange = {
                authkey = it
            },
            singleLine = true,
            label = { Text(text = "Auth Key") },
            modifier = Modifier
                .height(60.dp)
                .width(225.dp)
        )
        Spacer(
            modifier = Modifier.width(10.dp)
        )
        FilledButton(
            "Set AuthKey",
            modifier = Modifier
                .size(width = 125.dp, height = 60.dp),
            onClick = {
                settings.set("auth_key", authkey.text)
                if (debug) {
                    println("Auth Key: " + settings["auth_key", ""])
                }
            },
        )
    }
}

@Composable
private fun BtnNavToSelectInsuredPeople(intent: Url, context: Any, data: MutableState<StateData>) {
    val navigator = LocalNavigator.currentOrThrow

    FilledButton("Set InsuredPerson",
        onClick = {
            navigator.push(item = SelectInsuredPerson(intent, context, data))
        },
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(50.dp)
    )
}

/**
 * This function provides a list of test identities. A kvnr can be chosen from the list but can also
 * be entered manually via the textbox. The given kvnr is used for the following authentication
 * process.
 *
 * @param intent Intent which the app receives at starting
 * @param context For starting another app we need context of the android app
 * @return Column(...)
 */
@Composable
fun BtnAuthentication(
    intent: Url,
    context: Any,
    claims: List<String>,
    kvnr: String,
    settings: Settings
) {
    val scope = rememberCoroutineScope()
    val (redirectUrl, requestUri, _) = decomposeIntent(intent)
    var authkey by remember { mutableStateOf(TextFieldValue(settings["auth_key", ""])) }

    return Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 20.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier
                .padding(bottom = 20.dp)
                .fillMaxWidth()
        ) {
            FilledButton("Accept", onClick = {

                scope.launch {
                    try {
                        val response = HttpController(settings["auth_key", ""]).authorizationRequestSendClaims(
                            redirectUrl,
                            requestUri,
                            kvnr,
                            claims
                        )

                        println("App-App-Flow Nr 8 TX: $response")
                        executeDeeplink(context, response)

                    } catch (e: Exception) {
                        executeDeeplink(context, "$intent&error_msg=$e")
                    }
                }
            })
            FilledButton("Decline", onClick = {
                // Behaviour for declining authentication
            })
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun DrawGematikLogo() {
    return Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        Image(
            painterResource("gematik.xml"),
            contentDescription = "Gematik Logo"
        )
    }
}

/**
 * This extends to a Column displaying the name of the App that called the GSIA App
 */
@Composable
fun Body(
    intent: Url,
    context: Any,
    appRedirectUri: Url,
    errorMsg: String?,
    data: MutableState<StateData>,
    settings: Settings
) {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column {
            RequestInfo(appRedirectUri)
            DisplayError(errorMsg)
        }

        ListClaims(data)

        Column {
            SetAuthKey(settings)
            BtnNavToSelectInsuredPeople(intent, context, data)
            TextFieldKVNR(data)
            BtnAuthentication(intent, context, data.value.claims.filter { it.value }.map { it.key }, data.value.kvnr, settings)
        }
    }
}

@Composable
fun App(context: Any, intent: String) {

    val settings: Settings = Settings()
    val data = remember { mutableStateOf(StateData("X123456784", mutableMapOf())) }

    if (!checkIntentCorrectness(Url(intent))) {
        Navigator(
            screen = IncorrectIntent(Url(intent), context)
        )
    } else {
        Navigator(
            screen = Authentication(Url(intent), context, data, settings)
        )
    }
}
