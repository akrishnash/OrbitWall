# Quick Start: Play Store Upload

## 🚀 Fast Track (5 Steps)

### 1. Create Keystore (One-time setup)
```bash
cd android-app
create-keystore.bat    # Windows
# or
./create-keystore.sh   # Linux/Mac
```
**Save the passwords you enter - you'll need them forever!**

### 2. Configure Keystore Properties
```bash
# Copy template
copy keystore.properties.template keystore.properties  # Windows
# or
cp keystore.properties.template keystore.properties     # Linux/Mac

# Edit keystore.properties and fill in your passwords
```

### 3. Build Release AAB
```bash
build-release.bat      # Windows
# or
./build-release.sh     # Linux/Mac
```

### 4. Find Your AAB
Location: `app/build/outputs/bundle/release/app-release.aab`

### 5. Upload to Play Console
1. Go to https://play.google.com/console
2. Create app → Upload AAB → Complete store listing → Publish

## 📋 What You Need Before Upload

- [ ] App name: "OrbitWall"
- [ ] Short description (80 chars)
- [ ] Full description (4000 chars)
- [ ] App icon (512x512 PNG)
- [ ] Feature graphic (1024x500 PNG)
- [ ] Screenshots (2-8 images)
- [ ] Privacy policy URL (recommended)

## ⚠️ Important Notes

- **Keystore is FOREVER**: If you lose it, you can't update your app
- **Backup keystore**: Store in secure, encrypted location
- **Never commit**: keystore.properties and .jks files to git
- **Version codes**: Must increment for each release (1, 2, 3...)

## 📚 Full Documentation

- Detailed setup: `PLAY_STORE_SETUP.md`
- Complete checklist: `PLAY_STORE_CHECKLIST.md`

## 🆘 Troubleshooting

**Build fails?**
- Check keystore.properties exists and has correct paths
- Verify keystore file exists at specified location
- Check passwords are correct

**Can't find keytool?**
- Make sure Java JDK is installed
- Add Java bin directory to PATH

**Upload rejected?**
- Check content rating is complete
- Verify privacy policy if required
- Ensure all required graphics are uploaded

