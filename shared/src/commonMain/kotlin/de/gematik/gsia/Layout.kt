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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.statement.HttpResponse
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Authorization Request incomming\nFrom: ${appRedirectUri.host}",
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
 * This function extends to a Row containing two Buttons "Accept" and "Decline" with which the user decides whether to accept the authentication or not.
 * @param intent Intent which the app receives at starting
 * @param context For starting another app we need context of the android app
 * @return Row(...)
 */
@Composable
fun AuthenticationButtons(intent: Url, context: Any) {
    val scope = rememberCoroutineScope()
    var authCode by remember { mutableStateOf("Loading") }
    var state by remember { mutableStateOf("Loading") }
    val (redirectUrl, requestUri, appRedirectUri) = decomposeIntent(intent)

    return Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.padding(bottom = 20.dp)
    ) {
        FilledButton("Accept", onClick = {
            scope.launch {
                try {
                    val response = HttpController().authorizationRequest(redirectUrl, requestUri)
                    authCode = response.first
                    state = response.second

                    println("App-App-Flow Nr 7 RX: authCode=$authCode, state=$state")

                    val deeplink: String = buildString {
                        append("https://$appRedirectUri?")
                        append("&code=$authCode")
                        append("&state=$state")
                    }

                    println("App-App-Flow Nr 8 TX: $deeplink")
                    executeDeeplink(context, deeplink)

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

/**
 * This function creates Layout that should be displayed when the app is started with an invalid or without an Intent.
 * The Layout offers a Button with which an Intent can be simulated. By clicking the Button the GSIA is called via Deeplink
 * @param context Localcontext.current of Android app
 * @param intent Intent with which the GSIA was opened
 */
@Composable
fun InvalidIntentActivity(context: Any, intent: Url) {

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

fun getAppRedirectUri(url: Url): String {
    return decomposeIntent(url).third
}

fun checkIntentCorrectness(url: Url?): Boolean {

    url ?: return false;

    if (!(url.parameters.contains("request_uri") && url.parameters.contains("client_id"))) {
        return false
    }
    return true
}

fun decomposeIntent(url: Url): Triple<String, String, String> {
    return Triple(
        first = url.toString().split("?")[0],
        second = url.parameters["request_uri"]!!,
        third = url.parameters["client_id"]!!)
}

@Composable
fun App(context: Any, intent: String) {

    println(intent)

    return Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colors.background
    ) {

        if (!checkIntentCorrectness(Url(intent))) {
            println("Intent invalid")

            InvalidIntentActivity(context, Url(intent))
        } else {
            println("Intent valid")
            println("App-App-Flow Nr 5 RX: $intent")

            // DrawGematikLogo()
            Body(Url(getAppRedirectUri(Url(intent))), Url(intent).parameters["error_msg"])

            AuthenticationButtons(Url(intent), context)
        }
    }
}