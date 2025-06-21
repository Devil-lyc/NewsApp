package com.example.model

/**
 * 用户注册请求
 *
 * @property username 用户名
 * @property email 邮箱
 * @property password 密码
 */
data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)

/**
 * 用户登录请求
 *
 * @property email 邮箱
 * @property password 密码
 */
data class LoginRequest(
    val email: String,
    val password: String
)

/**
 * 用户信息
 *
 * @property id 用户ID
 * @property username 用户名
 * @property email 邮箱
 * @property avatar 头像链接
 * @property bio 个人简介
 */
data class User(
    val id: String,
    val username: String,
    val email: String,
    val avatar: String? = null,
    val bio: String? = null
)

/**
 * 认证响应
 *
 * @property success 是否成功
 * @property message 消息
 * @property data 响应数据
 */
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val data: AuthData? = null
)

/**
 * 认证数据
 *
 * @property user 用户信息
 * @property token 令牌
 */
data class AuthData(
    val user: User,
    val token: String
)

/**
 * 通用错误响应
 *
 * @property success 是否成功
 * @property message 错误消息
 * @property errors 详细错误列表
 */
data class ErrorResponse(
    val success: Boolean,
    val message: String,
    val errors: List<ErrorDetail>? = null
)

/**
 * 错误详情
 *
 * @property msg 错误信息
 * @property param 错误参数
 * @property location 错误位置
 */
data class ErrorDetail(
    val msg: String,
    val param: String,
    val location: String
) 