<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://github.com/user-attachments/assets/0aa67016-6eaf-458a-adb2-6e31a0763ed6" />
</div>

# Run and deploy your AI Studio app

This contains everything you need to run your existing React experience and the new Android-native port.

View your app in AI Studio: https://ai.studio/apps/drive/1b9hiKfeb8LxY0-SmB8GouiuLkUNBYZ4D

## Run the React experience locally

**Prerequisites:** Node.js

1. Install dependencies: `npm install`
2. Set the `GEMINI_API_KEY` in [.env.local](.env.local) to your Gemini API key
3. Run the app: `npm run dev`

## Android login-enabled build (Jetpack Compose)

**Project root:** `android-app/`

### Highlights
- **Animated Splash Screen**: Beautiful splash screen with pulsing glow and star field animations
- **Home Screen**: Scrollable grid of 110+ stunning locations worldwide with responsive layout
- **Category Browsing**: Browse wallpapers by categories (Nature, Cities, Mountains, Oceans, etc.)
- **Downloads Section**: Save and manage wallpapers offline in `Pictures/OrbitWall`
- **Saved Wallpaper Editor**: View and set saved wallpapers offline with full-screen preview
- **Bottom Navigation**: Easy navigation between Home, Category, and Downloads
- **Editor Features**: Tile stitching, gesture previews (pinch-to-zoom, pan), brightness/blur controls
- **Wallpaper Generation**: High-quality 2K/4K wallpaper generation from satellite imagery
- **MediaStore Integration**: Save renders directly to device storage with proper permissions
- **Wallpaper Cache**: Intelligent caching system for faster loading of previously generated wallpapers
- **Aspect Ratio Preservation**: Proper scaling to prevent image distortion when setting wallpapers

### Getting started

**📖 Detailed Instructions:**
- **[Quick Start Guide](android-app/QUICK_START.md)** - 5-minute checklist to get running
- **[Complete Setup Guide](android-app/ANDROID_STUDIO_SETUP.md)** - Step-by-step with troubleshooting

**Quick steps:**
1. Open Android Studio Hedgehog+ and select *Open an Existing Project*.
2. Choose the `android-app` directory and let Gradle sync (requires JDK 17, Android Gradle Plugin 8.5+).
3. Create a device/emulator running API 29 or higher.
4. Press **Run** (▶) to deploy.

> **Note:** Blur post-processing uses `RenderEffect` when available (API 31+); older devices fall back to sharp imagery.
