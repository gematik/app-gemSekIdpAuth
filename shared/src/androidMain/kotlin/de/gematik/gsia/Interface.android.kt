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

package de.gematik.gsia

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import io.ktor.client.HttpClient
import androidx.core.net.toUri

actual fun executeDeeplink(context: Any?, uri: String) {
    // startActivity(Intent(Intent.ACTION_VIEW))
    startActivity(context as Context, Intent(Intent.ACTION_VIEW).apply {
        data = uri.toUri()
    }, null)
}

actual val PlatformHttpEngine = HttpClient() { followRedirects = false }
