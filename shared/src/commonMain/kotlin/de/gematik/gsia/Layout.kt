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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.data.GSIAViewModel
import de.gematik.gsia.data.StateData
import de.gematik.gsia.screens.Authentication
import de.gematik.gsia.screens.OpenedWithoutDeeplink
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
fun FilledButton(label: String, onClick: () -> Unit = ({ }), modifier: Modifier, fontSize: TextUnit = 20.sp) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1, 14, 82, 255)),
        onClick = { onClick() })
    {
        Text(
            text = label,
            color = Color(0, 255, 101, 255),
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RequestInfo(intent: Url) {
    /*
    Text(
        modifier = Modifier
            .padding(5.dp)
            .border(BorderStroke(1.dp, Color.Black))
            .padding(5.dp),
        text = "Authorization Request incomming.\nFrom: ${appRedirectUri.host}",
        fontFamily = FontFamily.SansSerif,
        fontSize = 20.sp,
    )
     */

    val (redirectUrl, requestUri, clientId) = decomposeIntent(intent)

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
        Row(

        ) {
            Column {
                Text("sekIDP")
                Text("RP")
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(redirectUrl, maxLines = 1)
                if (intent.toString().contains("client_id"))
                    Text(clientId, maxLines = 1)
                else
                    Text("client_id was not part of response")
            }
        }
    }
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
private fun ListClaims(modifier: Modifier) {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    val selectedClaims = remember { mutableStateMapOf<String, Boolean>() }

    viewModel.claims.forEach { (k, v) ->
        selectedClaims[k] = v
    }

    Column (
        modifier = modifier.then(Modifier.verticalScroll(rememberScrollState())),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("available Claims", fontWeight = FontWeight.Bold, fontSize = 20.sp)

        Column {
            if (viewModel.claims.size < 2)
                Text(modifier = Modifier.padding(vertical = 10.dp, horizontal = 10.dp),
                    text = "Empty list of claims might be caused by empty/wrong X-Auth Key")
            selectedClaims.forEach { (claim, value) ->
                Row(
                    modifier = Modifier
                        .clickable {
                            selectedClaims[claim] = !selectedClaims[claim]!!
                            if (debug) {
                                println(viewModel.claims)
                                println("$claim ${selectedClaims[claim]}")
                            }
                            viewModel.setSelectedClaims(selectedClaims)
                        }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .fillMaxWidth()
                        .height(40.dp)
                        .background(color = if (value) Color.Green.copy(alpha = 0.5f) else Color.Red.copy(alpha = 0.5f))
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        fontSize = 15.sp,
                        text = claim,
                    )
                    Text(
                        fontSize = 15.sp,
                        text = if (value) "granted" else "denied"
                    )
                }
                // Divider(modifier = Modifier.padding(vertical = 5.dp))
            }
        }
    }
}

@Composable
private fun TextFieldKVNR() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    TextField(
        value = viewModel.kvnr.value,
        onValueChange = {
            viewModel.setKVNR(it)
        },
        label = { Text(text = "KVNR") },
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth()
    )
}

@Composable
fun SetAuthKey() {

    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    var authkey by remember { mutableStateOf(TextFieldValue(viewModel.settings.value["auth_key", ""])) }

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
                .size(width = 125.dp, height = 60.dp),
            onClick = {
                Toast(viewModel.context.value, "Set X-Auth Key. Change takes effect at next authentication")
                viewModel.settings.value.set("auth_key", authkey.text)
                if (debug) {
                    println("Auth Key: " + viewModel.settings.value["auth_key", ""])
                }
            },
            fontSize = 17.sp,
        )
    }
}

@Composable
fun SetAuthKeyMax() {

    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }
    var authkey by remember { mutableStateOf(TextFieldValue(viewModel.settings.value["auth_key", ""])) }

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
                Toast(viewModel.context.value, "Set X-Auth Key. Change takes effect at next authentication")
                viewModel.settings.value.set("auth_key", authkey.text)
                if (debug) {
                    println("Auth Key: " + viewModel.settings.value["auth_key", ""])
                }
            },
            fontSize = 17.sp,
        )
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
fun BtnAuthentication() {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    val scope = rememberCoroutineScope()
    val (redirectUrl, requestUri, _) = decomposeIntent(viewModel.intent.value)
    var authkey by remember { mutableStateOf(TextFieldValue(viewModel.settings.value["auth_key", ""])) }

    return Column(
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

                if (viewModel.settings.value["auth_key", ""] == "") {
                    Toast(viewModel.context.value, "You need to set X-Auth Key!")
                } else {
                    scope.launch {
                        try {
                            val response = HttpController(viewModel.settings.value["auth_key", ""]).authorizationRequestSendClaims(
                                redirectUrl,
                                requestUri,
                                viewModel.kvnr.value,
                                viewModel.claims.filter { it.value }.map { it.key }
                            )

                            println("used kvnr: ${viewModel.kvnr.value}")
                            println("App-App-Flow Nr 8 TX: $response")
                            executeDeeplink(viewModel.context.value, response)

                        } catch (e: Exception) {
                            executeDeeplink(viewModel.context.value, "${viewModel.intent.value}&error_msg=$e")
                        }
                    }
                }
            })
            FilledButton("Decline", onClick = {
                // Behaviour for declining authentication
            })
        }
    }
}

/*
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
*/

/**
 * This extends to a Column displaying the name of the App that called the GSIA App
 */
@Composable
fun Body(
    appRedirectUri: Url,
    errorMsg: String?,
) {
    val viewModel: GSIAViewModel = viewModel { GSIAViewModel() }

    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.height(100.dp)
        ) {
            RequestInfo(viewModel.intent.value)
            DisplayError(errorMsg)
        }

        ListClaims(modifier = Modifier.fillMaxHeight().weight(1f))

        Column(
            modifier = Modifier.height(320.dp)
        ){
            SetAuthKey()
            TextFieldKVNR()

            BtnAuthentication()
        }
    }
}

@Composable
fun App(context: Any, intent: String) {

    val viewModel : GSIAViewModel = viewModel { GSIAViewModel() }

    val settings: Settings = Settings()
    val (_, _, userId) = decomposeIntent(Url(intent))

    viewModel.setIntent(Url(intent))
    viewModel.setContext(context)

    if (intent.contains("user_id"))
        viewModel.setKVNR(userId)
    else
        viewModel.setKVNR("X123456784")

    if (!checkIntentCorrectness(Url(intent))) {
        println("incorrect intent")
        println(intent)
        OpenedWithoutDeeplink()
    } else {
        Authentication()
    }
}
