package com.lyc.newsapp.ui.feature.auth

/**
 * 认证相关用户意图（MVI）。
 */
sealed class AuthIntent {
    data class Login(val email: String, val password: String) : AuthIntent()
    data class Register(val username: String, val email: String, val password: String) : AuthIntent()
    object Logout : AuthIntent()
    object ClearError : AuthIntent()
    object ResetLoginSuccess : AuthIntent()
    object ResetRegisterSuccess : AuthIntent()
    object SyncSession : AuthIntent()
}
