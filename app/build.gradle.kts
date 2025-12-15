plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)

    // Room
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.gestionusuarioshibrido"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gestionusuarioshibrido"
        minSdk = 24
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
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation(libs.androidx.compose.material.icons.extended)

    // Compose Navigation
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.squareup.retrofit)

    // Retrofit + Kotlin Serialization converter
    implementation(libs.jakewharton.retrofit.serialization)

    // OkHttp
    implementation(libs.squareup.okhttp)

    // Kotlin Serialization
    implementation(libs.jetbrains.kotlinx.serialization.json)

    // Coil 3
    implementation(libs.coil3.compose)
    implementation(libs.coil3.network.okhttp)


    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)

    // Compilador Room con KSP - Kotlin Symbol Processing
    // (tecnología de procesamiento de anotaciones)
    ksp(libs.room.compiler.ksp)


}