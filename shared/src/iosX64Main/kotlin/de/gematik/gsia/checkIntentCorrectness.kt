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

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.ktor.http.Url

actual fun getAppName(): String {
    TODO("Not yet implemented")
}

@Composable
internal actual fun getGematikLogoPainter(): Painter {
    TODO("Not yet implemented")
    // return painterResource(ImageResource("gematik.xml"))
}

actual fun executeDeeplink(context: Any?, uri: String) {
}