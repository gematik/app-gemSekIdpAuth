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

package de.gematik.gsia.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.gematik.gsia.Constants.debug
import de.gematik.gsia.HttpController
import de.gematik.gsia.executeDeeplink
import de.gematik.gsia.ui.DialogMessage
import de.gematik.gsia.ui.MessageType
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EventClaims(val claims: List<String>)

class GSIAViewModel : ViewModel() {

    var settings by mutableStateOf<SharedPreference>(RusshwolfSharedPreferences())
        private set

    var kvnr by mutableStateOf(settings.get("kvnr") ?: "")
        private set

    var claims = mutableStateMapOf<String, Boolean>()
        private set

    var autoAuthenticate = mutableStateOf(false)

    private val _event: MutableSharedFlow<EventClaims> = MutableSharedFlow()
    val event: Flow<EventClaims> = _event

    private val _errorHandlerFlow: MutableSharedFlow<DialogMessage> = MutableSharedFlow()
    val errorHandlerFlow: Flow<DialogMessage> = _errorHandlerFlow

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var context by mutableStateOf<Any?>(null)

    var intent by mutableStateOf<GSIAIntentStep5>(GSIAIntentStep5(""))

    private val _recentKVNRs: MutableStateFlow<List<String>> = MutableStateFlow(getRecentKVNRs())
    val recentKVNRs: StateFlow<List<String>> = _recentKVNRs

    init {
        viewModelScope.launch {
            event.collect { event ->
                setSelectedClaims(event.claims.associateWith { true }.toMutableMap())
            }
        }

        if (settings.get("auto-authenticate") == null) {
            settings.set("auto-authenticate", "false")
        }
        if (settings.get("auto-authenticate") != null) {
            when (settings.get("auto-authenticate")) {
                "true" -> autoAuthenticate.value = true
                "false" -> autoAuthenticate.value = false
            }
        }
    }

    fun getRecentKVNRs(): List<String> {
        val kvnrs: List<String> = listOf(
            settings.get("kvnr1", ""),
            settings.get("kvnr2", ""),
            settings.get("kvnr3", ""),
            settings.get("kvnr4", ""),
            settings.get("kvnr5", "")
        )

        return if (kvnrs.all { it.isEmpty() })
            listOf(kvnr)
        else
            kvnrs.filter { it.isNotEmpty() }
    }

    fun setKVNR(kvnr: String) {
        settings.set("kvnr", kvnr)

        if (kvnr.length == 10) {
            if (getRecentKVNRs().contains(kvnr)) {
                for (i in 2..5) {
                    if (settings.get("kvnr$i") == kvnr) {
                        for (j in i downTo 2) {
                            settings.set("kvnr${j}", settings.get("kvnr${j - 1}", ""))
                        }
                        settings.set("kvnr1", kvnr)
                    }
                }
            } else {
                if (settings.get("kvnr1").isNullOrEmpty())
                    settings.set("kvnr1", kvnr)

                for (i in 4 downTo 1) {
                    settings.set("kvnr${i+1}", settings.get("kvnr${i}", ""))
                }
                settings.set("kvnr1", settings.get("kvnr", ""))
            }
        }

        this.kvnr = kvnr
        this._recentKVNRs.value = getRecentKVNRs()
    }

    fun setSelectedClaims(selectedClaims: Map<String, Boolean>) {
        selectedClaims.forEach { (k, v) ->
            claims[k] = v
        }
    }

    fun setIntent(intent: String) {
        try {
            this.intent = GSIAIntentStep5(intent)
        } catch (e: Exception) {
            println("incorrect intent")
            println(intent)
            CoroutineScope(Dispatchers.IO).launch {
                _errorHandlerFlow.emit(
                    DialogMessage(
                    e.message ?: "unspecified exception raised! See logs for further information",
                        MessageType.ERROR
                    )
                )
            }
        }

        if (this.intent.user_id.isNotEmpty())
            setKVNR(this.intent.user_id)
    }

    fun setAutoAuthenticate(boolean: Boolean) {
        autoAuthenticate.value = boolean
        settings.set("auto-authenticate", boolean.toString())
    }

    fun toggleClaim(claim: String) {
        claims[claim] = claims[claim]!!.not()
    }

    fun viewmodelGetClaims() {

        viewModelScope.launch {
            _isLoading.update { true }

            try {
                val claims = HttpController(settings.get("auth_key", "")).authorizationRequestGetClaims(
                    intent.location,
                    intent.request_uri,
                ).associateWith { true }.toMutableMap()

                _event.emit(EventClaims(claims.keys.toList()))

                println("App-App-Flow Nr 6a RX: (${claims.size} Claims received)")

            } catch (_: SocketTimeoutException) {
                _errorHandlerFlow.emit(DialogMessage("Connection timeout. Check your Internet Connection.", MessageType.ERROR))
                println("Can't connect to gemSekIdp. Can't retrieve claims. Check your Internet Connection.")
            } catch (_: GemSekIdpForbidden) {
                _errorHandlerFlow.emit(DialogMessage("Http Code: 302. Check your X-Auth Key.", MessageType.ERROR))
                println("Can't get Claims from gemSekIdp due to wrong X-Auth Key. Please enter correct X-Auth Key in GSIA. In Case you haven't got one, contact gematik.")
            } catch (e: GemSekIdpUnexpectedStatusCode) {
                _errorHandlerFlow.emit(DialogMessage("Unexpected HTTP Response Code: ${e.status}", MessageType.ERROR))
                println("gemSekIdp responded with an unexpected HTTP Code: ${e.status}, ${e.message}")
            } catch (e: Exception) {
                println("!Unexpected Exception occurred: ${e.message}\n${e.cause}")
                _errorHandlerFlow.emit(DialogMessage("Unexpected Error. Look at logs to get further information.", MessageType.ERROR))
            } finally {
                _isLoading.emit(false)
            }

            if (autoAuthenticate.value)
                acceptAuthentication()
        }
    }

    fun acceptAuthentication() {
        CoroutineScope(Dispatchers.IO).launch {
            if (settings.get("auth_key").isNullOrEmpty())
                _errorHandlerFlow.emit(DialogMessage("You need to set X-Auth Key!"))

            try {
                val response = HttpController(settings.get("auth_key", "")).authorizationRequestSendClaims(
                    intent.location, // redirectUrl,
                    intent.request_uri, // requestUri,
                    kvnr,
                    claims.filter { it.value }.map { it.key }
                )

                println("used kvnr: ${kvnr}")
                println("App-App-Flow Nr 8 TX: $response")
                executeDeeplink(context, response)
            } catch (_: GemSekIdpTimeout) {
                _errorHandlerFlow.emit(DialogMessage("Connection timeout. Check your Internet Connection.", MessageType.ERROR))
            }  catch (e: Exception) {
                println("!Unexpected Exception occurred: ${e.message}\n${e.cause}")
                _errorHandlerFlow.emit(
                    DialogMessage(
                        "Unexpected Error. Look at logs to get further information.",
                        MessageType.ERROR
                    )
                )
            }
        }
    }

    fun declineAuthentication() {
        CoroutineScope(Dispatchers.IO).launch {
            _errorHandlerFlow.emit(DialogMessage("Decline Button has no function", MessageType.INFORMATION))
        }
    }

    fun setXAuthKeyInAuthentication(authKey: String, inAuthentication: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            _errorHandlerFlow.emit(
                DialogMessage(
                    if (inAuthentication)
                        "Update X-Auth Key! Changes apply immediately"
                    else
                        "Update X-Auth Key! Change takes effect at next authentication",
                    MessageType.INFORMATION
                )
            )
        }

        settings.set("auth_key", authKey)

        if (debug) {
            println("Auth Key: " + settings.get("auth_key"))
        }

        if (inAuthentication && claims.isEmpty())
            viewmodelGetClaims()
    }
}