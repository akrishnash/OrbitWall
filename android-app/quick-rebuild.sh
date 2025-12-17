#!/bin/bash
# Quick rebuild and reinstall script for faster iteration

# SDK location
SDK_DIR="/c/Users/aks/AppData/Local/Android/Sdk"
ADB="$SDK_DIR/platform-tools/adb.exe"

# Add Android SDK tools to PATH
export PATH="$SDK_DIR/platform-tools:$SDK_DIR/emulator:$PATH"

# Set Java if needed
if [ -d "/e/anurag/android studio/jbr" ]; then
    export JAVA_HOME="/e/anurag/android studio/jbr"
    export PATH="$JAVA_HOME/bin:$PATH"
fi

cd "$(dirname "$0")"

echo "========================================"
echo "Quick Rebuild & Install"
echo "========================================"
echo ""

# Check if device is connected
if ! "$ADB" devices 2>/dev/null | grep -q "device$"; then
    echo "⚠️  No device connected!"
    echo "Make sure emulator is running or device is connected."
    echo ""
    echo "Run: ./start-emulator.sh"
    exit 1
fi

echo "Building and installing..."
echo ""

# Build and install (Gradle will handle incremental builds)
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ SUCCESS! App updated on device."
    echo ""
    echo "If the app is already open, restart it to see changes."
    echo "Or launch it:"
    echo "  $ADB shell am start -n com.orbitwall/.MainActivity"
else
    echo ""
    echo "❌ Build failed. Check errors above."
    exit 1
fi
