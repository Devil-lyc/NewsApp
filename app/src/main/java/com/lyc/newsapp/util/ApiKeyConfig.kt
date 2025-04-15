package com.lyc.newsapp.util

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Properties
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 安全管理API密钥的工具类
 * 负责从assets中的配置文件加载API密钥
 */
@Singleton
class ApiKeyConfig @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private const val CONFIG_FILE = "api-keys.properties"
        private const val NEWS_API_KEY = "NEWS_API_KEY"
        
        // 如果配置文件加载失败，提供一个备用密钥（建议为空字符串，迫使开发者提供正确的密钥文件）
        private const val FALLBACK_API_KEY = "YOUR_API_KEY_HERE"
    }
    
    private val properties: Properties by lazy {
        val properties = Properties()
        try {
            val inputStream = context.assets.open(CONFIG_FILE)
            properties.load(inputStream)
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            // 在开发环境中可以选择抛出异常，强制开发者提供正确的配置文件
            // 在生产环境中可以使用默认值
            // throw RuntimeException("无法加载API密钥配置文件，请确保assets目录下存在$CONFIG_FILE文件")
        }
        properties
    }
    
    /**
     * 获取新闻API密钥
     */
    fun getNewsApiKey(): String {
        return properties.getProperty(NEWS_API_KEY, FALLBACK_API_KEY)
    }
} 