#!/bin/bash

#
#  Copyright 2024 gematik GmbH
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#       http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#  limitations under the License.
#  
#

version_old="$1"
version_new="$2"
IFS='.' read major minor patch <<< "$version_old"
version_code_old=$(printf "%d%02d%02d" "$major" "$minor" "$patch")
IFS='.' read major minor patch <<< "$version_new"
version_code_new=$(printf "%d%02d%02d" "$major" "$minor" "$patch")

echo "$version_old -> $version_new"

sed -i "s/$version_code_old/$version_code_new/" ./gsia_Android/build.gradle.kts
sed -i "s/$version_old/$version_new/" ./gsia_Android/build.gradle.kts