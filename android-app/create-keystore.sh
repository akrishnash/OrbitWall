#!/bin/bash
# Script to create keystore for Play Store release signing

echo "========================================"
echo "OrbitWall - Create Release Keystore"
echo "========================================"
echo ""
echo "This will create a keystore file for signing your release builds."
echo "You'll need this keystore for ALL future Play Store updates."
echo ""
echo "IMPORTANT: Keep this keystore file and passwords safe!"
echo ""

cd "$(dirname "$0")"

# Create keystore directory if it doesn't exist
mkdir -p keystore

echo "Creating keystore..."
echo ""
keytool -genkey -v -keystore keystore/orbitwall-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias orbitwall

if [ $? -ne 0 ]; then
    echo ""
    echo "ERROR: Failed to create keystore!"
    echo "Make sure Java is installed and keytool is in your PATH."
    exit 1
fi

echo ""
echo "========================================"
echo "Keystore created successfully!"
echo "========================================"
echo ""
echo "Next steps:"
echo "1. Copy keystore.properties.template to keystore.properties"
echo "2. Edit keystore.properties with your keystore passwords"
echo "3. Run: ./gradlew bundleRelease"
echo ""
echo "Keystore location: keystore/orbitwall-release.jks"
echo ""

