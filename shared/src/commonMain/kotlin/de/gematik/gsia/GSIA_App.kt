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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.data.GSIAIntentStep5
import de.gematik.gsia.data.GSIAViewModel
import de.gematik.gsia.presentation.AuthenticationScreen
import de.gematik.gsia.presentation.DefaultScreen
import kotlinx.coroutines.launch

@Composable
fun App(context: Any?, intent: String) {

    val viewModel : GSIAViewModel = viewModel { GSIAViewModel() }
    val scope = rememberCoroutineScope()

    viewModel.context = context
    viewModel.setIntent(intent)

    // Display any toasts coming from viewmodel throughout the whole app
    scope.launch {
        viewModel.toastFlow.collect { message ->
            createToast(viewModel.context, message)
        }
    }

    if (intent.isEmpty())
        DefaultScreen()
    else
        AuthenticationScreen()
}