package com.lyc.newsapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * 应用程序入口类
 * 
 * 使用Hilt进行依赖注入
 */
@HiltAndroidApp
class NewsApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化日志系统
//        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            Timber.d("新闻App初始化")
//        }
    }
} 