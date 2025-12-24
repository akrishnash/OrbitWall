# Play Store Upload Setup Guide

## Step 1: Generate Keystore

First, you need to create a keystore file for signing your release builds. **Keep this keystore safe - you'll need it for all future updates!**

### On Windows (PowerShell):
```powershell
cd android-app
keytool -genkey -v -keystore keystore/orbitwall-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias orbitwall
```

### On Linux/Mac:
```bash
cd android-app
keytool -genkey -v -keystore keystore/orbitwall-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias orbitwall
```

**Important:**
- Store the keystore file in `android-app/keystore/` directory
- Remember your passwords - you'll need them for every release
- Keep a backup of the keystore in a safe place
- The keystore file should NOT be committed to git

## Step 2: Configure Keystore Properties

1. Copy the template file:
   ```bash
   cp keystore.properties.template keystore.properties
   ```

2. Edit `keystore.properties` and fill in your actual values:
   ```
   storeFile=keystore/orbitwall-release.jks
   storePassword=YOUR_ACTUAL_KEYSTORE_PASSWORD
   keyAlias=orbitwall
   keyPassword=YOUR_ACTUAL_KEY_PASSWORD
   ```

3. **DO NOT commit `keystore.properties` to git!** It's already in `.gitignore`

## Step 3: Build Release AAB (Android App Bundle)

The Play Store prefers AAB (Android App Bundle) format over APK:

```bash
cd android-app
.\gradlew.bat bundleRelease
```

The AAB will be located at:
`android-app/app/build/outputs/bundle/release/app-release.aab`

## Step 4: Build Release APK (Alternative)

If you need an APK instead:

```bash
cd android-app
.\gradlew.bat assembleRelease
```

The APK will be at:
`android-app/app/build/outputs/apk/release/app-release.apk`

## Step 5: Update Version for Next Release

Before each new release, update in `app/build.gradle.kts`:
- `versionCode`: Increment by 1 (e.g., 1 → 2 → 3)
- `versionName`: Update version string (e.g., "1.0.0" → "1.0.1" → "1.1.0")

## Step 6: Play Store Console Checklist

### Required Information:
- [ ] App name: "OrbitWall"
- [ ] Short description (80 characters max)
- [ ] Full description (4000 characters max)
- [ ] App icon (512x512 PNG, no transparency)
- [ ] Feature graphic (1024x500 PNG)
- [ ] Screenshots (at least 2, up to 8)
  - Phone screenshots (required)
  - Tablet screenshots (optional)
- [ ] Privacy policy URL (if app collects data)
- [ ] Content rating questionnaire
- [ ] Target audience
- [ ] Data safety section

### App Details to Prepare:
- **Category**: Personalization / Wallpapers
- **Content Rating**: Complete questionnaire
- **Pricing**: Free or Paid
- **Countries**: Select distribution countries

## Step 7: Upload to Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create a new app or select existing
3. Go to "Production" → "Create new release"
4. Upload the AAB file (`app-release.aab`)
5. Fill in release notes
6. Review and publish

## Troubleshooting

### Build fails with "keystore.properties not found"
- Make sure you've created `keystore.properties` from the template
- Check that the file is in `android-app/` directory (not in `app/`)

### Build fails with signing errors
- Verify keystore file path is correct
- Check passwords match what you used when creating keystore
- Ensure keystore file exists at the specified path

### ProGuard/R8 errors
- Check `proguard-rules.pro` for any missing rules
- Some libraries may need specific ProGuard rules

## Security Notes

- **NEVER** commit `keystore.properties` or `.jks` files to git
- Store keystore backups in secure, encrypted locations
- Use strong passwords for keystore and key
- Consider using a password manager for keystore passwords

