# Running Android App from Cursor Terminal

This guide shows you how to build, install, and run the Android app directly from Cursor's terminal without opening Android Studio.

## Prerequisites

1. **Java 11+ (JDK 17 recommended)** - Required for Android Gradle Plugin 8.13.2
   - Verify: `java -version` should show Java 11 or higher
   - If you see Java 8, see troubleshooting section below
2. **Android SDK** - Should be installed with Android Studio
3. **ADB (Android Debug Bridge)** - Usually comes with Android Studio
4. **Connected Device or Emulator** - Physical device with USB debugging OR Android emulator

## Quick Start

### Windows (PowerShell/CMD)

```bash
cd android-app
.\build-and-run.bat
```

### Mac/Linux

```bash
cd android-app
chmod +x build-and-run.sh
./build-and-run.sh
```

## Manual Steps

If the scripts don't work, follow these steps:

### 1. Navigate to Project

```bash
cd android-app
```

### 2. Check for Connected Device

```bash
# Windows
adb devices

# Mac/Linux  
adb devices
```

You should see your device listed. If not:
- **Physical device**: Enable USB debugging in Developer Options
- **Emulator**: Start an emulator from Android Studio first

### 3. Build the APK

```bash
# Windows
gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

This creates: `app/build/outputs/apk/debug/app-debug.apk`

### 4. Install on Device

```bash
# Windows
gradlew.bat installDebug

# Mac/Linux
./gradlew installDebug
```

### 5. Launch the App

```bash
adb shell am start -n com.orbitwall/.MainActivity
```

Or just open the app manually on your device.

## Common Commands

### Build Only (No Install)
```bash
# Windows
gradlew.bat assembleDebug

# Mac/Linux
./gradlew assembleDebug
```

### Clean Build
```bash
# Windows
gradlew.bat clean assembleDebug

# Mac/Linux
./gradlew clean assembleDebug
```

### View Logs (while app is running)
```bash
adb logcat | grep -i orbitwall
```

### Uninstall App
```bash
adb uninstall com.orbitwall
```

### List All Installed Packages
```bash
adb shell pm list packages | grep orbitwall
```

## Troubleshooting

### "Java version mismatch" or "requires Java 11" error

**Error**: `No matching variant of com.android.tools.build:gradle:8.13.2 was found... compatible with Java 11 and the consumer needed... compatible with Java 8`

**Cause**: Android Gradle Plugin 8.13.2 requires Java 11+ to run, but your system is using Java 8.

**Solution**: Install Java 17 (recommended to match your app's compile target):

**Windows (using Git Bash/MINGW64):**
```bash
# Check current Java version
java -version

# Install Java 17 using a package manager or download from:
# https://adoptium.net/ or https://www.oracle.com/java/technologies/downloads/#java17

# After installation, set JAVA_HOME (replace path with your actual installation)
export JAVA_HOME="/c/Program Files/Java/jdk-17"
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version  # Should show Java 17
```

**Windows (PowerShell/CMD):**
```powershell
# Set JAVA_HOME (replace path with your actual installation)
[System.Environment]::SetEnvironmentVariable('JAVA_HOME', 'C:\Program Files\Java\jdk-17', 'User')
$env:JAVA_HOME = 'C:\Program Files\Java\jdk-17'
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verify
java -version
```

**Mac/Linux:**
```bash
# Using Homebrew (Mac)
brew install openjdk@17

# Set JAVA_HOME
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export PATH="$JAVA_HOME/bin:$PATH"

# Or manually set (adjust path as needed)
export JAVA_HOME="/usr/lib/jvm/java-17-openjdk"
export PATH="$JAVA_HOME/bin:$PATH"

# Verify
java -version
```

**Alternative**: If you have multiple Java versions installed, you can specify which one Gradle should use:
```bash
# Set JAVA_HOME before running gradlew
export JAVA_HOME="/path/to/java-17"
./gradlew tasks
```

**Note**: Android Studio typically manages its own JDK, but when running Gradle from command line, you need to ensure your system Java is version 11+.

### "No connection to gradle server"

**Solution**: The Gradle wrapper version was updated. Try:

```bash
# Windows
gradlew.bat --stop
gradlew.bat assembleDebug

# Mac/Linux
./gradlew --stop
./gradlew assembleDebug
```

### "Command not found: adb"

**Solution**: Add Android SDK platform-tools to your PATH:

**Windows:**
```powershell
# Usually located at:
$env:Path += ";C:\Users\YourName\AppData\Local\Android\Sdk\platform-tools"
```

**Mac/Linux:**
```bash
# Add to ~/.bashrc or ~/.zshrc
export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools
# OR
export PATH=$PATH:~/Android/Sdk/platform-tools
```

### "Gradle daemon not running"

**Solution**: Start Gradle daemon:
```bash
# Windows
gradlew.bat --daemon

# Mac/Linux
./gradlew --daemon
```

### Build Fails with Dependency Errors

**Solution**: 
1. Check internet connection (Gradle downloads dependencies)
2. Try cleaning and rebuilding:
```bash
# Windows
gradlew.bat clean build

# Mac/Linux
./gradlew clean build
```

### Device Not Found

**Solutions**:
1. **Physical Device**:
   - Enable Developer Options (tap Build Number 7 times)
   - Enable USB Debugging
   - Accept USB debugging prompt on device
   - Try different USB cable/port

2. **Emulator**:
   - **Option 1 - Using Helper Script** (Recommended):
     ```bash
     # Git Bash/Mac/Linux
     chmod +x start-emulator.sh
     ./start-emulator.sh
     
     # Windows CMD/PowerShell
     start-emulator.bat
     ```
     The script automatically configures AVD storage on E drive to avoid disk space issues.
   - **Option 2 - From Android Studio**:
     - Start emulator from Android Studio Device Manager
     - Wait for it to fully boot
   - **Option 3 - Manual Command**:
     ```bash
     # Set AVD home to E drive (for Git Bash)
     export ANDROID_AVD_HOME="/e/anurag/.android/avd"
     export ANDROID_SDK_HOME="/e/anurag/.android"
     mkdir -p "$ANDROID_AVD_HOME"
     
     # List available AVDs
     emulator -list-avds
     
     # Start specific emulator (replace AVD_NAME with actual name)
     emulator -avd AVD_NAME &
     ```
   - Wait for emulator to boot completely (30-60 seconds)
   - Run `adb devices` to verify it's connected

### "Not enough disk space" Error

**Error**: `Your device does not have enough disk space to run avd`

**Solution**: The helper scripts (`start-emulator.sh` and `start-emulator.bat`) automatically configure the emulator to store AVD files on the E drive. This prevents disk space issues on C drive.

**If you have existing AVDs on C drive and want to move them**:
1. **Copy existing AVDs** (if any):
   ```bash
   # Windows PowerShell
   Copy-Item "$env:USERPROFILE\.android\avd\*" "E:\anurag\.android\avd\" -Recurse -Force
   ```
2. **Set environment variable permanently** (optional):
   - Windows: Add `ANDROID_AVD_HOME=E:\anurag\.android\avd` to System Environment Variables
   - Or use the scripts which set it automatically

**Create new AVDs on E drive**:
- The scripts will automatically use E drive for new AVDs
- If creating from Android Studio, you may need to manually move them or use command line tools

## Development Workflow

1. **Make code changes** in Cursor
2. **Build**: `gradlew.bat assembleDebug` (Windows) or `./gradlew assembleDebug` (Mac/Linux)
3. **Install**: `gradlew.bat installDebug` (Windows) or `./gradlew installDebug` (Mac/Linux)
4. **Test** on device
5. **View logs**: `adb logcat` if needed
6. **Repeat**

## Hot Reload / Fast Iteration

For faster iteration during development:

### Quick Rebuild Script (Recommended)
Use the helper script for fastest rebuild:
```bash
# Git Bash/Mac/Linux
./quick-rebuild.sh

# This will:
# - Check for connected device
# - Build (incremental - faster than full build)
# - Install on device
# - Show launch command
```

### Manual Method
1. Keep the app running on your device/emulator
2. After code changes, rebuild and install:
   ```bash
   # Windows
   gradlew.bat installDebug
   
   # Mac/Linux
   ./gradlew installDebug
   ```
3. **Restart the app** to see changes (tap home, then reopen OrbitWall, or use):
   ```bash
   adb shell am force-stop com.orbitwall
   adb shell am start -n com.orbitwall/.MainActivity
   ```

**Note**: Android doesn't have true hot reload like React Native. For UI changes:
- Small changes (text, colors): May appear after restarting the app
- Major changes (layout, new screens): Full rebuild is usually needed
- Compose UI: Changes require rebuild and app restart

## Building Release APK

For a release build (signed APK):

```bash
# Windows
gradlew.bat assembleRelease

# Mac/Linux
./gradlew assembleRelease
```

**Note**: Release builds require signing configuration. See Android documentation for setting up keystores.

## Next Steps

- Edit code in Cursor
- Build and test
- Iterate quickly without Android Studio!

