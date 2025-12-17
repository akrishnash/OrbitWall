#!/bin/bash
# Create Android Virtual Device (AVD) on E Drive

# SDK location from local.properties
SDK_DIR="/c/Users/aks/AppData/Local/Android/Sdk"
AVDMANAGER="$SDK_DIR/cmdline-tools/latest/bin/avdmanager"
SDKMANAGER="$SDK_DIR/cmdline-tools/latest/bin/sdkmanager"

# Set AVD home to E drive
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

# Create directory if it doesn't exist
mkdir -p "$ANDROID_AVD_HOME"
mkdir -p "$ANDROID_SDK_HOME"

# Add to PATH
export PATH="$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/emulator:$SDK_DIR/platform-tools:$PATH"

echo "========================================"
echo "Create Android Virtual Device (AVD)"
echo "========================================"
echo "AVD will be created at: $ANDROID_AVD_HOME"
echo ""

# Check if cmdline-tools exists
if [ ! -f "$AVDMANAGER" ]; then
    echo "⚠️  cmdline-tools not found at expected location."
    echo "Trying alternative locations..."
    
    # Try to find avdmanager in common locations
    echo "Searching for avdmanager..."
    
    # Try common locations
    POSSIBLE_PATHS=(
        "$SDK_DIR/cmdline-tools/latest/bin/avdmanager"
        "$SDK_DIR/cmdline-tools/bin/avdmanager"
        "$SDK_DIR/tools/bin/avdmanager"
        "/c/Program Files/Android/Android Studio/bin/avdmanager"
    )
    
    for path in "${POSSIBLE_PATHS[@]}"; do
        if [ -f "$path" ]; then
            AVDMANAGER="$path"
            SDKMANAGER="${path%avdmanager}sdkmanager"
            echo "✅ Found at: $AVDMANAGER"
            break
        fi
    done
    
    # Try Windows find command (works better in Git Bash)
    if [ -z "$AVDMANAGER" ]; then
        AVDMANAGER=$(cmd.exe /c "dir /s /b \"$SDK_DIR\\avdmanager.bat\" 2>nul" | head -n 1 | tr -d '\r')
        if [ -n "$AVDMANAGER" ]; then
            # Convert Windows path to Unix-style
            AVDMANAGER=$(echo "$AVDMANAGER" | sed 's|^C:|/c|' | sed 's|\\|/|g')
            SDKMANAGER=$(echo "$AVDMANAGER" | sed 's|avdmanager.bat|sdkmanager.bat|')
        fi
    fi
    
    if [ -z "$AVDMANAGER" ] || [ ! -f "$AVDMANAGER" ]; then
        echo "❌ ERROR: avdmanager not found!"
        echo ""
        echo "════════════════════════════════════════════════════════════"
        echo "EASIER OPTION: Create AVD in Android Studio"
        echo "════════════════════════════════════════════════════════════"
        echo ""
        echo "Since command-line tools aren't installed, create the AVD"
        echo "in Android Studio and it will work with E drive:"
        echo ""
        echo "1. Open Android Studio"
        echo "2. Tools > Device Manager > Create Device"
        echo "3. Select Pixel 7 > Next"
        echo "4. Download/Select API 34 system image"
        echo "5. Finish"
        echo ""
        echo "Then BEFORE starting it, set environment variables:"
        echo "  export ANDROID_AVD_HOME=\"/e/anurag/.android/avd\""
        echo "  export ANDROID_SDK_HOME=\"/e/anurag/.android\""
        echo ""
        echo "Or use: ./start-emulator.sh (it sets these automatically)"
        echo ""
        echo "════════════════════════════════════════════════════════════"
        echo "INSTALL COMMAND-LINE TOOLS (if you want command-line method)"
        echo "════════════════════════════════════════════════════════════"
        echo "1. Open Android Studio"
        echo "2. Tools > SDK Manager"
        echo "3. SDK Tools tab"
        echo "4. Check 'Android SDK Command-line Tools (latest)'"
        echo "5. Click Apply"
        echo "6. Run this script again: ./create-avd.sh"
        echo ""
        exit 1
    fi
fi

# List available system images
echo "Checking available system images..."
echo ""

# List installed system images
echo "Installed system images:"
$AVDMANAGER list target 2>/dev/null | grep -A 20 "id:" || echo "  (none installed yet)"
echo ""

# Check for API 34 (Android 14) system image
API34_IMAGE=$($SDKMANAGER --list_installed 2>/dev/null | grep "system-images;android-34" | head -n 1)

if [ -z "$API34_IMAGE" ]; then
    echo "⚠️  API 34 system image not found!"
    echo ""
    echo "You need to install a system image first."
    echo ""
    echo "Option 1 - Using Android Studio (Recommended):"
    echo "  1. Open Android Studio"
    echo "  2. Tools > SDK Manager"
    echo "  3. SDK Platforms tab > Check 'Android 14.0 (API 34)'"
    echo "  4. SDK Tools tab > Check 'Android Emulator'"
    echo "  5. Click Apply to download"
    echo ""
    echo "Option 2 - Using Command Line:"
    echo "  $SDKMANAGER \"system-images;android-34;google_apis;x86_64\""
    echo "  (Accept licenses when prompted)"
    echo ""
    echo "After installing, run this script again."
    exit 1
fi

echo "✅ Found system images!"
echo ""

# Get available system images
echo "Available system images for AVD creation:"
$AVDMANAGER list target 2>/dev/null | grep -E "id:|Name:" | head -n 20
echo ""

# Create AVD with default settings
AVD_NAME="Pixel_7_API_34"
echo "Creating AVD: $AVD_NAME"
echo "This will create a Pixel 7 emulator with API 34"
echo ""

# Try to create AVD with Pixel 7 device definition
$AVDMANAGER create avd \
    -n "$AVD_NAME" \
    -k "system-images;android-34;google_apis;x86_64" \
    -d "pixel_7" \
    --force

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ AVD created successfully!"
    echo ""
    echo "AVD Name: $AVD_NAME"
    echo "Location: $ANDROID_AVD_HOME"
    echo ""
    echo "To start the emulator, run:"
    echo "  ./start-emulator.sh"
    echo ""
else
    echo ""
    echo "⚠️  Could not create AVD with Pixel 7 device."
    echo "Trying with generic device..."
    echo ""
    
    # Try with generic device
    $AVDMANAGER create avd \
        -n "$AVD_NAME" \
        -k "system-images;android-34;google_apis;x86_64" \
        --force
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✅ AVD created successfully with generic device!"
        echo ""
        echo "To start the emulator, run:"
        echo "  ./start-emulator.sh"
    else
        echo ""
        echo "❌ Failed to create AVD"
        echo ""
        echo "Please create it manually in Android Studio:"
        echo "  1. Tools > Device Manager"
        echo "  2. Create Device"
        echo "  3. Select Pixel 7"
        echo "  4. Choose API 34 system image"
        echo "  5. Finish"
        echo ""
        echo "Then move it to E drive or recreate using this script after installing system images."
    fi
fi

