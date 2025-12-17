@echo off
REM Build and Run Android App from Cursor Terminal
REM This script builds the APK and installs it on a connected device

echo ========================================
echo OrbitWall Android - Build and Run
echo ========================================
echo.

cd /d "%~dp0"

REM Check if device is connected
echo Checking for connected Android device...
adb devices | findstr "device$" >nul
if errorlevel 1 (
    echo ERROR: No Android device or emulator found!
    echo Please connect a device or start an emulator, then run again.
    pause
    exit /b 1
)

echo Device found! Building and installing...
echo.

REM Clean previous build (optional - comment out if you want faster builds)
REM echo Cleaning previous build...
REM call gradlew.bat clean

REM Build and install
echo Building debug APK...
call gradlew.bat assembleDebug
if errorlevel 1 (
    echo.
    echo BUILD FAILED! Check errors above.
    pause
    exit /b 1
)

echo.
echo Installing APK on device...
call gradlew.bat installDebug
if errorlevel 1 (
    echo.
    echo INSTALL FAILED! Check errors above.
    pause
    exit /b 1
)

echo.
echo ========================================
echo SUCCESS! App installed and ready to launch
echo ========================================
echo.
echo To launch the app, run: adb shell am start -n com.orbitwall/.MainActivity
echo Or just open it manually on your device.
echo.
pause

