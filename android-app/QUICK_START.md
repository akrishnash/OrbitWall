# Quick Start Checklist

## ✅ Pre-Flight Check

- [ ] Android Studio Hedgehog (2023.1.1+) installed
- [ ] JDK 17+ installed and configured
- [ ] Android SDK with API 34 installed
- [ ] Project opened in Android Studio (`android-app` folder)

## ✅ Setup Steps (5 minutes)

1. **Wait for Gradle Sync** (2-5 min on first run)
   - Look for "Gradle sync finished" message
   - If errors appear, see troubleshooting below

2. **Create/Select Device**
   - Click **Device Manager** in toolbar
   - Create new AVD (Pixel 7, API 34) OR connect physical device
   - Enable USB debugging on physical device

3. **Run Configuration**
   - Top toolbar: Select **"app"** from Run Configuration dropdown
   - Select your device from device dropdown
   - Click **▶ Run** button (or press `Shift+F10`)

4. **First Build** (2-5 minutes)
   - Android Studio will download dependencies
   - Build the APK
   - Install and launch on device

## ✅ Verify It Works

When app launches:
- [ ] Login screen appears
- [ ] Enter any email/password → Click "Continue"
- [ ] Gallery screen shows regions
- [ ] Tap a region → Editor opens
- [ ] Preview loads (may take 10-30 seconds)
- [ ] Adjust sliders → Preview updates
- [ ] Click "Save" → Image saved to gallery

## 🚨 Common Issues & Quick Fixes

| Issue | Quick Fix |
|-------|-----------|
| **"Java 11 required" error** | Install Java 17 and set `JAVA_HOME`. See `CURSOR_RUN_GUIDE.md` troubleshooting |
| Gradle sync fails | **File → Invalidate Caches → Invalidate and Restart** |
| "SDK not found" | **Tools → SDK Manager → Install API 34** |
| "JDK not found" | **File → Project Structure → SDK Location → Set JDK path** |
| No devices | **Tools → Device Manager → Create Device** |
| App crashes | Check **Logcat** tab for error messages |
| Build takes forever | Normal on first build (downloads dependencies) |

## 📱 Testing the App

### Login Screen
- Email: `test@example.com`
- Password: `password123` (or anything)
- Click "Continue"

### Gallery Screen
- Scroll through regions
- Tap any region card to open editor

### Editor Screen
- Wait for preview to load (shows "Fetching satellite tiles...")
- Adjust **Zoom Offset** slider (-3 to +3)
- Adjust **Blur** (0-20px)
- Adjust **Brightness** (0.5-1.5)
- Adjust **Overlay** opacity (0-0.8)
- Select resolution: **Native**, **2K**, or **4K**
- Click **Save** to download high-res wallpaper

## 🔧 If Something's Wrong

1. **Check Logcat**: Bottom tab → Filter by "Error"
2. **Clean Build**: **Build → Clean Project** → **Build → Rebuild Project**
3. **Invalidate Caches**: **File → Invalidate Caches → Invalidate and Restart**
4. **Check Gradle**: **File → Sync Project with Gradle Files**

## 📚 Need More Help?

See `ANDROID_STUDIO_SETUP.md` for detailed instructions.

