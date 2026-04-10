plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.baselineprofile)
}

android {
    namespace = "com.example.baselineprofile"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    defaultConfig {
        minSdk = 28
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["targetAppId"] = "com.lyc.newsapp"
        // 在可调试包/模拟器上生成 profile 时抑制部分检查（仍建议优先使用真机）
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] =
            "DEBUGGABLE,EMULATOR,LOW-BATTERY,UNLOCKED"
    }

    targetProjectPath = ":app"

    // 若实体机 pressHome/厂商桌面导致 Macrobenchmark 卡住，可改用 GMD（API 33+、aosp 镜像），并关闭下面 baselineProfile 的 useConnectedDevices。
    // import com.android.build.api.dsl.ManagedVirtualDevice
    // testOptions.managedDevices.devices {
    //     create<ManagedVirtualDevice>("pixel6Api34") {
    //         device = "Pixel 6"
    //         apiLevel = 34
    //         systemImageSource = "aosp"
    //     }
    // }
}

// Baseline Profile Gradle 插件配置：可使用 Gradle 托管设备或已连接的真机/模拟器
baselineProfile {
    useConnectedDevices = true
    // 使用 GMD 时改为 false，并取消上面 managedDevices 与下一行注释：
    // managedDevices += "pixel6Api34"
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}