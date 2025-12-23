#!/bin/bash
# Reinstall app and launch it

echo "========================================"
echo "Reinstalling and Launching OrbitWall"
echo "========================================"
echo ""

# Uninstall old version
echo "Uninstalling old version..."
adb uninstall com.orbitwall 2>/dev/null || echo "  (not installed yet)"

# Install new version
echo ""
echo "Installing new version..."
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo ""
        echo "✅ App installed!"
    echo ""
    
    # Wait a moment for installation to complete
    sleep 2
    
    echo "Launching app..."
    adb shell am start -n com.orbitwall/.MainActivity
    
    echo ""
    echo "✅ App should now be visible on your device!"
    echo ""
    echo "If you see a blank screen, check logs with:"
    echo "  adb logcat | grep -E 'OrbitWall|AndroidRuntime'"
    echo ""
    echo "If you still see a blank screen, check logs with:"
    echo "  adb logcat | grep OrbitWall"
else
    echo ""
    echo "❌ Installation failed"
    exit 1
fi

