package com.lyc.newsapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.lyc.newsapp.data.preferences.ThemeMode

// 根据ui.html的颜色系统定义颜色
val Primary = Color(0xFF1A73E8)
val PrimaryLight = Color(0xFFD0E3FA)
val PrimaryDark = Color(0xFF0D47A1)
val Secondary = Color(0xFFF25D50)
val SecondaryLight = Color(0xFFFFEBE9)

// 中性色
val TextPrimary = Color(0xFF202124)
val TextSecondary = Color(0xFF5F6368)
val TextTertiary = Color(0xFF9AA0A6)
val Background = Color(0xFFFFFFFF)
val Surface = Color(0xFFF8F9FA)
val Border = Color(0xFFDADCE0)
val Divider = Color(0x14000000) // rgba(0, 0, 0, 0.08)

// 亮色主题配色方案
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = Secondary,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = Color.White,
    onSurfaceVariant = TextSecondary,
    outline = Border
)

// 暗色主题配色方案
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF5D1E18),
    onSecondaryContainer = SecondaryLight,
    background = Color(0xFF121212),
    onBackground = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFBBBBBB),
    outline = Color(0xFF444444)
)

/**
 * 应用主题
 *
 * @param darkTheme 是否使用深色主题
 * @param content 内容
 */
@Composable
fun NewsAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}