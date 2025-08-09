plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.undefined.farfaraway"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.undefined.farfaraway"
        minSdk = 31
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.play.services.location)
    kapt(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation (libs.androidx.hilt.navigation.compose.v110)
    implementation (libs.androidx.navigation.compose.v277)

    //DataStore
    implementation (libs.androidx.datastore.preferences.v100)

    // livedata
    implementation (libs.androidx.runtime.livedata)

    //Icons
    implementation (libs.androidx.material.icons.extended.v178)

    //Google fonts
    implementation (libs.androidx.ui.text.google.fonts)

    implementation(libs.androidx.work.runtime.ktx)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)

    //Debug
    implementation (libs.androidx.ui)
    debugImplementation (libs.androidx.ui.tooling)

    //Weareable
    implementation (libs.play.services.wearable)

    //Cart Compose
    implementation (libs.compose.charts)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    //Wear
    //implementation(libs.play.services.wearable.v1810)


    //implementation(libs.retrofit.v290)
    //implementation(libs.converter.gson.v290)

    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Core Utility Library
    implementation("com.google.maps.android:android-maps-utils:3.12.0")

    // Kotlin Extensions for Utility Library
    implementation("com.google.maps.android:maps-utils-ktx:5.2.0") // officially supported :contentReference[oaicite:1]{index=1}

    // Jetpack Compose map UI components
    implementation("com.google.maps.android:maps-compose:6.7.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

}

kapt {
    correctErrorTypes = true
}