#!/bin/bash
# Start emulator and install app - all in one!

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
EMULATOR="$SDK_DIR/emulator/emulator.exe"
ADB="$SDK_DIR/platform-tools/adb.exe"

# Set AVD home
export ANDROID_AVD_HOME="/c/Users/anurag/.android/avd"
export ANDROID_SDK_HOME="/c/Users/anurag/.android"

echo "========================================"
echo "Starting Emulator & Installing App"
echo "========================================"
echo ""

# Check if already running
if "$ADB" devices 2>/dev/null | grep -q "emulator.*device$"; then
    echo "✅ Emulator already running!"
    "$ADB" devices
    echo ""
    echo "Installing app..."
    ./gradlew installDebug
    exit 0
fi

# Start Pixel_4
echo "🚀 Starting Pixel_4 emulator..."
"$EMULATOR" -avd "Pixel_4" > /dev/null 2>&1 &

echo "⏳ Waiting for emulator to boot (this takes 30-60 seconds)..."
echo ""

# Wait for emulator to be fully booted
echo "Waiting for Android to fully boot..."
for i in {1..120}; do
    sleep 2
    # Check if device is connected
    if "$ADB" devices 2>/dev/null | grep -q "emulator.*device$"; then
        # Check if Android is fully booted
        BOOT_COMPLETED=$("$ADB" shell getprop sys.boot_completed 2>/dev/null | tr -d '\r')
        if [ "$BOOT_COMPLETED" = "1" ]; then
            echo "✅ Emulator is fully ready!"
            echo ""
            
            # Wait a bit more to ensure ADB connection is stable
            echo "Waiting for ADB connection to stabilize..."
            sleep 5
            
            # Verify device is still online
            DEVICE_STATUS=$("$ADB" devices 2>/dev/null | grep "emulator.*device$" | wc -l)
            if [ "$DEVICE_STATUS" -eq 0 ]; then
                echo "⚠️  Device went offline. Waiting a bit more..."
                sleep 5
                # Restart ADB server if needed
                "$ADB" kill-server 2>/dev/null
                sleep 2
                "$ADB" start-server 2>/dev/null
                sleep 3
            fi
            
            "$ADB" devices
            echo ""
            echo "📦 Installing OrbitWall app..."
            
            # Try installation with retry
            MAX_RETRIES=3
            RETRY_COUNT=0
            INSTALL_SUCCESS=0
            
            while [ $RETRY_COUNT -lt $MAX_RETRIES ] && [ $INSTALL_SUCCESS -eq 0 ]; do
                if [ $RETRY_COUNT -gt 0 ]; then
                    echo "Retry attempt $RETRY_COUNT of $MAX_RETRIES..."
                    sleep 3
                    "$ADB" devices > /dev/null 2>&1
                fi
                
                ./gradlew installDebug
                if [ $? -eq 0 ]; then
                    INSTALL_SUCCESS=1
                else
                    RETRY_COUNT=$((RETRY_COUNT + 1))
                    if [ $RETRY_COUNT -lt $MAX_RETRIES ]; then
                        echo "Installation failed. Retrying..."
                    fi
                fi
            done
            
            if [ $INSTALL_SUCCESS -eq 1 ]; then
                echo ""
                echo "✅ App installed! Launching..."
                sleep 2
                "$ADB" shell am start -n com.orbitwall/.MainActivity
                echo ""
                echo "✅ Done! App should be visible now."
            else
                echo ""
                echo "⚠️  Installation failed after $MAX_RETRIES attempts."
                echo "Try manually: ./gradlew installDebug"
            fi
            exit 0
        fi
    fi
    if [ $((i % 10)) -eq 0 ]; then
        echo "   Still booting... ($i seconds) - waiting for Android to fully start..."
    fi
done

echo "⚠️  Emulator taking longer than expected."
echo "Check manually: adb devices"
echo "Then run: ./gradlew installDebug"

