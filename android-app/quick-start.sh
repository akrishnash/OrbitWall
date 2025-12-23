#!/bin/bash
# Quick start script - starts Pixel_4 emulator and installs app

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
EMULATOR="$SDK_DIR/emulator/emulator.exe"
ADB="$SDK_DIR/platform-tools/adb.exe"

# Set AVD home (will use C drive location for existing AVDs)
export ANDROID_AVD_HOME="/c/Users/anurag/.android/avd"
export ANDROID_SDK_HOME="/c/Users/anurag/.android"

echo "========================================"
echo "Quick Start - Pixel_4 Emulator"
echo "========================================"
echo ""

# Check if emulator is already running
if "$ADB" devices 2>/dev/null | grep -q "emulator.*device$"; then
    echo "✅ Emulator is already running!"
    "$ADB" devices
    echo ""
    echo "Installing app..."
    ./gradlew installDebug
    exit 0
fi

# Start Pixel_4 emulator
echo "Starting Pixel_4 emulator..."
echo "This will take 30-60 seconds to boot..."
echo ""

"$EMULATOR" -avd "Pixel_4" > /dev/null 2>&1 &

echo "✅ Emulator is starting in the background!"
echo ""
echo "Waiting for emulator to boot..."
echo ""

# Wait for emulator to be ready
echo "Checking if emulator is ready..."
for i in {1..60}; do
    if "$ADB" devices 2>/dev/null | grep -q "emulator.*device$"; then
        echo "✅ Emulator is ready!"
        echo ""
        "$ADB" devices
        echo ""
        echo "Installing OrbitWall app..."
        ./gradlew installDebug
        echo ""
        echo "✅ App installed! It should launch automatically."
        exit 0
    fi
    sleep 2
    if [ $((i % 5)) -eq 0 ]; then
        echo "Still waiting... ($i/60 seconds)"
    fi
done

echo "⚠️  Emulator is taking longer than expected."
echo "You can manually check with: adb devices"
echo "Then install app with: ./gradlew installDebug"

