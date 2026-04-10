package com.example.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 测试类：衡量应用冷启动耗时，用于对比 Baseline Profile 是否生效。
 *
 * - [CompilationMode.None]：无 Baseline Profile 优化时的基线。
 * - [CompilationMode.Partial]：启用 Baseline Profile（AOT/配置文件引导编译）后的情况。
 *
 * 可在 Android Studio 中作为 instrumentation 测试运行，或通过 Gradle 执行对应变体，例如：
 * ```
 * ./gradlew :baselineprofile:connectedBenchmarkReleaseAndroidTest
 * ```
 *
 * 请在 **真机** 上跑分；模拟器与宿主机共享资源，不能代表真实性能。
 *
 * @see [Macrobenchmark 文档](https://d.android.com/macrobenchmark#create-macrobenchmark)
 * @see [instrumentation 参数](https://d.android.com/topic/performance/benchmarking/macrobenchmark-instrumentation-args)
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmarks {

    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun startupCompilationNone() =
        benchmark(CompilationMode.None())

    @Test
    fun startupCompilationBaselineProfiles() =
        benchmark(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun benchmark(compilationMode: CompilationMode) {
        // 目标应用的 applicationId 由 Gradle / 插件通过 instrumentation 参数注入
        rule.measureRepeated(
            packageName = "com.lyc.newsapp",
            metrics = listOf(StartupTimingMetric()),
            compilationMode = compilationMode,
            startupMode = StartupMode.COLD,
            iterations = 10,
            setupBlock = {
                pressHome()
            },
            measureBlock = {
                startActivityAndWait()

                // TODO：若需统计「完全显示」时间，可在业务就绪时调用 Activity.reportFullyDrawn，
                // 或在 Compose 中使用 ReportDrawn / ReportDrawnWhen / ReportDrawnAfter（AndroidX Activity）。

                // 与界面交互可参考 UiAutomator：
                // https://d.android.com/training/testing/other-components/ui-automator
            }
        )
    }
}