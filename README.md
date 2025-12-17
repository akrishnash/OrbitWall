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
- Email/password login gate with inline validation.
- Gallery of predefined orbit-worthy regions matching the web data set.
- Editor with tile stitching, gesture previews, brightness/blur/overlay controls, and 2K/4K exports.
- MediaStore integration to save renders directly into `Pictures/OrbitWall`.

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
