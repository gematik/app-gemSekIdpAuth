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

import SwiftUI
import shared

struct ContentView: View {
    @State var universalLink: URL?

	var body: some View {
        VStack {
            if let universalLink = universalLink {
                // id is used to refresh the view and trigger a new session
                ComposeView(universalLink: universalLink)
                    .id(universalLink.absoluteString)
            } else {
                ComposeView(universalLink: nil)
            }
        }
        .onOpenURL(perform: { url in
            universalLink = url
        })
	}
}

struct ContentView_Previews: PreviewProvider {
	static var previews: some View {
		ContentView()
	}
}
