#!/bin/bash
# Quick script to check for existing AVDs and system images

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"

echo "========================================"
echo "Checking for Existing AVDs and Images"
echo "========================================"
echo ""

# Check for existing AVDs
echo "📱 Existing AVDs:"
echo "----------------------------------------"
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

# Check E drive
if [ -d "$ANDROID_AVD_HOME" ]; then
    AVD_COUNT=$(ls -1 "$ANDROID_AVD_HOME"/*.ini 2>/dev/null | wc -l)
    if [ "$AVD_COUNT" -gt 0 ]; then
        echo "✅ Found $AVD_COUNT AVD(s) on E drive:"
        ls -1 "$ANDROID_AVD_HOME"/*.ini 2>/dev/null | sed 's|.*/||' | sed 's|\.ini||' | sed 's|^|  - |'
    fi
fi

# Check C drive default location
C_DRIVE_AVD="/c/Users/anurag/.android/avd"
if [ -d "$C_DRIVE_AVD" ]; then
    AVD_COUNT=$(ls -1 "$C_DRIVE_AVD"/*.ini 2>/dev/null | wc -l)
    if [ "$AVD_COUNT" -gt 0 ]; then
        echo "✅ Found $AVD_COUNT AVD(s) on C drive:"
        ls -1 "$C_DRIVE_AVD"/*.ini 2>/dev/null | sed 's|.*/||' | sed 's|\.ini||' | sed 's|^|  - |'
    fi
fi

# Check using emulator command
if [ -f "$SDK_DIR/emulator/emulator.exe" ]; then
    echo ""
    echo "📋 AVDs detected by emulator:"
    echo "----------------------------------------"
    "$SDK_DIR/emulator/emulator.exe" -list-avds 2>/dev/null || echo "  (none found)"
fi

echo ""
echo "========================================"
echo "Installed System Images"
echo "========================================"
echo ""

if [ -f "$SDK_DIR/cmdline-tools/latest/bin/sdkmanager" ]; then
    echo "Checking installed system images..."
    "$SDK_DIR/cmdline-tools/latest/bin/sdkmanager" --list_installed 2>/dev/null | grep "system-images" || echo "  (none found)"
elif [ -f "$SDK_DIR/cmdline-tools/bin/sdkmanager" ]; then
    "$SDK_DIR/cmdline-tools/bin/sdkmanager" --list_installed 2>/dev/null | grep "system-images" || echo "  (none found)"
else
    echo "⚠️  sdkmanager not found. Cannot check system images."
fi

echo ""
echo "========================================"
echo "Quick Actions"
echo "========================================"
echo ""
echo "If you found an existing AVD, start it with:"
echo "  cd android-app"
echo "  ./start-emulator.sh"
echo ""
echo "Or directly:"
echo "  $SDK_DIR/emulator/emulator.exe -avd <AVD_NAME>"
echo ""

