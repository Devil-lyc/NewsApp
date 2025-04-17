package com.lyc.newsapp.data.remote

import com.lyc.newsapp.data.model.AuthResponse
import com.lyc.newsapp.data.model.LoginRequest
import com.lyc.newsapp.data.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 认证API服务接口
 */
interface AuthApiService {
    
    /**
     * 用户注册
     *
     * @param request 注册请求体
     * @return 认证响应
     */
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>
    
    /**
     * 用户登录
     *
     * @param request 登录请求体
     * @return 认证响应
     */
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>
} 