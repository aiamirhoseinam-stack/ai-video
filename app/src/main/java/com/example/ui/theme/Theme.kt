package com.example.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = KhanGreenPrimary,
    onPrimary = KhanSurface,
    primaryContainer = KhanGreenContainer,
    onPrimaryContainer = KhanOnGreenContainer,
    secondary = KhanGoldPrimary,
    onSecondary = KhanSurface,
    secondaryContainer = KhanGoldContainer,
    onSecondaryContainer = KhanOnGoldContainer,
    tertiary = KhanRuby,
    onTertiary = KhanSurface,
    tertiaryContainer = KhanRubyContainer,
    background = KhanBackground,
    onBackground = KhanTextPrimary,
    surface = KhanSurface,
    onSurface = KhanTextPrimary,
    surfaceVariant = KhanSurfaceVariant,
    onSurfaceVariant = KhanTextSecondary,
    outline = KhanBorder
)

private val DarkColorScheme = darkColorScheme(
    primary = KhanGreenLight,
    onPrimary = KhanGreenDark,
    primaryContainer = KhanGreenPrimary,
    onPrimaryContainer = KhanGreenContainer,
    secondary = KhanGoldLight,
    onSecondary = KhanGoldDark,
    secondaryContainer = KhanGoldDark,
    onSecondaryContainer = KhanGoldContainer,
    tertiary = KhanRuby,
    onTertiary = KhanSurface,
    background = KhanDarkBackground,
    onBackground = KhanDarkTextPrimary,
    surface = KhanDarkSurface,
    onSurface = KhanDarkTextPrimary,
    surfaceVariant = KhanDarkSurfaceVariant,
    onSurfaceVariant = KhanDarkTextSecondary,
    outline = KhanDarkSurfaceVariant
)

@Composable
fun KhanBabaeeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.surface.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
