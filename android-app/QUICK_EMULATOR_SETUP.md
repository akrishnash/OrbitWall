# Quick Emulator Setup Guide

## Create an Android Virtual Device (AVD)

### Option 1: Using the Script (Recommended)

In Git Bash, run:

```bash
cd android-app
chmod +x create-avd.sh
./create-avd.sh
```

This will:
- Create an AVD on E drive (saves C drive space)
- Use Pixel 7 device with API 34 (Android 14)
- Set up everything automatically

### Option 2: Using Android Studio (Easier if script doesn't work)

1. Open **Android Studio**
2. Go to **Tools > Device Manager**
3. Click **Create Device** (or the **+** button)
4. Select **Pixel 7** (or any device)
5. Click **Next**
6. Download/Select **API 34** (Android 14.0) system image
7. Click **Next** > **Finish**

**Important**: After creating in Android Studio, the AVD might be on C drive. To use it with E drive, run:

```bash
cd android-app
./start-emulator.sh
```

The script will automatically set the environment to use E drive.

## Start the Emulator

After creating an AVD, start it with:

```bash
cd android-app
./start-emulator.sh
```

Or if you know the AVD name:

```bash
cd android-app
./start-emulator.sh Pixel_7_API_34
```

## Install and Run the App

Once the emulator is running:

```bash
cd android-app
./gradlew installDebug
```

The app will automatically install and launch on the emulator!

## Troubleshooting

**Q: Script says "avdmanager not found"**
A: Install Android SDK Command-line Tools:
   - Open Android Studio
   - Tools > SDK Manager > SDK Tools tab
   - Check "Android SDK Command-line Tools (latest)"
   - Click Apply

**Q: No system images found**
A: Install system images:
   - Open Android Studio
   - Tools > SDK Manager > SDK Platforms tab
   - Check "Android 14.0 (API 34)"
   - Click Apply

**Q: Emulator won't start**
A: Make sure you have enough RAM (at least 4GB free) and virtualization is enabled in BIOS.

