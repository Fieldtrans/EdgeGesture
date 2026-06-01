package com.example.myedgegesture.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * EdgeGesture 应用主题
 */

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF006D3F),
    onPrimary = Color.White,
    secondary = Color(0xFF006A6A),
    tertiary = Color(0xFF6750A4),
    background = Color(0xFFF7FAF7),
    surface = Color(0xFFFEFCF8),
    surfaceVariant = Color(0xFFE7EFE8),
    outline = Color(0xFFB8C8BD)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64D692),
    secondary = Color(0xFF4DD5D3),
    tertiary = Color(0xFFD0BCFF)
)

@Composable
fun EdgeGestureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
