package com.example.baselineprofile

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 仅采集 **冷启动关键路径**：直接启动 Launcher Activity，并短暂等待首帧空闲。
 *
 * **为何不调用 [pressHome]**：`pressHome()` 依赖 UiAutomator 模拟系统 Home。部分厂商桌面（如 vivo）会拦截、
 * 动画过长或焦点异常，表现为卡住「不退出」当前阶段。而 `BaselineProfileRule.collect` 在每次迭代前会终止
 * 目标应用进程，通常已处于可 `startActivityAndWait` 的状态，**不必**先按 Home。
 *
 * ---
 * **其它生成 Baseline Profile 的途径**（不依赖本机 `pressHome` 行为）：
 *
 * 1. **Gradle 托管设备（GMD）**：在 `baselineProfile/build.gradle.kts` 里配置 API 33+ 的 `aosp` 虚拟设备，
 *    设置 `useConnectedDevices = false` 与 `managedDevices += "你的设备名"`，再跑
 *    `./gradlew :app:generateReleaseBaselineProfile`（仓库里已留示例注释）。
 * 2. **另一台 API 33+ 真机 / 官方镜像模拟器**：采集与 `pressHome` 无关，换设备往往即可。
 * 3. **手写 / 合并 `baseline-prof.txt`**：放在 `app/src/main/baselineProfiles/`（与 `saveInSrc = true` 一致），
 *    格式见官方文档；工作量大，一般只作补充或从其它构建拷贝。
 * 4. **依赖库自带的 baseline**：部分 AndroidX 库已带 profile；你仍可为应用增量生成自定义规则。
 *
 * 执行：
 * ```
 * ./gradlew :app:generateReleaseBaselineProfile
 * ```
 *
 * @see <a href="https://d.android.com/topic/performance/baselineprofiles">Baseline Profile 说明</a>
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {

    @get:Rule
    val rule = BaselineProfileRule()

    @Test
    fun generate() {
        val appId = InstrumentationRegistry.getArguments().getString("targetAppId")
            ?: "com.lyc.newsapp"

        rule.collect(
            packageName = appId,
            includeInStartupProfile = false
        ) {
            // 故意不调用 pressHome()，避免部分厂商桌面卡住；冷启动由规则在迭代间杀进程保证
            startActivityAndWait()
//            runCatching { device.waitForIdle() }
        }
    }
}
