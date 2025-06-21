package com.lyc.newsapp.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.database.ThemeMode
import com.example.database.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * 主题ViewModel，负责管理应用主题相关状态
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreference: ThemePreference
) : ViewModel() {
    
    // 预加载的主题设置缓存
    private var cachedThemeMode: ThemeMode? = null
    
    /**
     * 当前主题模式
     */
    val themeMode: StateFlow<ThemeMode> = themePreference.themeMode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            // 使用缓存的主题模式作为初始值，如果有的话
            cachedThemeMode ?: ThemeMode.SYSTEM
        )
    
    /**
     * 是否使用深色主题
     */
    val isDarkMode: StateFlow<Boolean> = themePreference.themeMode
        .map { it == ThemeMode.DARK }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            cachedThemeMode == ThemeMode.DARK
        )
    
    /**
     * 在后台线程预加载主题设置
     * 这样可以避免在UI线程中阻塞
     */
    suspend fun preloadThemeSettings() {
        withContext(Dispatchers.IO) {
            try {
                // 尝试读取主题设置
                cachedThemeMode = themePreference.themeMode.firstOrNull() ?: ThemeMode.SYSTEM
                Timber.d("主题设置预加载完成: $cachedThemeMode")
            } catch (e: Exception) {
                Timber.e(e, "预加载主题设置失败")
                cachedThemeMode = ThemeMode.SYSTEM
            }
        }
    }
    
    /**
     * 更新主题模式
     */
    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            themePreference.updateThemeMode(mode)
        }
    }
    
    /**
     * 切换深色模式状态
     */
    fun toggleDarkMode(isDark: Boolean) {
        viewModelScope.launch {
            val mode = if (isDark) ThemeMode.DARK else ThemeMode.LIGHT
            themePreference.updateThemeMode(mode)
        }
    }
}