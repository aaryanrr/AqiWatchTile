plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.aryan.aqiwatchtile"
    //noinspection GradleDependency
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aryan.aqiwatchtile"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation("com.google.android.gms:play-services-wearable:18.0.0")
    // 1. Wear OS Tile & ProtoLayout Libraries
    implementation("androidx.wear.tiles:tiles:1.5.0")
    implementation("androidx.wear.protolayout:protolayout:1.3.0")
    implementation("androidx.wear.protolayout:protolayout-material:1.3.0")
    // 2. Networking (Retrofit & Gson)
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")
    // 3. Coroutines (for background API calls)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-guava:1.10.2")
    // Crucial for Tile Futures
    implementation("com.google.guava:guava:33.5.0-android")
    // 4. Location Services
    implementation("com.google.android.gms:play-services-location:21.3.0")
    // 5. Coroutine adapter for Google Play Services (Tasks -> Coroutines)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
}