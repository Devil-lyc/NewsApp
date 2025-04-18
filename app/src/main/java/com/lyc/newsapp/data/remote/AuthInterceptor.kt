package com.lyc.newsapp.data.remote

import com.lyc.newsapp.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 授权拦截器：为所有请求添加Token认证信息
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val sessionManager: SessionManager
) : Interceptor {
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 获取存储的令牌
        val token = sessionManager.getAuthToken()
        
        // 如果没有令牌，直接发送原始请求
        if (token.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }
        
        // 创建带有授权头的新请求
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
            
        return chain.proceed(newRequest)
    }
} 