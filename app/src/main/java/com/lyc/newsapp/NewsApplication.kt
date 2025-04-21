package com.lyc.newsapp

import android.app.Application
import com.lyc.newsapp.data.local.SessionManager
import com.lyc.newsapp.util.performance.StartupTracer
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * 应用程序入口类
 * 
 * 使用Hilt进行依赖注入
 */
@HiltAndroidApp
class NewsApplication : Application() {
    
    @Inject
    lateinit var sessionManager: SessionManager
    
    override fun onCreate() {
        // 初始化启动性能跟踪器
        StartupTracer.init()
        
        super.onCreate()
        
        // 初始化MMKV（必须在所有操作之前完成）
        StartupTracer.startStage("mmkv_init")
        val rootDir = MMKV.initialize(this)
        StartupTracer.endStage("mmkv_init")
        
        // 初始化日志系统
        StartupTracer.startStage("timber_init")
//        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
//        }
        Timber.d("MMKV已初始化，存储路径: $rootDir")
        StartupTracer.endStage("timber_init")
        
        // 在MMKV初始化之后初始化SessionManager
        StartupTracer.startStage("session_manager_init")
        sessionManager.initialize()
        Timber.d("SessionManager已初始化")
        StartupTracer.endStage("session_manager_init")
        
        Timber.d("应用初始化完成")
    }
    
    override fun onTerminate() {
        super.onTerminate()
        // 打印启动性能报告
        StartupTracer.printReport()
    }
    
    companion object {
        // 是否启用性能跟踪
        var enablePerformanceTracking = true
            set(value) {
                field = value
                StartupTracer.setVerboseLogging(value)
            }
    }
}