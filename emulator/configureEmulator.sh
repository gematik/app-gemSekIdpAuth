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

JDK17=/c/Program\ Files/Eclipse\ Adoptium/jdk-17.0.6.10-hotspot/

##### CONFIGURATION #####
INSTALL_CUSTOM_DIGA=0
DIGA_PATH=/c/repos/gsia/emulator/
DIGA_NAME=callee.apk
#########################

export JAVA_HOME=$JDK17
export JDK_HOME=JAVA_HOME

adb wait-for-device
isBooted=$(adb shell getprop sys.boot_completed)

while [ "$isBooted" != "1" ]
do
  sleep 3
  isBooted=$(adb shell getprop sys.boot_completed)
  echo "emulator is booting.."
done

echo "emulator is started!"

sh ./gradlew clean
sh ./gradlew :gsia:installDebug

# wait for 10 seconds to be sure emulator is ready to be used
sleep 10
projectDir=$PWD
echo "$projectDir"

if [ $INSTALL_CUSTOM_DIGA -eq 1 ]
then
  echo "installing $DIGA_NAME"
  cd $DIGA_PATH || return
  adb install $DIGA_NAME
fi