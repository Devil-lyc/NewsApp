package com.lyc.newsapp

import android.app.Application
import com.tencent.mmkv.MMKV
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
//            Timber.plant(Timber.DebugTree())
//        }
        
        // 初始化MMKV
        val rootDir = MMKV.initialize(this)
        Timber.d("MMKV已初始化，存储路径: $rootDir")
    }
} 