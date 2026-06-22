package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
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

@Composable
fun ArtGeneratorTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
