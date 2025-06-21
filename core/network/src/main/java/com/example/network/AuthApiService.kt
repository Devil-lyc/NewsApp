package com.example.network

import com.example.model.AuthResponse
import com.example.model.LoginRequest
import com.example.model.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 认证API服务接口
 * 基于 https://kkzynytfzajt.sealoshzh.site/ API
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