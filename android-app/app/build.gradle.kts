plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.orbitwall"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.orbitwall"
        minSdk = 29
        targetSdk = 34
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
        kotlinCompilerExtensionVersion = "1.5.11"
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

// Configure release task in afterEvaluate
afterEvaluate {
    tasks.findByName("assembleRelease")?.apply {
        finalizedBy("copyReleaseApkToRoot")
    }
}

dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.09.02")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.google.android.material:material:1.13.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(composeBom)
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.3.0")
    implementation("androidx.compose.material:material-icons-extended:1.7.0")
    implementation("androidx.navigation:navigation-compose:2.8.0")
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    testImplementation("junit:junit:4.13.2")
}
