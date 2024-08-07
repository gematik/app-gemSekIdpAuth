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

import android.app.Activity
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
internal actual fun getGematikLogoPainter(): Painter {
    // return painterResource(ImageResource(R.drawable.gematik))
    TODO("Not yet implemented")
}

actual fun executeDeeplink(context: Any?, uri: String) {
    // startActivity(Intent(Intent.ACTION_VIEW))
    startActivity(context as Context, Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse(uri)
    }, null)
}

actual fun Toast(context: Any?, string: String) {
    Toast.makeText(context as Context, string, Toast.LENGTH_SHORT).show()
}