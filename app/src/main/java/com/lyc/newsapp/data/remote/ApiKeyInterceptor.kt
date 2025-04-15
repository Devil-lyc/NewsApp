package com.lyc.newsapp.data.remote

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 拦截器：为所有请求自动添加 API 密钥
 */
class ApiKeyInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 检查 URL 是否已包含 apikey 参数
        val originalUrl = originalRequest.url
        val containsApiKey = originalUrl.queryParameterNames.contains("apikey")
        
        // 如果已经有 apikey 参数且值不为 null，则使用原始请求
        if (containsApiKey && originalUrl.queryParameter("apikey") != null) {
            return chain.proceed(originalRequest)
        }
        
        // 否则添加或替换 apikey 参数
        val newUrl = originalUrl.newBuilder()
            .removeAllQueryParameters("apikey")
            .addQueryParameter("apikey", apiKey)
            .build()
            
        // 创建带有新 URL 的请求
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()
            
        return chain.proceed(newRequest)
    }
} 