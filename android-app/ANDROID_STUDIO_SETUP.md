# Step-by-Step Guide: Running OrbitWall in Android Studio

## Prerequisites Check

Before starting, ensure you have:
- ✅ Android Studio Hedgehog (2023.1.1) or newer
- ✅ JDK 17 or higher installed
- ✅ Android SDK with API 29+ installed
- ✅ Project imported into Android Studio

---

## Step 1: Open Project in Android Studio

1. Launch **Android Studio**
2. If you haven't already, select **File → Open**
3. Navigate to and select the `android-app` folder
4. Click **OK** to open the project

---

## Step 2: Wait for Gradle Sync

1. Android Studio will automatically start **Gradle Sync**
2. You'll see a progress bar at the bottom: "Gradle sync in progress..."
3. **Wait for it to complete** (this may take 2-5 minutes on first run)
4. If you see any errors, see **Troubleshooting** section below

---

## Step 3: Check Project Structure

Verify the project structure looks correct:
- `app/` module should be visible
- `app/src/main/java/com/orbitwall/` should contain your Kotlin files
- `app/src/main/res/` should contain resources

---

## Step 4: Configure SDK and Build Tools

1. Go to **File → Project Structure** (or press `Ctrl+Alt+Shift+S` on Windows/Linux, `Cmd+;` on Mac)
2. In the left panel, select **Project**
3. Verify:
   - **SDK Location**: Points to your Android SDK
   - **Gradle Version**: Should be 8.13+ (configured in `gradle-wrapper.properties`)
   - **Android Gradle Plugin Version**: Should be 8.13.2 (already set in `build.gradle.kts`)
4. Click **OK**

---

## Step 5: Set Up an Android Virtual Device (AVD)

If you don't have an emulator set up:

1. Click the **Device Manager** icon in the toolbar (or **Tools → Device Manager**)
2. Click **Create Device**
3. Select a device (e.g., **Pixel 7** or **Pixel 6**)
4. Click **Next**
5. Select a **System Image**:
   - Choose **API 34** (Android 14) or **API 33** (Android 13)
   - If not downloaded, click **Download** next to the API level
   - Wait for download to complete
6. Click **Next**
7. Review configuration and click **Finish**

---

## Step 6: Create Run Configuration

1. Look at the top toolbar for the **Run Configuration** dropdown
2. It should show **"app"** by default
3. If not, click the dropdown and select **"app"**
4. If "app" doesn't appear:
   - Click **Run → Edit Configurations...**
   - Click the **+** button and select **Android App**
   - Name it: `OrbitWall`
   - Module: Select **app**
   - Launch: Select **Default Activity**
   - Click **OK**

---

## Step 7: Select Target Device

1. In the toolbar, next to the Run button, you'll see a device selector
2. Click the dropdown
3. Select either:
   - Your **AVD** (emulator) you created in Step 5, OR
   - A **physical device** connected via USB (with USB debugging enabled)

---

## Step 8: Build and Run

### Option A: Using the Run Button
1. Click the green **▶ Run** button in the toolbar (or press `Shift+F10`)
2. Android Studio will:
   - Build the project (first build may take 2-5 minutes)
   - Install the app on your device/emulator
   - Launch the app

### Option B: Using Gradle
1. Open the **Terminal** tab at the bottom of Android Studio
2. Run: `./gradlew assembleDebug` (or `gradlew.bat assembleDebug` on Windows)
3. Then: `./gradlew installDebug` (or `gradlew.bat installDebug` on Windows)

---

## Step 9: First Launch

When the app launches:
1. You should see the **Login Screen** with email and password fields
2. Enter any email and password (authentication is currently local-only)
3. Click **Login** to proceed
4. You'll see the **Gallery** with predefined regions
5. Tap any region to open the **Editor**
6. In the editor, you can adjust settings and save wallpapers

---

## Troubleshooting

### Issue: Gradle Sync Failed

**Error: "Could not resolve all dependencies"**
- **Solution**: 
  1. Go to **File → Settings → Build, Execution, Deployment → Build Tools → Gradle**
  2. Check **"Use Gradle from"** - should be set to a valid Gradle version
  3. Click **Apply** and try **File → Sync Project with Gradle Files**

**Error: "JDK not found"**
- **Solution**:
  1. Go to **File → Project Structure → SDK Location**
  2. Set **JDK location** to your JDK 17+ installation path
  3. Click **OK** and sync again

### Issue: Build Failed

**Error: "Kotlin version mismatch"**
- **Solution**: The project uses Kotlin 1.9.24. Android Studio should handle this automatically, but if issues persist:
  1. Go to **File → Settings → Plugins**
  2. Ensure **Kotlin** plugin is installed and up to date
  3. Sync project again

**Error: "compileSdk 34 not found"**
- **Solution**:
  1. Go to **Tools → SDK Manager**
  2. In the **SDK Platforms** tab, check **Android 14.0 (API 34)**
  3. Click **Apply** to download
  4. Sync project again

### Issue: App Crashes on Launch

**Check Logcat:**
1. Open **Logcat** tab at the bottom
2. Look for red error messages
3. Common issues:
   - **Missing resources**: Check if `strings.xml` and `colors.xml` exist
   - **Permission denied**: Ensure `INTERNET` permission is in manifest (already added)
   - **ClassNotFoundException**: Clean and rebuild: **Build → Clean Project**, then **Build → Rebuild Project**

### Issue: No Devices Available

**For Physical Device:**
1. Enable **Developer Options** on your phone:
   - Go to **Settings → About Phone**
   - Tap **Build Number** 7 times
2. Enable **USB Debugging**:
   - Go to **Settings → Developer Options**
   - Enable **USB Debugging**
3. Connect phone via USB
4. Accept the USB debugging prompt on your phone
5. Device should appear in Android Studio

**For Emulator:**
- Follow Step 5 above to create an AVD

---

## Additional Configuration (Optional)

### Enable ProGuard/R8 (for Release Builds)

If you want to enable code shrinking for release builds:
1. Open `app/build.gradle.kts`
2. In `buildTypes.release`, change `isMinifyEnabled = false` to `true`
3. Add ProGuard rules if needed

### Change App Icon

1. Right-click `app/src/main/res` → **New → Image Asset**
2. Follow the wizard to create app icons
3. Replace the default icon

### Add Signing Configuration

For release builds, you'll need a signing config:
1. Go to **File → Project Structure → Modules → app → Signing Configs**
2. Add your keystore configuration

---

## Quick Reference

| Action | Shortcut (Windows/Linux) | Shortcut (Mac) |
|--------|-------------------------|----------------|
| Run App | `Shift+F10` | `Ctrl+R` |
| Build Project | `Ctrl+F9` | `Cmd+F9` |
| Sync Gradle | `Ctrl+Shift+O` | `Cmd+Shift+O` |
| Open Project Structure | `Ctrl+Alt+Shift+S` | `Cmd+;` |
| Open Logcat | `Alt+6` | `Cmd+6` |

---

## Next Steps

Once the app is running:
- ✅ Test login functionality
- ✅ Browse the gallery
- ✅ Generate and save wallpapers
- ✅ Test different regions and settings

If you encounter any issues not covered here, check the **Logcat** output for detailed error messages.

