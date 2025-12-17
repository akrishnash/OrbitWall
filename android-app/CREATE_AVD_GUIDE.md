# Creating an AVD on E Drive - Quick Guide

## Option 1: Using Android Studio (Recommended - Easier)

This is the simplest method if command-line tools aren't installed.

### Steps:

1. **Open Android Studio**

2. **Create the AVD**:
   - Go to **Tools > Device Manager**
   - Click **Create Device** (or the **+** button)
   - Select **Pixel 7** (or any device you prefer)
   - Click **Next**

3. **Select System Image**:
   - If API 34 is not downloaded, click **Download** next to it
   - Select **API 34** (Android 14.0)
   - Click **Next**

4. **Finish**:
   - Review the configuration
   - Click **Finish**

5. **Important**: The AVD might be created on C drive. To use it with E drive:

   **Before starting the emulator**, run in your terminal:
   ```bash
   export ANDROID_AVD_HOME="/e/anurag/.android/avd"
   export ANDROID_SDK_HOME="/e/anurag/.android"
   ```

   Or simply use the helper script which sets this automatically:
   ```bash
   ./start-emulator.sh
   ```

### Moving Existing AVD to E Drive (Optional)

If you already created an AVD in Android Studio and want to move it:

1. **Find your AVD**:
   - Usually at: `C:\Users\aks\.android\avd\`
   - Look for folders like `Pixel_7_API_34.avd`

2. **Copy to E drive**:
   ```powershell
   # PowerShell
   Copy-Item "$env:USERPROFILE\.android\avd\Pixel_7_API_34*" "E:\anurag\.android\avd\" -Recurse
   ```

3. **Update the .ini file**:
   - Edit `E:\anurag\.android\avd\Pixel_7_API_34.ini`
   - Change the `path` line to: `path=E:\anurag\.android\avd\Pixel_7_API_34.avd`

4. **Use the helper script**:
   ```bash
   ./start-emulator.sh
   ```

---

## Option 2: Using Command Line

Requires Android SDK Command-line Tools to be installed.

### Install Command-line Tools:

1. Open **Android Studio**
2. Go to **Tools > SDK Manager**
3. Click **SDK Tools** tab
4. Check **Android SDK Command-line Tools (latest)**
5. Click **Apply** to install

### Create AVD:

```bash
cd /e/anurag/OrbitWall/android-app
chmod +x create-avd.sh
./create-avd.sh
```

This will automatically:
- Set AVD home to E drive
- Check for system images
- Create the AVD on E drive

---

## Quick Start After AVD is Created

Once you have an AVD (created via either method):

```bash
cd /e/anurag/OrbitWall/android-app
./start-emulator.sh
```

This will:
- Set environment variables for E drive
- Start the emulator
- Avoid disk space issues

---

## Troubleshooting

**Q: Emulator still shows "not enough disk space"?**
A: Make sure you're using `./start-emulator.sh` which sets `ANDROID_AVD_HOME` to E drive. Don't start emulator directly from Android Studio UI without setting the environment variable first.

**Q: Can I set this permanently?**
A: Yes! Add to your `~/.bashrc` (Git Bash) or Windows Environment Variables:
```bash
export ANDROID_AVD_HOME="/e/anurag/.android/avd"
export ANDROID_SDK_HOME="/e/anurag/.android"
```

**Q: How do I verify AVD is on E drive?**
A: Check if files exist:
```bash
ls -la /e/anurag/.android/avd/
```

