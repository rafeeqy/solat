import java.time.ZonedDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

// Shown in Settings next to the version, e.g. "4:04 pm, 15/11/2026" (Malaysia time). Only release
// builds carry it: a value that changes on every build would make every debug build recompile.
val releaseBuildTime: String = ZonedDateTime.now(ZoneId.of("Asia/Kuala_Lumpur"))
    .format(DateTimeFormatter.ofPattern("h:mm a, dd/MM/uuuu", Locale.ENGLISH))

android {
    namespace = "my.naqibrafiqi.solat"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "my.naqibrafiqi.solat"
        minSdk = 29
        targetSdk = 36
        versionCode = 2
        versionName = "1.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField("String", "BUILD_TIME", "\"debug\"")
        }
        release {
            buildConfigField("String", "BUILD_TIME", "\"$releaseBuildTime\"")

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

    testOptions {
        unitTests {
            // Robolectric needs the merged resources to run Android code (Room, AlarmManager)
            // on the JVM.
            isIncludeAndroidResources = true
        }
    }

    ksp {
        arg("room.schemaLocation", "$projectDir/schemas")
    }
}

dependencies {
    // AndroidX
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.ui.text.google.fonts)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)

    // Dependency injection (Hilt, with WorkManager and Navigation)
    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // Storage: Room and DataStore
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.datastore.preferences)

    // Background work
    implementation(libs.androidx.work.runtime.ktx)

    // Networking: Retrofit (https://github.com/square/retrofit) with kotlinx.serialization
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.kotlinx.serialization)
    implementation(libs.kotlinx.serialization.json)

    // Permissions
    implementation(libs.accompanist.permissions)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.robolectric)
    testImplementation(libs.androidx.test.core)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.work.testing)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
