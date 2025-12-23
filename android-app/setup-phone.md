# Setup Your Phone for Android Development

## Quick Setup Steps

### 1. Enable Developer Options on Your Phone

1. Go to **Settings** > **About Phone**
2. Find **Build Number** (or **MIUI Version** on Xiaomi)
3. Tap it **7 times** until you see "You are now a developer!"

### 2. Enable USB Debugging

1. Go back to **Settings** > **Developer Options**
2. Turn on **Developer Options** (toggle at top)
3. Enable **USB Debugging**
4. (Optional but recommended) Enable **Stay Awake** (keeps screen on while charging)

### 3. Connect Your Phone

1. Connect phone to computer via USB cable
2. On your phone, you'll see a popup asking "Allow USB debugging?"
3. Check **"Always allow from this computer"**
4. Tap **Allow**

### 4. Verify Connection

In Git Bash, run:
```bash
adb devices
```

You should see your device listed, for example:
```
List of devices attached
ABC123XYZ    device
```

## Benefits of Using Your Phone

✅ **Much faster** - No emulator startup time
✅ **More reliable** - No connection issues
✅ **Real device testing** - See how it works on actual hardware
✅ **Instant install** - Faster than emulator
✅ **Better performance** - Native speed

## Install App on Your Phone

Once connected:

```bash
cd android-app
./gradlew installDebug
```

The app will install directly on your phone!

## Troubleshooting

**Device not showing?**
1. Make sure USB debugging is enabled
2. Try a different USB cable (data cable, not charge-only)
3. Try different USB port on computer
4. Restart ADB: `adb kill-server && adb start-server`
5. Re-authorize USB debugging on phone

**Still not working?**
- Check if phone manufacturer drivers are installed (Samsung, Xiaomi, etc.)
- Enable "USB Configuration" > "File Transfer" or "MTP" mode







