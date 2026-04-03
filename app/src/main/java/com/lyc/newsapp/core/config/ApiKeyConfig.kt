package com.lyc.newsapp.core.config

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
        }
        properties
    }

    fun getNewsApiKey(): String {
        return properties.getProperty(NEWS_API_KEY, FALLBACK_API_KEY)
    }
}
