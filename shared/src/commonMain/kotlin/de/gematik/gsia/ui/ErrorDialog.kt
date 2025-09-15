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

package de.gematik.gsia.ui

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import de.gematik.gsia.data.GSIAViewModel
import kotlinx.coroutines.launch

enum class MessageType {
    ERROR, INFORMATION
}

data class DialogMessage(val message: String, val type: MessageType = MessageType.ERROR)

@Composable
fun Dialog(message: String, type: MessageType, onDismiss: () -> Unit) {
    AlertDialog(
        title = {
            when (type) {
                MessageType.ERROR -> Text("oops.. Something went wrong")
                MessageType.INFORMATION -> Text("Note")
            }
        },
        onDismissRequest = { onDismiss() },
        text = { Text(text = message) },
        dismissButton = { },
        confirmButton = { }
    )
}

@Composable
fun ErrorHandler() {
    val openDialog = remember { mutableStateOf(false) }
    val messageDialog = remember { mutableStateOf(DialogMessage("", MessageType.INFORMATION)) }
    val viewModel : GSIAViewModel = viewModel { GSIAViewModel() }
    val scope = rememberCoroutineScope()


    // Display any toasts coming from viewmodel throughout the whole app
    scope.launch {
        viewModel.errorHandlerFlow.collect { message ->
            openDialog.value = true
            messageDialog.value = message
        }
    }

    if (openDialog.value) {
        Dialog(
            messageDialog.value.message,
            messageDialog.value.type,
            {
                openDialog.value = false
            }
        )
    }
}