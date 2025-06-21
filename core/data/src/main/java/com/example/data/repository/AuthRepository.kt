package com.example.data.repository

import android.net.http.HttpException
import android.os.Build
import androidx.annotation.RequiresExtension
import com.example.common.util.Resource
import com.example.model.User
import com.example.network.AuthApiService
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 认证仓库 - 处理用户认证相关操作
 */
@Singleton
class AuthRepository @Inject constructor(
    private val authApiService: AuthApiService,
    private val sessionManager: com.example.database.SessionManager
) {
    /**
     * 注册新用户
     *
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     * @return 包含用户信息的资源结果
     */
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun register(username: String, email: String, password: String): Resource<User> {
        return try {
            val request = com.example.model.RegisterRequest(username, email, password)
            val response = authApiService.register(request)

            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.success && authResponse.data != null) {
                    // 保存用户会话信息
                    sessionManager.saveAuthUser(authResponse.data!!.user, authResponse.data!!.token)
                    Resource.Success(authResponse.data!!.user)
                } else {
                    Resource.Error(authResponse?.message ?: "注册失败")
                }
            } else {
                // 尝试解析错误响应
                try {
                    val errorBody = response.errorBody()?.string()
//                    Log.e("AuthRepository", "注册错误: $errorBody")
                    if (!errorBody.isNullOrEmpty()) {
                        // 可以使用Gson或Moshi解析完整的错误信息
                        // 这里简单返回有意义的错误信息
                        Resource.Error("请检查您的用户名或邮箱是否已被使用")
                    } else {
                        Resource.Error("注册失败: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Resource.Error("注册失败: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
//            Log.e("AuthRepository", "注册HttpException", e)
            Resource.Error("网络错误: ${e.localizedMessage ?: "未知网络错误"}")
        
        } catch (e: IOException) {
//            Log.e("AuthRepository", "注册IOException", e)
            Resource.Error("网络连接失败: ${e.localizedMessage ?: "请检查您的网络连接"}")
        } catch (e: Exception) {
//            Log.e("AuthRepository", "注册Exception", e)
            Resource.Error("注册失败: ${e.localizedMessage ?: "未知错误"}")
        }
    }
    
    /**
     * 用户登录
     *
     * @param email 邮箱
     * @param password 密码
     * @return 包含用户信息的资源结果
     */
    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    suspend fun login(email: String, password: String): Resource<com.example.model.User> {
        return try {
            val request = com.example.model.LoginRequest(email, password)
            val response = authApiService.login(request)
            
            if (response.isSuccessful) {
                val authResponse = response.body()
                if (authResponse != null && authResponse.success && authResponse.data != null) {
                    // 保存用户会话信息
                    sessionManager.saveAuthUser(authResponse.data!!.user, authResponse.data!!.token)
//                    Log.d("AuthRepository", "登录成功，保存令牌: ${authResponse.data.token}")
                    Resource.Success(authResponse.data!!.user)
                } else {
                    Resource.Error(authResponse?.message ?: "登录失败")
                }
            } else {
                // 尝试解析错误响应
                try {
                    val errorBody = response.errorBody()?.string()
//                    Log.e("AuthRepository", "登录错误: $errorBody")
                    if (!errorBody.isNullOrEmpty()) {
                        // 可以使用Gson或Moshi解析完整的错误信息
                        // 这里简单返回有意义的错误信息
                        Resource.Error("请检查您的邮箱和密码是否正确")
                    } else {
                        Resource.Error("登录失败: ${response.code()}")
                    }
                } catch (e: Exception) {
                    Resource.Error("登录失败: ${response.code()}")
                }
            }
        } catch (e: HttpException) {
//            Log.e("AuthRepository", "登录HttpException", e)
            Resource.Error("网络错误: ${e.localizedMessage ?: "未知网络错误"}")
        } catch (e: IOException) {
//            Log.e("AuthRepository", "登录IOException", e)
            Resource.Error("网络连接失败: ${e.localizedMessage ?: "请检查您的网络连接"}")
        } catch (e: Exception) {
//            Log.e("AuthRepository", "登录Exception", e)
            Resource.Error("登录失败: ${e.localizedMessage ?: "未知错误"}")
        }
    }
    
    /**
     * 检查用户是否已登录
     *
     * @return 是否已登录
     */
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }
    
    /**
     * 获取当前登录用户
     *
     * @return 用户信息，如果未登录则返回null
     */
    fun getCurrentUser(): com.example.model.User? {
        return sessionManager.getUser()
    }
    
    /**
     * 登出当前用户
     */
    suspend fun logout() {
        sessionManager.logout()
    }
} 