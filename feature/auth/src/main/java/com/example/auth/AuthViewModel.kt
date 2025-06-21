package com.example.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.User
import com.example.data.repository.AuthRepository
import com.example.common.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 认证状态
 */
data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegisterSuccessful: Boolean = false
)

/**
 * 认证视图模型 - 处理登录和注册逻辑
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    
    private val _authState = MutableStateFlow(
        AuthState(
            isLoggedIn = authRepository.isLoggedIn(),
            user = authRepository.getCurrentUser()
        )
    )
    val authState: StateFlow<AuthState> = _authState
    
    /**
     * 检查用户登录状态 - 仅在需要强制刷新时使用
     */
    fun checkLoginStatus() {
        val isLoggedIn = authRepository.isLoggedIn()
        val currentUser = authRepository.getCurrentUser()
        
        _authState.value = _authState.value.copy(
            isLoggedIn = isLoggedIn,
            user = currentUser
        )
    }
    
    /**
     * 用户登录
     * 
     * @param email 邮箱
     * @param password 密码
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                isLoginSuccessful = false
            )
            
            when (val result = authRepository.login(email, password)) {
                is com.example.common.util.Resource.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        user = result.data,
                        isLoggedIn = true,
                        isLoginSuccessful = true
                    )
                }
                is com.example.common.util.Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccessful = false
                    )
                }
                else -> {}
            }
        }
    }
    
    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param email 邮箱
     * @param password 密码
     */
    fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.value = _authState.value.copy(
                isLoading = true,
                error = null,
                isRegisterSuccessful = false
            )
            
            when (val result = authRepository.register(username, email, password)) {
                is com.example.common.util.Resource.Success -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        user = result.data,
                        isLoggedIn = true,
                        isRegisterSuccessful = true
                    )
                }
                is com.example.common.util.Resource.Error -> {
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isRegisterSuccessful = false
                    )
                }
                else -> {}
            }
        }
    }
    
    /**
     * 用户登出
     */
    fun logout() {
        android.util.Log.d("AuthViewModel", "执行登出操作")
        // 更新状态为未登录
        _authState.value = AuthState(isLoggedIn = false)
        // 在协程中清除本地存储数据
        viewModelScope.launch {
            try {
                authRepository.logout()
                android.util.Log.d("AuthViewModel", "登出操作完成")
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "登出操作失败", e)
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
    
    /**
     * 重置登录状态
     */
    fun resetLoginSuccess() {
        _authState.value = _authState.value.copy(isLoginSuccessful = false)
    }
    
    /**
     * 重置注册状态
     */
    fun resetRegisterSuccess() {
        _authState.value = _authState.value.copy(isRegisterSuccessful = false)
    }
} 