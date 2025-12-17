#!/bin/bash
# Build and Run Android App from Cursor Terminal
# This script builds the APK and installs it on a connected device

echo "========================================"
echo "OrbitWall Android - Build and Run"
echo "========================================"
echo ""

cd "$(dirname "$0")"

# SDK location from local.properties (or use default)
SDK_DIR="/c/Users/aks/AppData/Local/Android/Sdk"
ADB="$SDK_DIR/platform-tools/adb.exe"

# Add Android SDK tools to PATH
export PATH="$SDK_DIR/platform-tools:$SDK_DIR/emulator:$SDK_DIR/tools:$SDK_DIR/tools/bin:$PATH"

# Set Java if needed (use Android Studio's JBR)
if [ -d "/e/anurag/android studio/jbr" ]; then
    export JAVA_HOME="/e/anurag/android studio/jbr"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

# Check if device is connected
echo "Checking for connected Android device..."
DEVICES=$("$ADB" devices 2>/dev/null)
echo ""
echo "Connected devices:"
echo "$DEVICES"
echo ""

# Check if any device is in "device" state (ready)
if ! echo "$DEVICES" | grep -q "device$"; then
    echo "⚠️  No ready device or emulator found!"
    echo ""
    
    # Check if device is offline or unauthorized
    if echo "$DEVICES" | grep -q "offline"; then
        echo "Device is offline. Waiting for emulator to boot..."
        echo "Please wait a few more seconds for the emulator to fully boot."
    elif echo "$DEVICES" | grep -q "unauthorized"; then
        echo "Device is unauthorized. Please accept the USB debugging prompt on the device."
    elif echo "$DEVICES" | grep -q "emulator"; then
        echo "Emulator detected but not ready yet. Waiting..."
        echo "Please wait for the emulator to fully boot (check the emulator window)."
    else
        echo "Please start an emulator or connect a device:"
        echo "  - If emulator is starting, wait for it to boot completely"
        echo "  - Or run: ./start-emulator.sh"
    fi
    
    echo ""
    echo "Try running this script again once the emulator shows the home screen."
    exit 1
fi

echo "Device found! Building and installing..."
echo ""

# Clean previous build (optional - comment out if you want faster builds)
# echo "Cleaning previous build..."
# ./gradlew clean

# Build and install
echo "Building debug APK..."
./gradlew assembleDebug
if [ $? -ne 0 ]; then
    echo ""
    echo "BUILD FAILED! Check errors above."
    exit 1
fi

echo ""
echo "Installing APK on device..."
./gradlew installDebug
if [ $? -ne 0 ]; then
    echo ""
    echo "INSTALL FAILED! Check errors above."
    exit 1
fi

echo ""
echo "========================================"
echo "SUCCESS! App installed and ready to launch"
echo "========================================"
echo ""
echo "To launch the app, run: $ADB shell am start -n com.orbitwall/.MainActivity"
echo "Or just open it manually on your device."
echo ""

