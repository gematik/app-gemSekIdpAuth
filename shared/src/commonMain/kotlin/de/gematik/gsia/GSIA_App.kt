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
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.data.GSIAIntentStep5
import de.gematik.gsia.data.GSIAViewModel
import de.gematik.gsia.presentation.AuthenticationScreen
import de.gematik.gsia.presentation.DefaultScreen

@Composable
fun App(context: Any?, intent: String) {

    val viewModel : GSIAViewModel = viewModel { GSIAViewModel() }
    viewModel.setContext(context)

    try {

        val gsiaIntent: GSIAIntentStep5 = GSIAIntentStep5(intent)
        viewModel.setIntent(GSIAIntentStep5(intent))

        if (intent.contains("user_id"))
            viewModel.setKVNR(gsiaIntent.getUser_id())
        else
            viewModel.setKVNR("X123456784")

    } catch (e: Exception) {
        println("incorrect intent")
        println(intent)
        createToast(
            viewModel.context.value,
            e.message ?: "unspecified exception raised! See logs for further information"
        )
    } finally {
        if (viewModel.intent.value.string.isEmpty()) {
            DefaultScreen()
        } else
            AuthenticationScreen()
    }
}
