@echo off
REM Start Android Emulator Helper Script

REM SDK location from local.properties
set SDK_DIR=%LOCALAPPDATA%\Android\Sdk
set EMULATOR=%SDK_DIR%\emulator\emulator.exe
set ADB=%SDK_DIR%\platform-tools\adb.exe

REM Set AVD home to E drive to save disk space
REM This tells the emulator to store AVD files on E drive instead of C drive
set ANDROID_AVD_HOME=E:\anurag\.android\avd
set ANDROID_SDK_HOME=E:\anurag\.android

REM Create directory if it doesn't exist
if not exist "%ANDROID_AVD_HOME%" mkdir "%ANDROID_AVD_HOME%"
if not exist "%ANDROID_SDK_HOME%" mkdir "%ANDROID_SDK_HOME%"

echo ========================================
echo Android Emulator Helper
echo ========================================
echo AVD Home: %ANDROID_AVD_HOME%
echo.

REM Check if SDK exists
if not exist "%EMULATOR%" (
    echo ERROR: Android SDK not found at: %SDK_DIR%
    echo Please install Android Studio and SDK first.
    pause
    exit /b 1
)

REM Add SDK tools to PATH for this session
set PATH=%SDK_DIR%\emulator;%SDK_DIR%\platform-tools;%SDK_DIR%\tools;%SDK_DIR%\tools\bin;%PATH%

REM List available AVDs
echo Available Android Virtual Devices (AVDs):
echo ----------------------------------------
"%EMULATOR%" -list-avds
if errorlevel 1 (
    echo No AVDs found. Create one in Android Studio: Tools ^> Device Manager ^> Create Device
)

echo.
echo.

REM Check if emulator is already running
"%ADB%" devices | findstr "emulator.*device$" >nul
if not errorlevel 1 (
    echo Emulator is already running!
    "%ADB%" devices
    pause
    exit /b 0
)

REM Get first AVD if one exists
for /f "delims=" %%i in ('"%EMULATOR%" -list-avds 2^>nul') do (
    set AVD_NAME=%%i
    goto :start_emulator
)

echo No AVDs found!
echo.
echo To create an AVD:
echo 1. Open Android Studio
echo 2. Go to Tools ^> Device Manager
echo 3. Click 'Create Device'
echo 4. Select a device (e.g., Pixel 7)
echo 5. Choose a system image (API 34 recommended)
echo 6. Finish the wizard
pause
exit /b 1

:start_emulator
if "%1"=="" (
    echo Starting emulator: %AVD_NAME%
    echo.
    start "" "%EMULATOR%" -avd %AVD_NAME%
    echo Emulator is starting in the background...
    echo Wait for it to boot completely (this may take 30-60 seconds)
    echo.
    echo Check status with: adb devices
) else (
    echo Starting emulator: %1
    echo.
    start "" "%EMULATOR%" -avd %1
    echo Emulator is starting in the background...
    echo Wait for it to boot completely (this may take 30-60 seconds)
    echo.
    echo Check status with: adb devices
)
