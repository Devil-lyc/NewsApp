package com.lyc.newsapp.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lyc.newsapp.data.preferences.ThemeMode
import com.lyc.newsapp.data.preferences.ThemePreference
import com.lyc.newsapp.ui.mvi.MviHost
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

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themePreference: ThemePreference
) : ViewModel(), MviHost<ThemeUiState, ThemeIntent> {

    override val uiState: StateFlow<ThemeUiState> = themePreference.themeMode
        .map { ThemeUiState(it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            ThemeUiState(ThemeMode.SYSTEM)
        )

    override fun dispatch(intent: ThemeIntent) {
        when (intent) {
            is ThemeIntent.SetMode -> viewModelScope.launch {
                themePreference.updateThemeMode(intent.mode)
            }
            is ThemeIntent.SetDarkEnabled -> viewModelScope.launch {
                val mode = if (intent.enabled) ThemeMode.DARK else ThemeMode.LIGHT
                themePreference.updateThemeMode(mode)
            }
        }
    }

    suspend fun preloadThemeSettings() {
        withContext(Dispatchers.IO) {
            try {
                themePreference.themeMode.firstOrNull()
                Timber.d("主题设置预加载完成")
            } catch (e: Exception) {
                Timber.e(e, "预加载主题设置失败")
            }
        }
    }
}
