@echo off
REM Build release AAB for Play Store upload

cd /d "%~dp0"

echo ========================================
echo OrbitWall - Building Release AAB
echo ========================================
echo.

REM Check if keystore.properties exists
if not exist "keystore.properties" (
    echo WARNING: keystore.properties not found!
    echo.
    echo You need to:
    echo 1. Create keystore: run create-keystore.bat
    echo 2. Copy keystore.properties.template to keystore.properties
    echo 3. Edit keystore.properties with your passwords
    echo.
    echo Building unsigned release (not suitable for Play Store)...
    echo.
    pause
)

echo Building release AAB...
echo.

call gradlew.bat clean bundleRelease

if errorlevel 1 (
    echo.
    echo BUILD FAILED!
    echo Check errors above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo BUILD SUCCESSFUL!
echo ========================================
echo.
echo Release AAB location:
echo app\build\outputs\bundle\release\app-release.aab
echo.
echo Upload this file to Google Play Console.
echo.
pause

