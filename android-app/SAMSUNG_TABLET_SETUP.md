# Setup for Samsung Tablet

## Step-by-Step Guide

### Step 1: Enable Developer Options on Samsung Tablet

1. Open **Settings**
2. Scroll down and tap **About tablet** (or **About device**)
3. Find **Software information**
4. Tap on **Build number** 7 times
5. You'll see a message: "You are now a developer!"

### Step 2: Enable USB Debugging

1. Go back to **Settings**
2. You should now see **Developer options** (near bottom)
3. Tap **Developer options**
4. Turn ON the toggle at the top
5. Scroll down and enable:
   - **USB debugging**
   - (Optional) **Stay awake** (keeps screen on while charging)
   - (Optional) **Install via USB** (if available)

### Step 3: Connect Tablet to Computer

1. Connect your Samsung tablet to computer via USB cable
2. On your tablet, you'll see a popup: **"Allow USB debugging?"**
3. Check the box: **"Always allow from this computer"**
4. Tap **Allow**

**Note:** If you see "USB Configuration" popup, select **File Transfer** or **MTP**

### Step 4: Verify Connection

Open Git Bash and run:
```bash
adb devices
```

You should see your tablet listed, like:
```
List of devices attached
R58M123ABC    device
```

If you see **"unauthorized"**, check your tablet screen and tap **Allow** again.

### Step 5: Install the App

Once connected, run:
```bash
cd android-app
chmod +x install-to-phone.sh
./install-to-phone.sh
```

That's it! The app will install and launch on your tablet.

---

## Troubleshooting for Samsung Tablets

### Tablet not showing in `adb devices`?

1. **Check USB cable** - Use a data cable (not charge-only)
2. **Try different USB port** on your computer
3. **Check tablet screen** - Make sure you authorized USB debugging
4. **Restart ADB:**
   ```bash
   adb kill-server
   adb start-server
   adb devices
   ```

### Samsung-Specific Issues

- **Samsung drivers needed?** Usually Windows auto-installs, but if not working:
  - Download Samsung USB drivers from Samsung website
  - Or use Samsung Smart Switch (includes drivers)

- **"USB debugging not working" on Samsung?**
  - Go to Developer Options
  - Enable **"USB debugging (Security settings)"** if available
  - Enable **"Install via USB"** if available

- **Tablet shows "unauthorized"?**
  - Disconnect and reconnect USB
  - On tablet, revoke USB debugging authorizations: Settings > Developer Options > Revoke USB debugging authorizations
  - Reconnect and authorize again

### Still Not Working?

1. Make sure tablet is **unlocked** when connecting
2. Try **different USB mode** on tablet (pull down notification, change USB mode)
3. Enable **Developer Options** again (sometimes gets disabled)
4. Restart both tablet and computer






