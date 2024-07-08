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
import com.russhwolf.settings.Settings
import io.ktor.http.Url

class GSIAViewModel : ViewModel() {

    private val _kvnr = mutableStateOf("")
    val kvnr = _kvnr

    private val _claims = mutableStateMapOf<String, Boolean>()
    val claims = _claims

    private val _context = mutableStateOf<Any?>(null)
    val context = _context

    private val _settings = mutableStateOf<Settings>(Settings())
    val settings = _settings

    private val _intent = mutableStateOf<Url>(Url(""))
    val intent = _intent

    init {
        // println("init() of viewmodel was called!")
    }

    fun setKVNR(kvnr: String) {
        _kvnr.value = kvnr
    }

    fun setSelectedClaims(selectedClaims: Map<String, Boolean>) {
        selectedClaims.forEach { (k, v) ->
            _claims[k] = v
        }
    }

    fun setContext(context: Any) {
        _context.value = context
    }

    fun setSettings(settings: Settings) {
        _settings.value = settings
    }

    fun setIntent(intent: Url) {
        _intent.value = intent
    }

    fun resetClaims() {
        _claims.clear()
    }
}