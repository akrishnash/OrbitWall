# Play Store Upload Checklist

## Pre-Upload Requirements

### 1. App Information
- [ ] **App Name**: OrbitWall (max 50 characters)
- [ ] **Short Description**: 80 characters max
  - Example: "Beautiful satellite imagery wallpapers from around the world"
- [ ] **Full Description**: 4000 characters max
  - Describe features, benefits, how to use
  - Include keywords for search
  - Mention dark mode, customization options, etc.

### 2. Graphics Assets

#### Required:
- [ ] **App Icon**: 512x512 PNG, no transparency, no rounded corners
- [ ] **Feature Graphic**: 1024x500 PNG
- [ ] **Phone Screenshots**: At least 2, up to 8
  - Recommended: 1080x1920 or 1440x2560
  - Show main features: gallery, editor, wallpaper preview

#### Optional but Recommended:
- [ ] **Tablet Screenshots**: 7" and 10" tablets
- [ ] **TV Screenshots**: If supporting Android TV
- [ ] **Wear OS Screenshots**: If supporting Wear OS

### 3. Content Rating

- [ ] Complete IARC (International Age Rating Coalition) questionnaire
- [ ] Answer questions about:
  - User-generated content
  - Location sharing
  - Social features
  - In-app purchases
  - Violence, sexual content, etc.

### 4. Privacy & Security

- [ ] **Privacy Policy URL** (required if app collects data)
  - Even if you don't collect data, it's recommended to have one
  - Can host on GitHub Pages, your website, etc.
- [ ] **Data Safety Section**:
  - Declare what data you collect (if any)
  - How data is used
  - Data sharing practices
  - Security practices

### 5. App Content

- [ ] **Category**: Personalization / Wallpapers
- [ ] **Tags**: Select relevant tags
- [ ] **Contact Details**:
  - Email address for support
  - Website (optional)
- [ ] **Default Language**: English (United States)

### 6. Pricing & Distribution

- [ ] **Pricing**: Free or Paid
- [ ] **Countries**: Select where to distribute
- [ ] **Device Categories**: Phones, Tablets (if supported)

### 7. Release Information

- [ ] **Release Name**: e.g., "1.0.0 - Initial Release"
- [ ] **Release Notes**: What's new in this version
  - Example: "Initial release of OrbitWall. Browse beautiful satellite imagery wallpapers from around the world."

## Build & Upload Steps

### Step 1: Create Keystore
```bash
cd android-app
# Windows:
create-keystore.bat
# Linux/Mac:
./create-keystore.sh
```

### Step 2: Configure Keystore
1. Copy `keystore.properties.template` to `keystore.properties`
2. Edit `keystore.properties` with your actual passwords
3. **DO NOT commit this file to git!**

### Step 3: Build Release AAB
```bash
# Windows:
build-release.bat
# Linux/Mac:
./build-release.sh
```

The AAB will be at: `app/build/outputs/bundle/release/app-release.aab`

### Step 4: Test Release Build
- [ ] Install release APK on test device
- [ ] Test all features:
  - [ ] Browse gallery
  - [ ] View editor
  - [ ] Set wallpaper (home/lock screen)
  - [ ] Save to gallery
  - [ ] Dark mode toggle
  - [ ] State persistence (minimize/restore)
- [ ] Verify no crashes
- [ ] Check performance

### Step 5: Upload to Play Console

1. Go to [Google Play Console](https://play.google.com/console)
2. Create new app or select existing
3. Complete "App content" section:
   - [ ] Content rating
   - [ ] Target audience
   - [ ] News apps (if applicable)
   - [ ] COVID-19 contact tracing (if applicable)
4. Go to "Production" → "Create new release"
5. Upload `app-release.aab`
6. Fill in release notes
7. Review and publish

## Post-Upload

- [ ] Monitor for crashes in Play Console
- [ ] Respond to user reviews
- [ ] Monitor analytics
- [ ] Plan next update

## Common Issues & Solutions

### Issue: "Upload failed - AAB is not signed"
**Solution**: Make sure keystore.properties is configured and keystore file exists

### Issue: "Version code already used"
**Solution**: Increment versionCode in build.gradle.kts

### Issue: "App rejected - Missing privacy policy"
**Solution**: Add privacy policy URL in Play Console

### Issue: "App rejected - Content rating incomplete"
**Solution**: Complete IARC questionnaire in Play Console

## Version Management

For each new release:
1. Update `versionCode` (increment by 1)
2. Update `versionName` (e.g., "1.0.0" → "1.0.1")
3. Build new AAB
4. Upload to Play Console
5. Update release notes

## Security Reminders

- ⚠️ **NEVER** commit keystore files to git
- ⚠️ **NEVER** share keystore passwords
- ⚠️ **ALWAYS** backup keystore in secure location
- ⚠️ **NEVER** lose your keystore - you can't update app without it!

