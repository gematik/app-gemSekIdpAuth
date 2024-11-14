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

import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set

interface SharedPreference {
    fun get(key: String): String?
    fun get(key: String, stringIfNull: String): String
    fun set(key: String, value: Any)
}


class MockSharedPreferences: SharedPreference {
    val data: MutableMap<String, String> = mutableMapOf()

    override fun get(key: String): String? {
        return data[key]
    }

    override fun get(key: String, stringIfNull: String): String {
        return data[key] ?: stringIfNull
    }

    override fun set(key: String, value: Any) {
        data[key] = value.toString()
    }
}

class RusshwolfSharedPreferences: SharedPreference {
    val data: Settings = Settings()

    override fun get(key: String): String? {
        return data[key]
    }

    override fun get(key: String, stringIfNull: String): String {
        return data[key, stringIfNull]
    }

    override fun set(key: String, value: Any) {
        data[key] = value.toString()
    }
}