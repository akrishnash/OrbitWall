# APK Directory

This directory contains the latest build of the OrbitWall Android app.

## Usage

- **OrbitWall.apk** - The latest APK build (replaced automatically after each build)
- Download this APK directly from GitHub to install on your Android device
- The APK is tracked using Git LFS for efficient version control

## Building

The APK is automatically copied here when you build the project:

```bash
cd android-app
./gradlew assembleDebug
```

Or build and copy manually:

```bash
cd android-app
./gradlew copyApkToRoot
```

## Installation

1. Download `OrbitWall.apk` from this directory
2. On your Android device, enable "Install from Unknown Sources" in Settings
3. Open the downloaded APK file to install
