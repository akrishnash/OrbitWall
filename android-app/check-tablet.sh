#!/bin/bash
# Quick script to check if tablet is connected

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
ADB="$SDK_DIR/platform-tools/adb.exe"

echo "========================================"
echo "Checking Samsung Tablet Connection"
echo "========================================"
echo ""

echo "Checking for connected devices..."
echo ""

# Get list of devices
DEVICES=$("$ADB" devices 2>/dev/null)

echo "$DEVICES"
echo ""

# Check if any device is connected
if echo "$DEVICES" | grep -q "device$"; then
    DEVICE_NAME=$(echo "$DEVICES" | grep "device$" | head -n 1 | awk '{print $1}')
    echo "✅ Device found: $DEVICE_NAME"
    echo ""
    echo "Your tablet is ready!"
    echo ""
    echo "To install the app, run:"
    echo "  ./install-to-phone.sh"
    echo ""
    echo "Or manually:"
    echo "  ./gradlew installDebug"
else
    if echo "$DEVICES" | grep -q "unauthorized"; then
        echo "⚠️  Device found but UNAUTHORIZED"
        echo ""
        echo "On your Samsung tablet:"
        echo "  1. Check the screen for 'Allow USB debugging?' popup"
        echo "  2. Check 'Always allow from this computer'"
        echo "  3. Tap 'Allow'"
        echo ""
        echo "Then run this script again: ./check-tablet.sh"
    else
        echo "❌ No devices found"
        echo ""
        echo "Make sure:"
        echo "  1. Tablet is connected via USB"
        echo "  2. USB Debugging is enabled (Settings > Developer Options)"
        echo "  3. USB mode is set to 'File Transfer' or 'MTP'"
        echo "  4. Tablet screen is unlocked"
        echo ""
        echo "Try:"
        echo "  adb kill-server"
        echo "  adb start-server"
        echo "  adb devices"
    fi
fi






