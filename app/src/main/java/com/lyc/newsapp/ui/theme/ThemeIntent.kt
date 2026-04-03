package com.lyc.newsapp.ui.theme

import com.lyc.newsapp.data.preferences.ThemeMode

sealed class ThemeIntent {
    data class SetMode(val mode: ThemeMode) : ThemeIntent()
    data class SetDarkEnabled(val enabled: Boolean) : ThemeIntent()
}
