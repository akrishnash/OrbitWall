# Running Emulator on E Drive (Disk Space Solution)

## Quick Start

The helper scripts have been configured to automatically use the E drive for emulator storage.

### Start Emulator (Git Bash):
```bash
cd /e/anurag/OrbitWall/android-app
./start-emulator.sh
```

### Start Emulator (Windows CMD):
```cmd
cd E:\anurag\OrbitWall\android-app
start-emulator.bat
```

## What Changed

The scripts now set:
- `ANDROID_AVD_HOME=E:\anurag\.android\avd` - Where AVD files are stored
- `ANDROID_SDK_HOME=E:\anurag\.android` - Android configuration directory

This prevents "not enough disk space" errors by using E drive instead of C drive.

## If You Have Existing AVDs on C Drive

If you already created AVDs in Android Studio before this setup:

### Option 1: Create New AVD on E Drive (Recommended)
1. Run the helper script - it will create new AVDs on E drive automatically
2. Or create new AVDs from command line (they'll use E drive):
   ```bash
   export ANDROID_AVD_HOME="/e/anurag/.android/avd"
   avdmanager create avd -n MyDevice -k "system-images;android-34;google_apis;x86_64"
   ```

### Option 2: Move Existing AVDs
If you want to move existing AVDs from C drive to E drive:

1. **Stop any running emulators**

2. **Copy AVD files**:
   ```powershell
   # PowerShell
   $source = "$env:USERPROFILE\.android\avd"
   $dest = "E:\anurag\.android\avd"
   New-Item -ItemType Directory -Force -Path $dest
   Copy-Item "$source\*" "$dest\" -Recurse -Force
   ```

3. **Update AVD config files**: Edit each `.ini` file in `E:\anurag\.android\avd\` to update the `path` to point to E drive

4. **Delete old AVDs** (optional, after verifying new location works):
   ```powershell
   Remove-Item "$env:USERPROFILE\.android\avd" -Recurse -Force
   ```

## Verify It's Working

After starting an emulator, verify it's using E drive:
```bash
# Check where AVDs are stored
ls /e/anurag/.android/avd/

# Or in PowerShell
dir E:\anurag\.android\avd\
```

## Creating New AVD from Command Line

If you need to create a new AVD and want it on E drive:

```bash
# Set environment variables (already done by the script)
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"

# List available system images
sdkmanager --list | grep system-images

# Create AVD (example for Pixel 7 API 34)
avdmanager create avd \
  -n Pixel7_API34 \
  -k "system-images;android-34;google_apis;x86_64" \
  -d "pixel_7"
```

## Troubleshooting

**Q: Script says "No AVDs found" but I created one in Android Studio?**
A: Android Studio might have created it on C drive. Either:
- Create a new one using the script (it will use E drive)
- Move the existing one (see Option 2 above)

**Q: Still getting disk space errors?**
A: Make sure you're using the updated script. Check that `ANDROID_AVD_HOME` is set:
```bash
echo $ANDROID_AVD_HOME
# Should show: /e/anurag/.android/avd
```

**Q: Can I use a different drive?**
A: Yes! Just change the paths in `start-emulator.sh` or `start-emulator.bat`:
- For E drive: `/e/anurag/.android/avd`
- For D drive: `/d/your-path/.android/avd`
- etc.

