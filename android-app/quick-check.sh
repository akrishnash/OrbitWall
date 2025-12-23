#!/bin/bash
# Quick check for existing AVDs - run this first!

SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"

echo "Quick Check for Existing AVDs..."
echo ""

# Check if emulator can list AVDs
if [ -f "$SDK_DIR/emulator/emulator.exe" ]; then
    echo "📱 Checking for existing AVDs..."
    AVDS=$("$SDK_DIR/emulator/emulator.exe" -list-avds 2>/dev/null)
    
    if [ -n "$AVDS" ]; then
        echo "✅ Found existing AVDs!"
        echo "$AVDS"
        echo ""
        echo "You can start one immediately with:"
        echo "  cd android-app"
        echo "  ./start-emulator.sh"
        exit 0
    else
        echo "❌ No AVDs found"
    fi
fi

echo ""
echo "💡 TIP: If download is taking too long, cancel it (Ctrl+C) and:"
echo "   1. Use Android Studio: Tools > Device Manager > Create Device"
echo "   2. It's usually faster and shows progress better"
echo ""

