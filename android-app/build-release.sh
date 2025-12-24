#!/bin/bash
# Build release AAB for Play Store upload

cd "$(dirname "$0")"

echo "========================================"
echo "OrbitWall - Building Release AAB"
echo "========================================"
echo ""

# Check if keystore.properties exists
if [ ! -f "keystore.properties" ]; then
    echo "WARNING: keystore.properties not found!"
    echo ""
    echo "You need to:"
    echo "1. Create keystore: run ./create-keystore.sh"
    echo "2. Copy keystore.properties.template to keystore.properties"
    echo "3. Edit keystore.properties with your passwords"
    echo ""
    echo "Building unsigned release (not suitable for Play Store)..."
    echo ""
    read -p "Press Enter to continue..."
fi

echo "Building release AAB..."
echo ""

./gradlew clean bundleRelease

if [ $? -ne 0 ]; then
    echo ""
    echo "BUILD FAILED!"
    echo "Check errors above."
    exit 1
fi

echo ""
echo "========================================"
echo "BUILD SUCCESSFUL!"
echo "========================================"
echo ""
echo "Release AAB location:"
echo "app/build/outputs/bundle/release/app-release.aab"
echo ""
echo "Upload this file to Google Play Console."
echo ""

