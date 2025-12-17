#!/bin/bash
# Start Android Emulator Helper Script

# SDK location from local.properties
SDK_DIR="/c/Users/aks/AppData/Local/Android/Sdk"
EMULATOR="$SDK_DIR/emulator/emulator.exe"
ADB="$SDK_DIR/platform-tools/adb.exe"

# Set AVD home to E drive to save disk space
# This tells the emulator to store AVD files on E drive instead of C drive
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

# Create directory if it doesn't exist
mkdir -p "$ANDROID_AVD_HOME"
mkdir -p "$ANDROID_SDK_HOME"

# Convert Windows paths to Unix-style for Git Bash
export PATH="$SDK_DIR/emulator:$SDK_DIR/platform-tools:$SDK_DIR/tools:$SDK_DIR/tools/bin:$PATH"

echo "========================================"
echo "Android Emulator Helper"
echo "========================================"
echo ""

# Check if SDK exists
if [ ! -f "$EMULATOR" ]; then
    echo "ERROR: Android SDK not found at: $SDK_DIR"
    echo "Please install Android Studio and SDK first."
    exit 1
fi

# List available AVDs
echo "Available Android Virtual Devices (AVDs):"
echo "----------------------------------------"
"$SDK_DIR/emulator/emulator.exe" -list-avds 2>/dev/null || echo "No AVDs found. Create one in Android Studio: Tools > Device Manager > Create Device"

echo ""
echo ""

# Check if emulator is already running
if "$ADB" devices 2>/dev/null | grep -q "emulator.*device$"; then
    echo "✅ Emulator is already running!"
    "$ADB" devices
    exit 0
fi

# Get list of AVDs - first try E drive, then default location
AVDS=$("$EMULATOR" -list-avds 2>/dev/null)

# If no AVDs found on E drive, check default C drive location
if [ -z "$AVDS" ]; then
    echo "Checking default AVD location on C drive..."
    DEFAULT_AVD_HOME="/c/Users/aks/.android/avd"
    
    # Check if default location exists and has AVD files
    if [ -d "$DEFAULT_AVD_HOME" ]; then
        # Look for .ini files which indicate AVDs
        AVD_INI_FILES=$(ls "$DEFAULT_AVD_HOME"/*.ini 2>/dev/null)
        
        if [ -n "$AVD_INI_FILES" ]; then
            echo "✅ Found AVDs on C drive!"
            echo ""
            
            # Extract AVD names from .ini filenames
            AVDS=""
            for ini_file in "$DEFAULT_AVD_HOME"/*.ini; do
                if [ -f "$ini_file" ]; then
                    avd_name=$(basename "$ini_file" .ini)
                    echo "  - $avd_name"
                    if [ -z "$AVDS" ]; then
                        AVDS="$avd_name"
                    else
                        AVDS="$AVDS"$'\n'"$avd_name"
                    fi
                fi
            done
            
            echo ""
            echo "⚠️  Note: These AVDs are on C drive. Temporarily using C drive location."
            echo "For better disk space usage, you can move them to E drive later."
            echo "(See CREATE_AVD_GUIDE.md for instructions)"
            echo ""
            
            # Temporarily use C drive location for this session
            export ANDROID_AVD_HOME="$DEFAULT_AVD_HOME"
        fi
    fi
fi

if [ -z "$AVDS" ]; then
    echo "❌ No AVDs found!"
    echo ""
    echo "AVD Home is set to: $ANDROID_AVD_HOME"
    echo ""
    echo "To create an AVD on E drive, you have two options:"
    echo ""
    echo "Option 1 - Use the helper script (recommended):"
    echo "  chmod +x create-avd.sh"
    echo "  ./create-avd.sh"
    echo ""
    echo "Option 2 - Using Android Studio:"
    echo "  1. Open Android Studio"
    echo "  2. Go to Tools > Device Manager"
    echo "  3. Click 'Create Device'"
    echo "  4. Select a device (e.g., Pixel 7)"
    echo "  5. Choose a system image (API 34 recommended)"
    echo "  6. Finish the wizard"
    echo "  Note: If created in Android Studio, it may be on C drive."
    echo "        You'll need to move it or recreate using Option 1."
    exit 1
fi

# Count AVDs
AVD_COUNT=$(echo "$AVDS" | wc -l)

if [ "$AVD_COUNT" -eq 1 ]; then
    # Only one AVD, start it
    AVD_NAME=$(echo "$AVDS" | head -n 1)
    echo "Starting emulator: $AVD_NAME"
    echo ""
    # If AVD is on C drive, temporarily allow access to it
    # but still use E drive for any new files
    echo "Note: Environment is configured to use E drive for storage"
    echo ""
    "$EMULATOR" -avd "$AVD_NAME" &
    echo "Emulator is starting in the background..."
    echo "Wait for it to boot completely (this may take 30-60 seconds)"
    echo ""
    echo "Check status with: adb devices"
else
    # Multiple AVDs, show list
    echo "Multiple AVDs found. Please select one:"
    echo ""
    echo "$AVDS" | nl
    echo ""
    echo "To start an emulator, run:"
    echo "  $EMULATOR -avd <AVD_NAME>"
    echo ""
    echo "Or specify the AVD name:"
    echo "  ./start-emulator.sh <AVD_NAME>"
    
    # If AVD name provided as argument, start it
    if [ -n "$1" ]; then
        echo ""
        echo "Starting emulator: $1"
        "$EMULATOR" -avd "$1" &
        echo "Emulator is starting in the background..."
    fi
fi
