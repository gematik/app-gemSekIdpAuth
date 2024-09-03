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

package de.gematik.gsia.presentation

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FilledButton(label: String, onClick: () -> Unit = ({ })) {
    Button(
        modifier = Modifier
            .height(50.dp)
            .width(150.dp),
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1, 14, 82, 255)),
        onClick = { onClick() })
    {
        Text(
            text = label,
            color = Color(0, 255, 101, 255),
            fontFamily = FontFamily.SansSerif,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun FilledButton(label: String, onClick: () -> Unit = ({ }), modifier: Modifier, fontSize: TextUnit = 20.sp) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(backgroundColor = Color(1, 14, 82, 255)),
        onClick = { onClick() })
    {
        Text(
            text = label,
            color = Color(0, 255, 101, 255),
            fontFamily = FontFamily.SansSerif,
            fontSize = fontSize,
            fontWeight = FontWeight.Bold
        )
    }
}