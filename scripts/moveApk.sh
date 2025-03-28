#!/bin/bash

# Step 1: Define the source and destination paths
APK_PATH="./gsia_Android/build/outputs/apk/release/gsia_Android-release.apk"  # Adjust if needed for debug or another variant
DEST_PATH="./app-release.apk"  # You can change this if you want the APK to have a different name

echo $APK_PATH

# Step 2: Check if the APK exists and move it to the root directory
if [ -f "$APK_PATH" ]; then
  echo "Moving APK to the root directory..."
  mv "$APK_PATH" "$DEST_PATH"
  echo "APK has been moved to the root directory!"
else
  echo "APK not found! Build might have failed."
fi

# generated with AI