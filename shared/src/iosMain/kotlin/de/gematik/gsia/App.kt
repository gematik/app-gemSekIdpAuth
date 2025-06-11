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

import androidx.compose.ui.window.ComposeUIViewController
import io.ktor.client.HttpClient
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import platform.CoreGraphics.CGPointMake
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIColor
import platform.UIKit.UILabel
import platform.UIKit.UIScreen
import platform.CoreGraphics.CGRectMake
import platform.UIKit.NSTextAlignmentCenter
import platform.UIKit.UIView
import platform.UIKit.UIViewAnimationOptionCurveEaseOut

fun MainViewController(url: String?) = ComposeUIViewController { App("Dummy()", url ?: "") }

typealias APPObject = UIApplication

actual fun executeDeeplink(context: Any?, uri: String) {
    if (UIApplication.sharedApplication.canOpenURL(
            NSURL.URLWithString(uri)!!)
    ) {
        UIApplication.sharedApplication.openURL(
            NSURL.URLWithString(
                uri
            )!!
        )
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun createToast(context: Any?, string: String) {

    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    val toast = UILabel(frame = CGRectMake(0.0, 0.0, UIScreen.mainScreen.bounds.useContents { size.width } - 40, 35.0))
    toast.center = CGPointMake(UIScreen.mainScreen.bounds.useContents { size.width } / 2, UIScreen.mainScreen.bounds.useContents { size.height } - 150.0)
    toast.textAlignment = NSTextAlignmentCenter
    toast.backgroundColor = UIColor.blackColor.colorWithAlphaComponent(0.6)
    toast.textColor = UIColor.whiteColor
    toast.text = string
    toast.alpha = 1.0
    toast.layer.cornerRadius = 10.0
    toast.clipsToBounds = true
    rootViewController?.view?.addSubview(toast)

    UIView.animateWithDuration(
        10.0,
        delay = 2.0,
        options = UIViewAnimationOptionCurveEaseOut,
        animations = {
            toast.alpha = 0.0
        },
        completion = {
            if (it)
                toast.removeFromSuperview()
        }
    )
}

actual val PlatformHttpEngine = HttpClient() { followRedirects = false }
