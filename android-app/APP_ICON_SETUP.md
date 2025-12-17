# App Icon Setup Guide

## Overview
Your OrbitWall logo has been placed in `drawable/app_logo.png`. To set it as the app icon, you need to create properly sized icons for different screen densities.

## Icon Sizes Required

Android requires different icon sizes for different screen densities:

| Density | Folder | Size (px) |
|---------|--------|-----------|
| mdpi    | mipmap-mdpi | 48×48 |
| hdpi    | mipmap-hdpi | 72×72 |
| xhdpi   | mipmap-xhdpi | 96×96 |
| xxhdpi  | mipmap-xxhdpi | 144×144 |
| xxxhdpi | mipmap-xxxhdpi | 192×192 |

## Recommended: Adaptive Icon (Android 8.0+)

Modern Android uses adaptive icons that consist of:
- **Foreground**: The main logo/icon (must be 108×108dp centered in a 108×108dp canvas)
- **Background**: Optional background layer

### Option 1: Use Online Tool (Easiest)
1. Go to https://romannurik.github.io/AndroidAssetStudio/icons-launcher.html
2. Upload your logo (`app_logo.png`)
3. Download the generated icons
4. Extract and copy to the respective `mipmap-*` folders

### Option 2: Use Android Studio
1. Right-click `res` folder → New → Image Asset
2. Select "Launcher Icons (Adaptive and Legacy)"
3. Choose your logo as the source
4. Adjust as needed
5. Click Next → Finish

### Option 3: Manual Setup
1. Resize your logo to each required size using an image editor
2. Save as `ic_launcher.png` in each `mipmap-*` folder
3. For adaptive icons, also create:
   - `ic_launcher_foreground.png` (108×108dp, can be larger)
   - `ic_launcher_background.png` (optional, 108×108dp)

## Quick Setup Script

If you have ImageMagick installed, you can use this script:

```bash
# Create icons from logo
LOGO="app/src/main/res/drawable/app_logo.png"

convert "$LOGO" -resize 48x48 app/src/main/res/mipmap-mdpi/ic_launcher.png
convert "$LOGO" -resize 72x72 app/src/main/res/mipmap-hdpi/ic_launcher.png
convert "$LOGO" -resize 96x96 app/src/main/res/mipmap-xhdpi/ic_launcher.png
convert "$LOGO" -resize 144x144 app/src/main/res/mipmap-xxhdpi/ic_launcher.png
convert "$LOGO" -resize 192x192 app/src/main/res/mipmap-xxxhdpi/ic_launcher.png
```

## After Creating Icons

Once icons are in place, rebuild the app:
```bash
./quick-rebuild.sh
```

The app will automatically use the new icons!

## Current Status

✅ Logo file placed at: `app/src/main/res/drawable/app_logo.png`  
✅ Mipmap directories created  
⚠️ Icon files need to be generated (use one of the options above)
