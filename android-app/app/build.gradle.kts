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

    // Load keystore properties for signing
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val keystoreProperties = java.util.Properties()
    if (keystorePropertiesFile.exists()) {
        keystoreProperties.load(java.io.FileInputStream(keystorePropertiesFile))
    }

    signingConfigs {
        create("release") {
            if (keystorePropertiesFile.exists()) {
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Apply signing config if keystore exists
            if (keystorePropertiesFile.exists()) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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

// Task to copy APK to root apk directory for easy download
val apkOutputDir = rootProject.projectDir.parentFile.resolve("apk")

tasks.register("copyApkToRoot", Copy::class) {
    description = "Copies the built APK to the root apk directory"
    group = "build"
    
    from("build/outputs/apk/debug/")
    include("app-debug.apk")
    into(apkOutputDir)
    
    // Rename to a consistent name
    rename("app-debug.apk", "OrbitWall.apk")
    
    doLast {
        println("APK copied to ${apkOutputDir}/OrbitWall.apk")
    }
    
    // Only run if APK exists
    onlyIf {
        file("build/outputs/apk/debug/app-debug.apk").exists()
    }
}

// Make copyApkToRoot run after assembleDebug
// Use afterEvaluate to ensure Android plugin tasks are created first
afterEvaluate {
    tasks.findByName("assembleDebug")?.apply {
        finalizedBy("copyApkToRoot")
    }
}

// Also for release builds
tasks.register("copyReleaseApkToRoot", Copy::class) {
    description = "Copies the release APK to the root apk directory"
    group = "build"
    
    from("build/outputs/apk/release/")
    include("app-release.apk")
    into(apkOutputDir)
    
    rename("app-release.apk", "OrbitWall.apk")
    
    doLast {
        println("Release APK copied to ${apkOutputDir}/OrbitWall.apk")
    }
    
    onlyIf {
        file("build/outputs/apk/release/app-release.apk").exists()
    }
}

// Task to copy AAB to root apk directory
tasks.register("copyAabToRoot", Copy::class) {
    description = "Copies the release AAB to the root apk directory"
    group = "build"
    
    from("build/outputs/bundle/release/")
    include("app-release.aab")
    into(apkOutputDir)
    
    rename("app-release.aab", "OrbitWall-release.aab")
    
    doLast {
        println("AAB copied to ${apkOutputDir}/OrbitWall-release.aab")
    }
    
    onlyIf {
        file("build/outputs/bundle/release/app-release.aab").exists()
    }
}

// Configure release tasks in afterEvaluate
afterEvaluate {
    tasks.findByName("assembleRelease")?.apply {
        finalizedBy("copyReleaseApkToRoot")
    }
    tasks.findByName("bundleRelease")?.apply {
        finalizedBy("copyAabToRoot")
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
    
    // Coroutines (explicit dependency for better control)
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
