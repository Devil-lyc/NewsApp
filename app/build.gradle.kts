plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.lyc.newsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.lyc.newsapp"
        minSdk = 25
        targetSdk = 34
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
    // 强制使用 catalog 中的 profileinstaller，避免被其它依赖降到旧版导致 Baseline Profile 生成收不到 save 广播
    constraints {
        implementation(libs.androidx.profileinstaller) {
            because("Baseline Profile / macrobenchmark 在 Android 15+ 需 1.4.x+")
        }
    }

    // AndroidX Core
//    implementation("androidx.core:core:1.10.1")
    implementation(libs.androidx.core.ktx)
    
    // SplashScreen API
    implementation(libs.androidx.core.splashscreen)

    // Timber for logging
    implementation(libs.timber)

    // DataStore
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)

    // MMKV for key-value storage
    implementation(libs.mmkv)

    // WebView for Compose
    implementation(libs.accompanist.webview)

    // 下拉刷新
    implementation(libs.androidx.compose.material)

    //Navigation
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.androidx.navigation.compose)

    // Retrofit
    implementation(libs.retrofit.core)
    implementation(libs.converter.gson)
    implementation(libs.okhttp.logging)

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":baselineprofile"))
    ksp(libs.room.compiler)

    // Dagger Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Image loading
    implementation(libs.coil.kt.compose)


    //Extended Icons
    implementation(libs.androidx.compose.material.iconsExtended)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

baselineProfile {
    // 将生成的 profile 写入 app/src/main/baselineProfiles/，便于提交仓库并在 release 中享受 AOT 收益
    saveInSrc = true
}