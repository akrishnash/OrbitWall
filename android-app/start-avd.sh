#!/bin/bash
# Quick script to start an existing AVD

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
EMULATOR="$SDK_DIR/emulator/emulator.exe"

# Set AVD home to E drive
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

echo "========================================"
echo "Starting Android Emulator"
echo "========================================"
echo ""

# List available AVDs
echo "Available AVDs:"
AVDS=$("$EMULATOR" -list-avds 2>/dev/null)
echo "$AVDS"
echo ""

# If AVD name provided as argument, use it
if [ -n "$1" ]; then
    AVD_NAME="$1"
    echo "Starting: $AVD_NAME"
else
    # Use the first available AVD (or you can specify)
    AVD_NAME=$(echo "$AVDS" | head -n 1)
    echo "Starting first available AVD: $AVD_NAME"
    echo ""
    echo "To start a specific AVD, run:"
    echo "  ./start-avd.sh Medium_Phone_API_36.0"
    echo "  or"
    echo "  ./start-avd.sh Pixel_4"
fi

echo ""
echo "Starting emulator in background..."
echo "This may take 30-60 seconds to boot..."
echo ""

# Start emulator
"$EMULATOR" -avd "$AVD_NAME" &

echo "✅ Emulator is starting!"
echo ""
echo "Wait for it to boot, then check status with:"
echo "  adb devices"
echo ""
echo "Once you see the device listed, install the app with:"
echo "  ./gradlew installDebug"
echo ""

