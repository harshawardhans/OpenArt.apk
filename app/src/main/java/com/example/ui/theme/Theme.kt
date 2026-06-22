package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun ArtGeneratorTheme(
    content: @Composable () -> Unit
) {
    val isDark = ThemeManager.currentTheme.isDark
    val currentThemeType = ThemeManager.currentTheme

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = PrimaryNeon,
            onPrimary = Color.White,
            secondary = SecondaryCyan,
            onSecondary = Color.Black,
            tertiary = AccentMagenta,
            onTertiary = Color.White,
            background = CosmicDarkBackground,
            onBackground = TextPrimary,
            surface = CosmicCardBackground,
            onSurface = TextPrimary,
            surfaceVariant = CosmicInputBackground,
            onSurfaceVariant = TextSecondary,
            outline = BorderGlow
        )
    } else {
        lightColorScheme(
            primary = PrimaryNeon,
            onPrimary = Color.White,
            secondary = SecondaryCyan,
            onSecondary = Color.Black,
            tertiary = AccentMagenta,
            onTertiary = Color.White,
            background = CosmicDarkBackground,
            onBackground = TextPrimary,
            surface = CosmicCardBackground,
            onSurface = TextPrimary,
            surfaceVariant = CosmicInputBackground,
            onSurfaceVariant = TextSecondary,
            outline = BorderGlow
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
