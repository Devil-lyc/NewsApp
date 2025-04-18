package com.lyc.newsapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_settings")

/**
 * 主题模式枚举
 */
enum class ThemeMode(val value: Int) {
    SYSTEM(0),   // 跟随系统
    LIGHT(1),    // 浅色主题
    DARK(2);     // 深色主题
    
    companion object {
        fun fromValue(value: Int): ThemeMode {
            return values().find { it.value == value } ?: SYSTEM
        }
    }
}

/**
 * 主题偏好设置类，用于管理主题相关的偏好设置
 */
@Singleton
class ThemePreference @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val themeKey = intPreferencesKey("theme_mode")
    
    /**
     * 获取主题模式设置
     */
    val themeMode: Flow<ThemeMode> = context.dataStore.data
        .map { preferences ->
            ThemeMode.fromValue(preferences[themeKey] ?: ThemeMode.SYSTEM.value)
        }
    
    /**
     * 更新主题模式设置
     */
    suspend fun updateThemeMode(mode: ThemeMode) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = mode.value
        }
    }
} 