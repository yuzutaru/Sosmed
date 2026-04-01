plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.yustar.sosmed"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.yustar.sosmed"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.yustar.sosmed.HiltTestRunner"
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

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    // Force concurrent-futures to 1.2.0 for all configurations to resolve version conflict
    implementation(libs.androidx.concurrent.futures)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.savedstate)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.navigation.testing)
    androidTestImplementation(libs.androidx.compose.ui)
    androidTestImplementation(libs.androidx.lifecycle.runtime.ktx)
    androidTestImplementation(libs.androidx.lifecycle.runtime.testing)
    androidTestImplementation(libs.androidx.lifecycle.runtime.compose)
    androidTestImplementation(libs.androidx.lifecycle.viewmodel.compose)
    androidTestImplementation(libs.androidx.lifecycle.viewmodel.savedstate)
    androidTestImplementation(libs.androidx.savedstate)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(project(":core"))
    implementation(project(":auth"))
    implementation(project(":dashboard"))


    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Mockk
    testImplementation(libs.io.mockk)
    androidTestImplementation(libs.io.mockk)
    androidTestImplementation(libs.io.mockk.android)

    // Splash Screen
    implementation(libs.androidx.core.splashscreen)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.video)

    // Supabase (Needed in tests to mock SupabaseClient)
    androidTestImplementation(libs.supabase.postgrest)
    androidTestImplementation(libs.supabase.auth)

    // Ktor Engine
    implementation(libs.ktor.client.android)
}
