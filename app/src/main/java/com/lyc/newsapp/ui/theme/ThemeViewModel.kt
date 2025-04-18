package com.lyc.newsapp.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.data.preferences.ThemeMode
import com.lyc.newsapp.data.preferences.ThemePreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 主题ViewModel，负责管理应用主题相关状态
 */
@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreference: ThemePreference
) : ViewModel() {
    
    /**
     * 当前主题模式
     */
    val themeMode: StateFlow<ThemeMode> = themePreference.themeMode
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ThemeMode.SYSTEM
        )
    
    /**
     * 是否使用深色主题
     */
    val isDarkMode: StateFlow<Boolean> = themePreference.themeMode
        .map { it == ThemeMode.DARK }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            false
        )
    
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