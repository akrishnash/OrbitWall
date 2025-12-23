# Changelog

All notable changes to the OrbitWall project will be documented in this file.

## [2025-12-24] - Major UI Upgrade & Bug Fixes

### 🎨 Major Features Added

#### 1. **Splash Screen with Animation**
- Added animated splash screen with "OrbitWall" branding
- Features pulsing glow effect and animated star field
- Smooth fade-in animation and automatic navigation to home screen
- Located in `app/src/main/java/com/orbitwall/ui/splash/SplashScreen.kt`

#### 2. **Home Screen Redesign**
- Transformed from fixed 2x2 grid to scrollable `LazyVerticalGrid`
- Displays all available regions (110+ locations worldwide)
- Responsive grid layout (2-4 columns based on screen width)
- Shows "Random" header with total wallpaper count
- Proper status bar padding integration

#### 3. **Bottom Navigation Bar**
- Added bottom navigation with three main tabs: Home, Category, Downloads
- Clean Material 3 design
- Visible on main screens (home, category, downloads)
- Proper navigation state management
- Located in `app/src/main/java/com/orbitwall/ui/navigation/BottomNavigation.kt`

#### 4. **Category Screen**
- New dedicated category browsing screen
- Clickable category cards that navigate to filtered gallery views
- Shows all available wallpaper categories (Nature, Cities, Mountains, etc.)
- Located in `app/src/main/java/com/orbitwall/ui/category/CategoryScreen.kt`

#### 5. **Downloads Section**
- Complete downloads/offline wallpaper management
- Scrollable grid view (2 columns) of saved wallpapers
- Displays wallpapers saved to `Pictures/OrbitWall` directory
- Shows wallpaper count in header
- Clickable cards that open saved wallpapers in editor
- Empty state with helpful message
- Located in `app/src/main/java/com/orbitwall/ui/downloads/DownloadsScreen.kt`

#### 6. **Saved Wallpaper Editor**
- New screen to view and set saved wallpapers offline
- Full-screen image preview with pinch-to-zoom support
- Set wallpaper button with options for Home, Lock, or Both screens
- Works completely offline with saved images
- Located in `app/src/main/java/com/orbitwall/ui/downloads/SavedWallpaperEditorScreen.kt`

### 🔧 Bug Fixes & Improvements

#### 1. **Wallpaper Scaling Fix**
- Fixed wallpaper stretching issue when setting as wallpaper
- Changed from `maxOf(scaleX, scaleY)` to `minOf(scaleX, scaleY)` in `WallpaperGenerator.kt`
- Now maintains aspect ratio properly, preventing distortion
- Letterboxing/pillarboxing if aspect ratios differ (preferable to distortion)

#### 2. **URI Navigation Fix**
- Fixed crash when opening downloaded images
- Added URL encoding/decoding for URI strings in navigation routes
- Handles special characters (like `:` and `/`) in URIs properly
- Better error handling for invalid URIs

#### 3. **Permission Handling**
- Implemented runtime permission requests for storage access
- Created `PermissionHandler.kt` utility for Android version-specific permissions
- Supports both Android 12- (READ_EXTERNAL_STORAGE) and Android 13+ (READ_MEDIA_IMAGES)
- Graceful permission handling throughout the app

#### 4. **Image Loading Improvements**
- Better error handling for bitmap loading
- Added SecurityException handling for file access
- Improved error messages for users
- Added logging for debugging image loading issues

#### 5. **Pinch Zoom Crash Fix**
- Fixed crash during pinch zoom in editor
- Refined gesture detection logic
- Prevented conflicts between `transformable` and `detectHorizontalDragGestures`
- Smooth zoom and pan interactions

#### 6. **Save Location Fix**
- Changed save location from `Downloads/OrbitWall` to `Pictures/OrbitWall`
- More reliable storage location for images
- Better organization of saved wallpapers
- Updated `DownloadsScreen.kt` to query correct directory

### 📦 New Dependencies & Utilities

#### 1. **Wallpaper Cache**
- Added `WallpaperCache.kt` for caching generated wallpapers
- Improves loading speed when revisiting edited wallpapers
- Reduces redundant wallpaper generation

#### 2. **Permission Handler**
- New utility class `PermissionHandler.kt`
- Simplifies permission requests across the app
- Handles Android version differences automatically

### 🗺️ Data Improvements

#### 1. **Expanded Region Database**
- Increased from 30 to 110+ diverse locations worldwide
- Fixed duplicate or overlapping region coordinates
- Ensures unique images for thumbnails and wallpapers
- Better geographic distribution

#### 2. **Thumbnail Optimization**
- Reduced thumbnail zoom level from 7 to 5 in `ThumbnailGenerator.kt`
- Faster loading times for gallery previews
- Better balance between quality and performance

### 🎨 UI/UX Improvements

#### 1. **Editor Screen Simplification**
- Removed "Customize" button and panel
- Simplified to "Save" and "Set Wallpaper" icon buttons only
- Cleaner, more focused interface
- Better floating action button placement

#### 2. **Gallery Screen Enhancements**
- Added dynamic category name in header
- Supports initial category filtering via navigation
- Better loading states and error handling

#### 3. **Theme Improvements**
- Consistent gradient backgrounds throughout app
- Better color scheme consistency
- Improved spacing and padding

### 📝 Code Quality

#### 1. **Better Error Handling**
- Comprehensive try-catch blocks for image operations
- User-friendly error messages
- Logging for debugging

#### 2. **Code Organization**
- Separated concerns into proper packages
- Better file structure (splash, navigation, downloads, category, home)
- Improved maintainability

### 🔒 Security & Permissions

- Added `READ_EXTERNAL_STORAGE` permission for Android 12 and below
- Added `READ_MEDIA_IMAGES` permission for Android 13+
- Added `WRITE_EXTERNAL_STORAGE` permission (for older Android versions)
- Proper runtime permission requests

### 📱 Android Manifest Updates

- Added all necessary storage permissions
- Proper permission declarations for all Android versions

---

## Future Improvements (Planned)

- [ ] Search functionality for wallpapers
- [ ] Favorite/bookmark system
- [ ] Share wallpaper functionality
- [ ] Batch download option
- [ ] Custom zoom level presets
- [ ] Advanced filtering options
- [ ] Dark mode toggle
- [ ] Wallpaper scheduling/auto-change


