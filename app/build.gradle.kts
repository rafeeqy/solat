import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("com.google.dagger.hilt.android")
    alias(libs.plugins.ksp)
}

android {
    namespace = "my.naqibrafiqi.solat"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "my.naqibrafiqi.solat"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.1"

        val buildTime = LocalDateTime.now()
            .atZone(ZoneId.of("Asia/Kuala_Lumpur"))
            .format(DateTimeFormatter.ofPattern("h:mm a, hh/MM/uuuu"))

        buildConfigField("String", "BUILD_TIME", "\"$buildTime\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        freeCompilerArgs += "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi"
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Appcompat
    implementation("androidx.appcompat:appcompat:1.7.1")

    // Splash screen
    implementation("androidx.core:core-splashscreen:1.2.0")

    // Compose Material Icons Extended
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // Google Fonts
    implementation("androidx.compose.ui:ui-text-google-fonts:1.11.3")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.10.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.9.6")

    // Kotlin JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    // DI with Hilt
    implementation("com.google.dagger:hilt-android:2.57.2")
    ksp("com.google.dagger:hilt-android-compiler:2.57.2")
    // Hilt for WorkManager
    implementation("androidx.hilt:hilt-work:1.3.0")
    ksp("androidx.hilt:hilt-compiler:1.3.0")
    // Hilt with Navigation
    implementation("androidx.hilt:hilt-navigation-compose:1.3.0")

    // Room Database
    implementation("androidx.room:room-runtime:2.8.4")
    ksp("androidx.room:room-compiler:2.8.3")
    implementation("androidx.room:room-ktx:2.8.4")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    // WorkManager
    implementation("androidx.work:work-runtime-ktx:2.11.0")

    // Retrofit
    // repo: https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    implementation("com.squareup.retrofit2:converter-kotlinx-serialization:3.0.0")

    // Google Accompanist
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // WorkManager test helpers
    androidTestImplementation("androidx.work:work-testing:2.11.0")

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}