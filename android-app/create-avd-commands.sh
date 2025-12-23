#!/bin/bash
# Step-by-step AVD creation using command line tools
# Run this in Git Bash

# Set SDK location
SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"

# Set AVD home to E drive (saves C drive space)
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

# Create directories
mkdir -p "$ANDROID_AVD_HOME"
mkdir -p "$ANDROID_SDK_HOME"

# Add to PATH
export PATH="$SDK_DIR/cmdline-tools/latest/bin:$SDK_DIR/emulator:$SDK_DIR/platform-tools:$SDK_DIR/tools/bin:$PATH"

echo "========================================"
echo "Creating Android Virtual Device (AVD)"
echo "========================================"
echo "SDK Location: $SDK_DIR"
echo "AVD Location: $ANDROID_AVD_HOME"
echo ""

# Find avdmanager
AVDMANAGER=""
SDKMANAGER=""

# Try different possible locations
if [ -f "$SDK_DIR/cmdline-tools/latest/bin/avdmanager" ]; then
    AVDMANAGER="$SDK_DIR/cmdline-tools/latest/bin/avdmanager"
    SDKMANAGER="$SDK_DIR/cmdline-tools/latest/bin/sdkmanager"
elif [ -f "$SDK_DIR/cmdline-tools/bin/avdmanager" ]; then
    AVDMANAGER="$SDK_DIR/cmdline-tools/bin/avdmanager"
    SDKMANAGER="$SDK_DIR/cmdline-tools/bin/sdkmanager"
elif [ -f "$SDK_DIR/tools/bin/avdmanager" ]; then
    AVDMANAGER="$SDK_DIR/tools/bin/avdmanager"
    SDKMANAGER="$SDK_DIR/tools/bin/sdkmanager"
fi

if [ -z "$AVDMANAGER" ] || [ ! -f "$AVDMANAGER" ]; then
    echo "❌ ERROR: avdmanager not found!"
    echo ""
    echo "Please install Android SDK Command-line Tools:"
    echo "1. Open Android Studio"
    echo "2. Tools > SDK Manager"
    echo "3. SDK Tools tab"
    echo "4. Check 'Android SDK Command-line Tools (latest)'"
    echo "5. Click Apply"
    echo ""
    echo "Then run this script again."
    exit 1
fi

echo "✅ Found avdmanager at: $AVDMANAGER"
echo ""

# Check for system images
echo "Checking for system images..."
echo ""

# List installed system images
INSTALLED_IMAGES=$($SDKMANAGER --list_installed 2>/dev/null | grep "system-images" | grep "android-34" || echo "")

if [ -z "$INSTALLED_IMAGES" ]; then
    echo "⚠️  API 34 system image not found!"
    echo ""
    echo "Installing API 34 system image..."
    echo "This may take a few minutes..."
    echo ""
    
    # Install system image
    $SDKMANAGER "system-images;android-34;google_apis;x86_64" --channel=0
    
    if [ $? -ne 0 ]; then
        echo ""
        echo "❌ Failed to install system image"
        echo ""
        echo "You can install it manually:"
        echo "1. Open Android Studio"
        echo "2. Tools > SDK Manager"
        echo "3. SDK Platforms tab > Check 'Android 14.0 (API 34)'"
        echo "4. Click Apply"
        echo ""
        echo "Or try running:"
        echo "  $SDKMANAGER \"system-images;android-34;google_apis;x86_64\""
        exit 1
    fi
    
    echo ""
    echo "✅ System image installed!"
    echo ""
fi

# List available device definitions
echo "Available device definitions:"
$AVDMANAGER list device 2>/dev/null | grep -E "id:|Name:" | head -n 20
echo ""

# Create AVD
AVD_NAME="Pixel_7_API_34"
echo "Creating AVD: $AVD_NAME"
echo ""

# Try to create with Pixel 7 device
echo "Attempting to create AVD with Pixel 7 device..."
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
    echo "  cd android-app"
    echo "  ./start-emulator.sh"
    echo ""
    echo "Or directly:"
    echo "  $SDK_DIR/emulator/emulator.exe -avd $AVD_NAME"
    exit 0
fi

# If Pixel 7 failed, try generic device
echo ""
echo "⚠️  Could not create with Pixel 7 device. Trying generic device..."
echo ""

$AVDMANAGER create avd \
    -n "$AVD_NAME" \
    -k "system-images;android-34;google_apis;x86_64" \
    --force

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ AVD created successfully with generic device!"
    echo ""
    echo "AVD Name: $AVD_NAME"
    echo "Location: $ANDROID_AVD_HOME"
    echo ""
    echo "To start the emulator, run:"
    echo "  cd android-app"
    echo "  ./start-emulator.sh"
    exit 0
else
    echo ""
    echo "❌ Failed to create AVD"
    echo ""
    echo "Please check:"
    echo "1. System image is installed (run: $SDKMANAGER --list_installed | grep system-images)"
    echo "2. You have write permissions to: $ANDROID_AVD_HOME"
    echo ""
    echo "Or create AVD manually in Android Studio:"
    echo "  Tools > Device Manager > Create Device"
    exit 1
fi

