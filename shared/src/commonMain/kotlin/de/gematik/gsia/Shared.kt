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

import io.ktor.client.HttpClient

/**
 * Mit dieser Funktion wird ein Deeplink ausgeführt.
 * @param context Kontext der Android App, für iOS nicht notwendig
 * @param uri Uri für die aufrufende App
 */
expect fun executeDeeplink(context: Any? = null, uri: String)

expect val PlatformHttpEngine: HttpClient

expect fun createToast(context: Any? = null, string: String)