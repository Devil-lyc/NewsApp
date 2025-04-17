package com.lyc.newsapp.data.local

import com.lyc.newsapp.data.model.User
import com.tencent.mmkv.MMKV
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 会话管理器 - 使用MMKV管理用户会话信息
 */
@Singleton
class SessionManager @Inject constructor() {
    private val mmkv = MMKV.defaultMMKV()
    
    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
        private const val KEY_AVATAR = "avatar"
        private const val KEY_BIO = "bio"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }
    
    /**
     * 保存用户认证信息
     * 
     * @param user 用户信息
     * @param token 认证令牌
     */
    fun saveAuthUser(user: User, token: String) {
        mmkv.encode(KEY_AUTH_TOKEN, token)
        mmkv.encode(KEY_USER_ID, user.id)
        mmkv.encode(KEY_USERNAME, user.username)
        mmkv.encode(KEY_EMAIL, user.email)
        user.avatar?.let { mmkv.encode(KEY_AVATAR, it) }
        user.bio?.let { mmkv.encode(KEY_BIO, it) }
        mmkv.encode(KEY_IS_LOGGED_IN, true)
    }
    
    /**
     * 获取当前用户信息
     * 
     * @return 用户信息，如果未登录则返回null
     */
    fun getUser(): User? {
        val userId = mmkv.decodeString(KEY_USER_ID) ?: return null
        val username = mmkv.decodeString(KEY_USERNAME) ?: return null
        val email = mmkv.decodeString(KEY_EMAIL) ?: return null
        val avatar = mmkv.decodeString(KEY_AVATAR)
        val bio = mmkv.decodeString(KEY_BIO)
        
        return User(
            id = userId,
            username = username,
            email = email,
            avatar = avatar,
            bio = bio
        )
    }
    
    /**
     * 获取认证令牌
     * 
     * @return 认证令牌，如果未登录则返回null
     */
    fun getAuthToken(): String? {
        return mmkv.decodeString(KEY_AUTH_TOKEN)
    }
    
    /**
     * 检查用户是否已登录
     * 
     * @return 是否已登录
     */
    fun isLoggedIn(): Boolean {
        return mmkv.decodeBool(KEY_IS_LOGGED_IN, false)
    }
    
    /**
     * 清除用户会话信息
     */
    fun logout() {
        mmkv.clearAll()
    }
} 