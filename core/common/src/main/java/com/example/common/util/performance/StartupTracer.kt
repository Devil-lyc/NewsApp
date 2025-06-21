package com.example.common.util.performance

import android.os.SystemClock
import timber.log.Timber

/**
 * 应用启动性能跟踪工具
 * 用于测量应用启动过程中的关键路径耗时
 */
object StartupTracer {
    private const val TAG = "StartupTracer"
    
    // 是否启用详细日志
    private var verboseLogging = true
    
    // 存储各个阶段的开始时间
    private val stageStartTimes = mutableMapOf<String, Long>()
    
    // 存储各个阶段的耗时
    private val stageDurations = mutableMapOf<String, Long>()
    
    // 应用冷启动开始时间(Application.onCreate)
    private var appStartTime = 0L
    
    // 应用启动阶段名称常量
    object Stages {
        const val APP_STARTUP = "app_startup"               // 应用启动总时间
        const val SPLASH_SCREEN = "splash_screen"           // 闪屏页面
        const val MAIN_ACTIVITY_INIT = "main_activity_init" // MainActivity初始化
        const val HOME_SCREEN_INIT = "home_screen_init"     // HomeScreen初始化
        const val FIRST_NEWS_LOAD = "first_news_load"       // 首次新闻加载
        const val FIRST_RENDER = "first_render"             // 首次渲染完成
        const val VIEWMODEL_INIT = "viewmodel_init"         // ViewModel初始化
        const val NETWORK_REQUEST = "network_request"       // 网络请求
        const val DATA_PROCESSING = "data_processing"       // 数据处理
    }
    
    /**
     * 初始化跟踪器，记录应用启动时间
     * 应在Application.onCreate中调用
     */
    fun init() {
        appStartTime = SystemClock.elapsedRealtime()
        startStage(Stages.APP_STARTUP)
        log("StartupTracer initialized")
    }
    
    /**
     * 开始计时某个阶段
     * @param stageName 阶段名称
     */
    fun startStage(stageName: String) {
        stageStartTimes[stageName] = SystemClock.elapsedRealtime()
        log("Stage started: $stageName")
    }
    
    /**
     * 结束某个阶段的计时并记录耗时
     * @param stageName 阶段名称
     * @return 该阶段的耗时(毫秒)
     */
    fun endStage(stageName: String): Long {
        val startTime = stageStartTimes[stageName] ?: run {
            Timber.tag(TAG).w("Trying to end stage $stageName that was never started")
            return 0
        }
        
        val duration = SystemClock.elapsedRealtime() - startTime
        stageDurations[stageName] = duration
        
        log("Stage completed: $stageName, duration: ${formatDuration(duration)}")
        return duration
    }
    
    /**
     * 立即记录某个事件的发生，用于记录瞬时事件而非阶段
     * @param eventName 事件名称
     */
    fun markEvent(eventName: String) {
        val timestamp = SystemClock.elapsedRealtime()
        val elapsedSinceStart = timestamp - appStartTime
        log("Event: $eventName, time since start: ${formatDuration(elapsedSinceStart)}")
    }
    
    /**
     * 获取某个阶段的耗时
     * @param stageName 阶段名称
     * @return 该阶段的耗时(毫秒)，如果阶段未结束返回-1
     */
    fun getStageDuration(stageName: String): Long {
        return stageDurations[stageName] ?: -1
    }
    
    /**
     * 获取从应用启动到现在的总耗时
     * @return 总耗时(毫秒)
     */
    fun getTotalDuration(): Long {
        return SystemClock.elapsedRealtime() - appStartTime
    }
    
    /**
     * 生成完整的启动性能报告
     * @return 格式化的性能报告字符串
     */
    fun generateReport(): String {
        val reportBuilder = StringBuilder()
        reportBuilder.appendLine("=========== 启动性能报告 ===========")
        reportBuilder.appendLine("总启动时间: ${formatDuration(getTotalDuration())}")
        reportBuilder.appendLine()
        reportBuilder.appendLine("各阶段耗时:")
        
        // 按耗时降序排列各阶段
        val sortedStages = stageDurations.entries.sortedByDescending { it.value }
        
        for ((stage, duration) in sortedStages) {
            val percentage = (duration * 100.0 / getTotalDuration()).toInt()
            reportBuilder.appendLine("- $stage: ${formatDuration(duration)} ($percentage%)")
        }
        
        reportBuilder.appendLine("===================================")
        return reportBuilder.toString()
    }
    
    /**
     * 打印完整性能报告到日志
     */
    fun printReport() {
        val report = generateReport()
        Timber.tag(TAG).i(report)
    }
    
    /**
     * 重置所有计时器
     */
    fun reset() {
        stageStartTimes.clear()
        stageDurations.clear()
        appStartTime = SystemClock.elapsedRealtime()
        log("StartupTracer reset")
    }
    
    /**
     * 设置是否启用详细日志
     */
    fun setVerboseLogging(enabled: Boolean) {
        verboseLogging = enabled
    }
    
    // 格式化持续时间
    private fun formatDuration(millis: Long): String {
        return when {
            millis < 1000 -> "${millis}ms"
            else -> String.format("%.2fs", millis / 1000.0)
        }
    }
    
    // 记录日志
    private fun log(message: String) {
        if (verboseLogging) {
            Timber.tag(TAG).d(message)
        }
    }
} 