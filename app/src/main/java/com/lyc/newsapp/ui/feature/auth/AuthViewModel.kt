package com.lyc.newsapp.ui.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.core.result.Resource
import com.lyc.newsapp.data.model.User
import com.lyc.newsapp.data.repository.AuthRepository
import com.lyc.newsapp.ui.mvi.MviHost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val isRegisterSuccessful: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(), MviHost<AuthState, AuthIntent> {

    private val _uiState = MutableStateFlow(
        AuthState(
            isLoggedIn = authRepository.isLoggedIn(),
            user = authRepository.getCurrentUser()
        )
    )
    override val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    /** 与 [uiState] 相同，便于 Composable 语义化收集 */
    val authState: StateFlow<AuthState> = uiState

    override fun dispatch(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.Login -> login(intent.email, intent.password)
            is AuthIntent.Register -> register(intent.username, intent.email, intent.password)
            AuthIntent.Logout -> logout()
            AuthIntent.ClearError -> clearError()
            AuthIntent.ResetLoginSuccess -> resetLoginSuccess()
            AuthIntent.ResetRegisterSuccess -> resetRegisterSuccess()
            AuthIntent.SyncSession -> syncSession()
        }
    }

    private fun syncSession() {
        val isLoggedIn = authRepository.isLoggedIn()
        val currentUser = authRepository.getCurrentUser()
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            user = currentUser
        )
    }

    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isLoginSuccessful = false
            )

            when (val result = authRepository.login(email, password)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        isLoggedIn = true,
                        isLoginSuccessful = true
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isLoginSuccessful = false
                    )
                }
                else -> Unit
            }
        }
    }

    private fun register(username: String, email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isRegisterSuccessful = false
            )

            when (val result = authRepository.register(username, email, password)) {
                is Resource.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        user = result.data,
                        isLoggedIn = true,
                        isRegisterSuccessful = true
                    )
                }
                is Resource.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message,
                        isRegisterSuccessful = false
                    )
                }
                else -> Unit
            }
        }
    }

    private fun logout() {
        _uiState.value = AuthState(isLoggedIn = false)
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (_: Exception) {
                // 忽略登出异常，UI 已切为未登录
            }
        }
    }

    private fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun resetLoginSuccess() {
        _uiState.value = _uiState.value.copy(isLoginSuccessful = false)
    }

    private fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isRegisterSuccessful = false)
    }
}
