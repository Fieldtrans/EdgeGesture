package com.example.edgegesture.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * EdgeGesture 应用主题
 */

private val LightColorScheme =
    lightColorScheme(
        primary = Color(0xFF006D3F),
        onPrimary = Color.White,
        primaryContainer = Color(0xFFB8F5D3),
        onPrimaryContainer = Color(0xFF002111),
        secondary = Color(0xFF006A6A),
        onSecondary = Color.White,
        secondaryContainer = Color(0xFF6FF7F6),
        onSecondaryContainer = Color(0xFF002020),
        tertiary = Color(0xFF6750A4),
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFEADDFF),
        onTertiaryContainer = Color(0xFF21005D),
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = Color(0xFFF7FAF7),
        onBackground = Color(0xFF191C1A),
        surface = Color(0xFFFCFDF8),
        onSurface = Color(0xFF191C1A),
        surfaceVariant = Color(0xFFDDE5DB),
        onSurfaceVariant = Color(0xFF414942),
        outline = Color(0xFF717971),
        outlineVariant = Color(0xFFC1C9BF),
        inverseSurface = Color(0xFF2E312E),
        inverseOnSurface = Color(0xFFF0F1EC),
        surfaceContainerLowest = Color(0xFFFFFFFF),
        surfaceContainerLow = Color(0xFFF6F7F2),
        surfaceContainer = Color(0xFFF0F1EC),
        surfaceContainerHigh = Color(0xFFEBECE7),
        surfaceContainerHighest = Color(0xFFE5E6E1),
    )

private val DarkColorScheme =
    darkColorScheme(
        primary = Color(0xFF64D692),
        onPrimary = Color(0xFF003920),
        primaryContainer = Color(0xFF00522F),
        onPrimaryContainer = Color(0xFF80F3AB),
        secondary = Color(0xFF4DD5D3),
        onSecondary = Color(0xFF003737),
        secondaryContainer = Color(0xFF004F4F),
        onSecondaryContainer = Color(0xFF6FF7F6),
        tertiary = Color(0xFFD0BCFF),
        onTertiary = Color(0xFF381E72),
        tertiaryContainer = Color(0xFF4F378B),
        onTertiaryContainer = Color(0xFFEADDFF),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = Color(0xFF191C1A),
        onBackground = Color(0xFFE1E3DE),
        surface = Color(0xFF111412),
        onSurface = Color(0xFFE1E3DE),
        surfaceVariant = Color(0xFF414942),
        onSurfaceVariant = Color(0xFFC1C9BF),
        outline = Color(0xFF8B938A),
        outlineVariant = Color(0xFF414942),
        inverseSurface = Color(0xFFE1E3DE),
        inverseOnSurface = Color(0xFF191C1A),
        surfaceContainerLowest = Color(0xFF0C0F0D),
        surfaceContainerLow = Color(0xFF191C1A),
        surfaceContainer = Color(0xFF1D201E),
        surfaceContainerHigh = Color(0xFF272B28),
        surfaceContainerHighest = Color(0xFF323633),
    )

private val AppTypography =
    Typography(
        displayLarge =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 57.sp,
                lineHeight = 64.sp,
                letterSpacing = (-0.25).sp,
            ),
        headlineLarge =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                lineHeight = 40.sp,
            ),
        headlineMedium =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                lineHeight = 36.sp,
            ),
        headlineSmall =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                lineHeight = 32.sp,
            ),
        titleLarge =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 22.sp,
                lineHeight = 28.sp,
            ),
        titleMedium =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.15.sp,
            ),
        titleSmall =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        bodyLarge =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.25.sp,
            ),
        bodySmall =
            TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.4.sp,
            ),
        labelLarge =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.1.sp,
            ),
        labelMedium =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                letterSpacing = 0.5.sp,
            ),
    )

private val AppShapes =
    Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(16.dp),
        large = RoundedCornerShape(24.dp),
        extraLarge = RoundedCornerShape(32.dp),
    )

enum class AppColorScheme {
    Dynamic,
    FixedLight,
    FixedDark,
}

fun selectAppColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    sdkInt: Int,
): AppColorScheme {
    return when {
        dynamicColor && sdkInt >= Build.VERSION_CODES.S -> AppColorScheme.Dynamic
        darkTheme -> AppColorScheme.FixedDark
        else -> AppColorScheme.FixedLight
    }
}

@Composable
fun EdgeGestureTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme =
        when (selectAppColorScheme(darkTheme, dynamicColor, Build.VERSION.SDK_INT)) {
            AppColorScheme.Dynamic ->
                if (darkTheme) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
            AppColorScheme.FixedDark -> DarkColorScheme
            AppColorScheme.FixedLight -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
