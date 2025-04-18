package com.lyc.newsapp

import android.app.Application
import com.lyc.newsapp.data.local.SessionManager
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
        super.onCreate()
        
        // 初始化MMKV（必须在所有操作之前完成）
        val rootDir = MMKV.initialize(this)
        
        // 初始化日志系统
//        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
//        }
        Timber.d("MMKV已初始化，存储路径: $rootDir")
        
        // 在MMKV初始化之后初始化SessionManager
        sessionManager.initialize()
        Timber.d("SessionManager已初始化")
    }
} 