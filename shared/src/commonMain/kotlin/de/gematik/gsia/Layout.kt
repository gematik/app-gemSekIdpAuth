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
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
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

/**
 * This extends to a Column displaying the name of the App that called the GSIA App
 */
@Composable
fun Body(appRedirectUri: Url, errorMsg: String? = null) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier
                .padding(5.dp)
                .border(BorderStroke(1.dp, Color.Black))
                .padding(5.dp),
            text = "Authorization Request incomming.\nFrom: ${appRedirectUri.host}",
            fontFamily = FontFamily.SansSerif,
            fontSize = 20.sp,
        )
        if (errorMsg != null) {
            Text(
                text = errorMsg,
                modifier = Modifier.padding(20.dp)
            )
        }
    }

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
fun AuthenticationButtons(intent: Url, context: Any) {
    val scope = rememberCoroutineScope()
    val (redirectUrl, requestUri, _) = decomposeIntent(intent)
    var kvnr by remember { mutableStateOf(TextFieldValue("X110400607")) }

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
                        .clickable { kvnr = TextFieldValue(insured.kvnr) }
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
            value = kvnr,
            onValueChange = {
                kvnr = it
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
            FilledButton("Accept", onClick = {
                scope.launch {
                    try {
                        val response = HttpController().authorizationRequest(
                            redirectUrl,
                            requestUri,
                            kvnr.text
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

/**
 * This function creates Layout that should be displayed when the app is started with an invalid or
 * without an Intent.
 * @param intent Intent with which the GSIA was opened
 */
@Composable
fun InvalidIntentActivity(intent: Url) {

    println("invalid intent: $intent")

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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun DrawGematikLogo() {
    return Column(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 20.dp)
    ) {
        Image(
            painterResource("gematik.xml"),
            contentDescription = "Gematik Logo"
        )
    }
}

@Composable
fun App(context: Any, intent: String) {

    return Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        if (!checkIntentCorrectness(Url(intent))) {
            println("Intent invalid")

            InvalidIntentActivity(Url(intent))
        } else {
            println("Intent valid")
            println("App-App-Flow Nr 5 RX: $intent")

            // DrawGematikLogo()
            Body(Url(getAppRedirectUri(Url(intent))), Url(intent).parameters["error_msg"])

            AuthenticationButtons(Url(intent), context)
        }
    }
}
