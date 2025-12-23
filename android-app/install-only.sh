#!/bin/bash
# Just install the app (assumes emulator is already running)

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
ADB="$SDK_DIR/platform-tools/adb.exe"

echo "Checking device connection..."
"$ADB" devices

echo ""
echo "Installing app..."

# Retry logic
MAX_RETRIES=3
RETRY_COUNT=0

while [ $RETRY_COUNT -lt $MAX_RETRIES ]; do
    ./gradlew installDebug
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ App installed successfully!"
        echo ""
        echo "Launching app..."
        "$ADB" shell am start -n com.orbitwall/.MainActivity
        exit 0
    else
        RETRY_COUNT=$((RETRY_COUNT + 1))
        if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
            echo ""
            echo "⚠️  Installation failed. Retrying in 3 seconds... ($RETRY_COUNT/$MAX_RETRIES)"
            sleep 3
            "$ADB" devices
        fi
    fi
done

echo ""
echo "❌ Installation failed after $MAX_RETRIES attempts."
echo ""
echo "Try:"
echo "  1. Make sure emulator is fully booted (home screen visible)"
echo "  2. Restart ADB: adb kill-server && adb start-server"
echo "  3. Try again: ./gradlew installDebug"







