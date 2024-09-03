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

import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import de.gematik.gsia.HttpController
import de.gematik.gsia.createToast
import io.ktor.client.network.sockets.SocketTimeoutException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class EventClaims(val claims: List<String>)

class GSIAViewModel : ViewModel() {

    private val _settings = mutableStateOf<Settings>(Settings())
    val settings = _settings

    private val _kvnr = mutableStateOf<String>(settings.value.get<String>("kvnr") ?: "")
    val kvnr = _kvnr

    private val _claims = mutableStateMapOf<String, Boolean>()
    val claims = _claims

    private val _event: MutableSharedFlow<EventClaims> = MutableSharedFlow()
    val event: Flow<EventClaims> = _event

    private val _context = mutableStateOf<Any?>(null)
    val context = _context

    private val _intent = mutableStateOf<GSIAIntentStep5>(GSIAIntentStep5(""))
    val intent = _intent

    var isGettingClaimsFromIdp = mutableStateOf(false)


    init {
        viewModelScope.launch {
            event.collect { event ->
                setSelectedClaims(event.claims.associateWith { true }.toMutableMap())
            }
        }
    }

    fun setKVNR(kvnr: String) {
        _kvnr.value = kvnr
        settings.value.set("kvnr", kvnr)
    }

    fun setSelectedClaims(selectedClaims: Map<String, Boolean>) {
        selectedClaims.forEach { (k, v) ->
            _claims[k] = v
        }
    }

    fun setClaim(claim: String, state: Boolean) {
        _claims[claim] = state
    }

    fun setContext(context: Any?) {
        _context.value = context
    }

    fun setIntent(intent: GSIAIntentStep5) {
        _intent.value = intent
    }

    fun resetClaims() {
        _claims.clear()
    }

    fun viewmodelGetClaims() {

        viewModelScope.launch {

            try {
                isGettingClaimsFromIdp.value = true

                val claims = HttpController(settings.value["auth_key", ""]).authorizationRequestGetClaims(
                    intent.value.getLocation(),
                    intent.value.getRequest_uri(),
                ).associateWith { true }.toMutableMap()

                _event.emit(EventClaims(claims.keys.toList()))

                println("App-App-Flow Nr 6a RX: (${claims.size} Claims received)")

            } catch (e: SocketTimeoutException) {
                createToast(context.value, "Connection timeout. Check your Internet Connection.")
                println("Can't connect to gemSekIdp. Can't retrieve claims. Check your Internet Connection.")
            } catch (e: GemSekIdpForbidden) {
                createToast(context.value, "Http Code: 302. Check your X-Auth Key.")
                println("Can't get Claims from gemSekIdp due to wrong X-Auth Key. Please enter correct X-Auth Key in GSIA. In Case you haven't got one, contact gematik.")
            } catch (e: GemSekIdpUnexpectedStatusCode) {
                createToast(context.value, "Unexpected HTTP Response Code: ${e.status}")
                println("gemSekIdp responded with an unexpected HTTP Code: ${e.status}, ${e.message}")
            } catch (e: Exception) {
                println("!Unexpected Exception occurred: ${e.message}")
                println(e.cause)
                createToast(
                    context.value,
                    "Unexpected Error. Look at logs to get further information."
                )
            } finally {
                isGettingClaimsFromIdp.value = false
            }
        }
    }
}