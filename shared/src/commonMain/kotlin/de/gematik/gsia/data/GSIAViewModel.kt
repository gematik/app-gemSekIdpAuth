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

    private val _event: MutableSharedFlow<EventClaims> = MutableSharedFlow()
    val event: Flow<EventClaims> = _event

    private val _toastFlow: MutableSharedFlow<String> = MutableSharedFlow()
    val toastFlow: Flow<String> = _toastFlow

    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    var context by mutableStateOf<Any?>(null)

    var intent by mutableStateOf<GSIAIntentStep5>(GSIAIntentStep5(""))

    init {
        viewModelScope.launch {
            event.collect { event ->
                setSelectedClaims(event.claims.associateWith { true }.toMutableMap())
            }
        }
    }

    fun setKVNR(kvnr: String) {
        settings.set("kvnr", kvnr)
        this.kvnr = kvnr
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
                _toastFlow.emit(
                    e.message ?: "unspecified exception raised! See logs for further information"
                )
            }
        }

        if (this.intent.user_id.isNotEmpty())
            setKVNR(this.intent.user_id)
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

            } catch (e: SocketTimeoutException) {
                _toastFlow.emit("Connection timeout. Check your Internet Connection.")
                println("Can't connect to gemSekIdp. Can't retrieve claims. Check your Internet Connection.")
            } catch (e: GemSekIdpForbidden) {
                _toastFlow.emit("Http Code: 302. Check your X-Auth Key.")
                println("Can't get Claims from gemSekIdp due to wrong X-Auth Key. Please enter correct X-Auth Key in GSIA. In Case you haven't got one, contact gematik.")
            } catch (e: GemSekIdpUnexpectedStatusCode) {
                _toastFlow.emit("Unexpected HTTP Response Code: ${e.status}")
                println("gemSekIdp responded with an unexpected HTTP Code: ${e.status}, ${e.message}")
            } catch (e: Exception) {
                println("!Unexpected Exception occurred: ${e.message}\n${e.cause}")
                _toastFlow.emit("Unexpected Error. Look at logs to get further information.")
            } finally {
                _isLoading.emit(false)
            }
        }
    }

    fun acceptAuthentication() {
        CoroutineScope(Dispatchers.IO).launch {
            if (settings.get("auth_key").isNullOrEmpty())
                _toastFlow.emit("You need to set X-Auth Key!")

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
            } catch (e: Exception) {
                CoroutineScope(Dispatchers.IO).launch {
                    _toastFlow.emit(e.message ?: "Claims konnten nicht abgerufen werden!")
                }
            }
        }
    }

    fun declineAuthentication() {
        CoroutineScope(Dispatchers.IO).launch {
            _toastFlow.emit("Decline Button has no function")
        }
    }

    fun setXAuthKeyInAuthentication(authKey: String, inAuthentication: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            _toastFlow.emit(
                if (inAuthentication)
                    "Update X-Auth Key! Changes apply immediately"
                else
                    "Update X-Auth Key! Change takes effect at next authentication"
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