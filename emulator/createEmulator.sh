#
#  Copyright 2023 gematik GmbH
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

#!/bin/bash

# JAVA 8 is needed to create the emulator
JDK8=/c/Program\ Files/Eclipse\ Adoptium/jdk-8.0.372.7-hotspot/
ANDROID_SDK=/.../Android/Sdk/
EMULATOR=/.../.android/avd/
EMULATOR_NAME=gsia_emulator

export JAVA_HOME=$JDK8
export JDK_HOME=JAVA_HOME

echo "Calling 'java' to output the currently used java version"
ls "$JAVA_HOME"
"$JAVA_HOME/bin/java" -version

# C:/Users/mourice.reinke/AppData/Local/Android/Sdk/tools/bin/avdmanager.bat --verbose create avd --force --name "gsia_emulator" --package "system-images;android-33;google_apis;x86_64"
echo no | $ANDROID_SDK/tools/bin/avdmanager.bat create avd --force --name "$EMULATOR_NAME" --package "system-images;android-33;google_apis;x86_64"
echo
echo "current location: $PWD"
cd ..


echo
echo "move config.."
echo "current location: $PWD"
cp $PWD/emulator/config.ini $EMULATOR/$EMULATOR_NAME.avd

echo
echo "starting emulator.."
/c/Program\ Files/Git/git-bash.exe -c "$ANDROID_SDK/tools/emulator.exe -avd $EMULATOR_NAME -wipe-data" &
# /c/Program\ Files/Git/git-bash.exe -c "$ANDROID_SDK/emulator/emulator.exe -avd $EMULATOR_NAME -verbose" &
"$PWD"/emulator/configureEmulator.sh

echo "program finished"
