# OrbitWall Working Build Configuration Reference

This file contains the complete working build configuration from OrbitWall that you can use to fix your GrocTrack project.

---

## Build Commands

### Windows (PowerShell/CMD):
```bash
.\gradlew.bat assembleDebug
```

### Linux/Mac:
```bash
./gradlew assembleDebug
```

### For install:
```bash
.\gradlew.bat installDebug
```

---

## 1. Root `build.gradle.kts`

```kotlin
plugins {
    id("com.android.application") version "8.7.3" apply false
    id("org.jetbrains.kotlin.android") version "1.9.24" apply false
}
```

---

## 2. App `build.gradle.kts` (Key Settings)

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.orbitwall"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.orbitwall"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")

    // Core Android libraries
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("com.google.android.material:material:1.12.0")
    
    // Lifecycle
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.3")
    
    // Activity Compose
    implementation("androidx.activity:activity-compose:1.9.3")
    
    // Compose BOM - manages all Compose library versions
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.2")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.7.0")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    // Debug tools
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Testing
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("junit:junit:4.13.2")
}
```

---

## 3. `gradle.properties`

```properties
# Java Version Requirements:
# - Android Gradle Plugin 8.7.3 requires Java 11+ to run Gradle
# - Your app compiles with Java 17 (configured in app/build.gradle.kts)
# - Make sure JAVA_HOME points to Java 11+ installation
# - Verify with: java -version (should show Java 11 or higher)

org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.nonTransitiveRClass=true
android.enableJetifier=true
kotlin.code.style=official
```

---

## 4. `settings.gradle.kts`

```kotlin
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "OrbitWallAndroid"
include(":app")
```

---

## 5. `gradle/wrapper/gradle-wrapper.properties`

```properties
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-8.9-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
```

---

## Version Summary

| Component | Version |
|-----------|---------|
| Gradle | 8.9 |
| Android Gradle Plugin | 8.7.3 |
| Kotlin | 1.9.24 |
| Compile SDK | 35 |
| Min SDK | 29 |
| Target SDK | 35 |
| Java/JVM Target | 17 |
| Compose Compiler | 1.5.14 |

---

## Java Version Requirements

- **Java 17** for compilation (configured in `compileOptions` and `kotlinOptions`)
- **Java 11+** required to run Gradle itself (AGP 8.7.3 requirement)

Verify with:
```bash
java -version
```

---

## Quick Setup Checklist for GrocTrack

1. ✅ Update `gradle-wrapper.properties` to use Gradle 8.9
2. ✅ Set Android Gradle Plugin to 8.7.3 in root `build.gradle.kts`
3. ✅ Set Java compatibility to 17 in app `build.gradle.kts`
4. ✅ Ensure `gradle.properties` includes the AndroidX settings
5. ✅ Verify `settings.gradle.kts` has correct repositories (google, mavenCentral)
6. ✅ Update namespace and applicationId in app `build.gradle.kts` to match your GrocTrack package
7. ✅ Adjust minSdk/targetSdk if needed for your app requirements

---

## Common Build Issues & Solutions

### Issue: "Unsupported class file major version"
**Solution:** Ensure Java 17 is installed and JAVA_HOME is set correctly

### Issue: "Could not resolve all dependencies"
**Solution:** Check internet connection and verify repositories in `settings.gradle.kts`

### Issue: "Gradle sync failed"
**Solution:** 
- Clean build: `.\gradlew.bat clean`
- Invalidate caches in Android Studio
- Delete `.gradle` folder and rebuild

### Issue: "AndroidX migration required"
**Solution:** Ensure `android.useAndroidX=true` and `android.enableJetifier=true` in `gradle.properties`

---

## Notes

- This configuration uses Kotlin DSL (`.gradle.kts` files)
- If your project uses Groovy (`.gradle` files), convert the syntax accordingly
- The Compose BOM version (2024.09.02) manages all Compose library versions automatically
- Adjust dependencies based on your GrocTrack app's actual needs

