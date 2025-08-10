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
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
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

    implementation(libs.androidx.hilt.navigation.compose.v110)
    implementation(libs.androidx.navigation.compose.v277)

    // DataStore
    implementation(libs.androidx.datastore.preferences.v100)

    // LiveData
    implementation(libs.androidx.runtime.livedata)

    // Icons
    implementation(libs.androidx.material.icons.extended.v178)

    // Google fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Firebase (usando BoM para manejar versiones)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation("com.google.firebase:firebase-firestore")

    // Debug
    implementation(libs.androidx.ui)
    debugImplementation(libs.androidx.ui.tooling)

    // Wear OS
    implementation(libs.play.services.wearable)

    // Chart Compose
    implementation(libs.compose.charts)

    // Retrofit + Gson
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    // Google Maps Utils
    implementation("com.google.maps.android:android-maps-utils:3.12.0")
    implementation("com.google.maps.android:maps-utils-ktx:5.2.0")

    // Jetpack Compose Maps UI
    implementation("com.google.maps.android:maps-compose:6.7.0")

    // Kotlin Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // Coil para imágenes
    implementation("io.coil-kt:coil-compose:2.5.0")
}

kapt {
    correctErrorTypes = true
}
