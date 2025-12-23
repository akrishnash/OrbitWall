# Fast AVD Setup - Quick Options

## Option 1: Use Android Studio (FASTEST - ~2 minutes)

If the command-line download is taking too long, use Android Studio:

1. **Open Android Studio**
2. **Tools > Device Manager**
3. **Create Device** (or **+** button)
4. **Select Pixel 7** > Next
5. **Download API 34** (if needed - usually faster than command line)
6. **Finish**

Then start it with:
```bash
cd android-app
./start-emulator.sh
```

## Option 2: Use Smaller System Image (Faster Download)

Instead of the full Google APIs image, use a smaller one:

```bash
# Use Google Play system image (smaller, faster)
sdkmanager "system-images;android-34;google_apis_playstore;x86_64"

# Or use a smaller API level (API 33 is smaller)
sdkmanager "system-images;android-33;google_apis;x86_64"
```

Then create AVD with that image.

## Option 3: Check if System Image Already Exists

You might already have a system image installed:

```bash
SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
$SDK_DIR/cmdline-tools/latest/bin/sdkmanager --list_installed | grep system-images
```

If you see any system images, you can use them directly without downloading!

## Option 4: Use Existing AVD

Check if you already have an AVD created:

```bash
SDK_DIR="/c/Users/anurag/AppData/Local/Android/Sdk"
$SDK_DIR/emulator/emulator.exe -list-avds
```

If you see any AVDs listed, you can start them directly!

## Quick Start (If AVD Already Exists)

```bash
cd android-app
./start-emulator.sh
```

This will automatically find and start any existing AVDs.

