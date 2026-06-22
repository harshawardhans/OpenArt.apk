package com.example.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object ThemeManager {
    enum class ThemeType(
        val displayName: String,
        val description: String,
        val primaryColor: Color,
        val accentColor: Color,
        val isDark: Boolean
    ) {
        COSMIC_CYBERPUNK(
            displayName = "Cosmic Cyberpunk",
            description = "Neon purple & cyan on obsidian space background",
            primaryColor = Color(0xFF9E00FF),
            accentColor = Color(0xFF00F5D4),
            isDark = true
        ),
        GOLD_LUXURY(
            displayName = "Amber Luxury",
            description = "Rich gold & soft champagne on dark carbon background",
            primaryColor = Color(0xFFD4AF37),
            accentColor = Color(0xFFFFE066),
            isDark = true
        ),
        NORDIC_FROST(
            displayName = "Nordic Frost",
            description = "Glacier blue on fresh snow pristine light background",
            primaryColor = Color(0xFF1E40AF),
            accentColor = Color(0xFF0284C7),
            isDark = false
        ),
        RETRO_SYNTHWAVE(
            displayName = "Retro Synthwave",
            description = "Vibrant sunrise orange & hot pink on virtual twilight background",
            primaryColor = Color(0xFFFF5E00),
            accentColor = Color(0xFFFF007F),
            isDark = true
        ),
        SAGE_ZEN(
            displayName = "Sage Zen",
            description = "Earthy forest green & sage moss on warm eggshell background",
            primaryColor = Color(0xFF2E4F2B),
            accentColor = Color(0xFF606C38),
            isDark = false
        )
    }

    private var prefs: SharedPreferences? = null

    var currentTheme by mutableStateOf(ThemeType.COSMIC_CYBERPUNK)
        private set

    fun initialize(context: Context) {
        prefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)
        val savedThemeName = prefs?.getString("selected_theme", null)
        if (savedThemeName != null) {
            try {
                currentTheme = ThemeType.valueOf(savedThemeName)
            } catch (e: Exception) {
                // Fallback to default
            }
        }
    }

    fun selectTheme(theme: ThemeType) {
        currentTheme = theme
        prefs?.edit()?.putString("selected_theme", theme.name)?.apply()
    }

    val primaryNeon: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFF9E00FF)
            ThemeType.GOLD_LUXURY -> Color(0xFFD4AF37)
            ThemeType.NORDIC_FROST -> Color(0xFF1E40AF)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFFFF5E00)
            ThemeType.SAGE_ZEN -> Color(0xFF2E4F2B)
        }

    val secondaryCyan: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFF00F5D4)
            ThemeType.GOLD_LUXURY -> Color(0xFFFFE066)
            ThemeType.NORDIC_FROST -> Color(0xFF0284C7)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFFFF007F)
            ThemeType.SAGE_ZEN -> Color(0xFF606C38)
        }

    val accentMagenta: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFFFF007F)
            ThemeType.GOLD_LUXURY -> Color(0xFF996515)
            ThemeType.NORDIC_FROST -> Color(0xFF0369A1)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFF7928CA)
            ThemeType.SAGE_ZEN -> Color(0xFFBC6C25)
        }

    val cosmicDarkBackground: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFF080213)
            ThemeType.GOLD_LUXURY -> Color(0xFF0F0E0B)
            ThemeType.NORDIC_FROST -> Color(0xFFF5F8FC)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFF100220)
            ThemeType.SAGE_ZEN -> Color(0xFFFAFAF6)
        }

    val cosmicCardBackground: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFF140B27)
            ThemeType.GOLD_LUXURY -> Color(0xFF1C1A16)
            ThemeType.NORDIC_FROST -> Color(0xFFFFFFFF)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFF22053A)
            ThemeType.SAGE_ZEN -> Color(0xFFEFECE6)
        }

    val cosmicInputBackground: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFF1D0E39)
            ThemeType.GOLD_LUXURY -> Color(0xFF2A2621)
            ThemeType.NORDIC_FROST -> Color(0xFFE2EAF4)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFF330954)
            ThemeType.SAGE_ZEN -> Color(0xFFDFDAD1)
        }

    val borderGlow: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0x339E00FF)
            ThemeType.GOLD_LUXURY -> Color(0x33D4AF37)
            ThemeType.NORDIC_FROST -> Color(0x44B0C4DE)
            ThemeType.RETRO_SYNTHWAVE -> Color(0x33FF5E00)
            ThemeType.SAGE_ZEN -> Color(0x332E4F2B)
        }

    val textPrimary: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFFF1EAFF)
            ThemeType.GOLD_LUXURY -> Color(0xFFFFFDF5)
            ThemeType.NORDIC_FROST -> Color(0xFF1E293B)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFFFFF0FA)
            ThemeType.SAGE_ZEN -> Color(0xFF283618)
        }

    val textSecondary: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFFB0A2CE)
            ThemeType.GOLD_LUXURY -> Color(0xFFC2B69D)
            ThemeType.NORDIC_FROST -> Color(0xFF64748B)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFFFFB2E5)
            ThemeType.SAGE_ZEN -> Color(0xFF6E7864)
        }

    val sparkleGold: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0xFFFFD700)
            ThemeType.GOLD_LUXURY -> Color(0xFFFFD700)
            ThemeType.NORDIC_FROST -> Color(0xFFB45309)
            ThemeType.RETRO_SYNTHWAVE -> Color(0xFFFEE2E2)
            ThemeType.SAGE_ZEN -> Color(0xFFBC6C25)
        }

    val transparentGlow: Color
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> Color(0x1500F5D4)
            ThemeType.GOLD_LUXURY -> Color(0x15FFE066)
            ThemeType.NORDIC_FROST -> Color(0x150284C7)
            ThemeType.RETRO_SYNTHWAVE -> Color(0x15FFFF00)
            ThemeType.SAGE_ZEN -> Color(0x152E4F2B)
        }

    val backgroundGradientColors: List<Color>
        get() = when (currentTheme) {
            ThemeType.COSMIC_CYBERPUNK -> listOf(Color(0xFF080213), Color(0xFF080213), Color(0xFF130630))
            ThemeType.GOLD_LUXURY -> listOf(Color(0xFF0F0E0B), Color(0xFF0F0E0B), Color(0xFF1A1712))
            ThemeType.NORDIC_FROST -> listOf(Color(0xFFF5F8FC), Color(0xFFF5F8FC), Color(0xFFE2EAF4))
            ThemeType.RETRO_SYNTHWAVE -> listOf(Color(0xFF100220), Color(0xFF100220), Color(0xFF28053D))
            ThemeType.SAGE_ZEN -> listOf(Color(0xFFFAFAF6), Color(0xFFFAFAF6), Color(0xFFEDE9DE))
        }
}

// Delegate top-level property accessors so old code stays intact!
val PrimaryNeon: Color
    get() = ThemeManager.primaryNeon

val SecondaryCyan: Color
    get() = ThemeManager.secondaryCyan

val AccentMagenta: Color
    get() = ThemeManager.accentMagenta

val CosmicDarkBackground: Color
    get() = ThemeManager.cosmicDarkBackground

val CosmicCardBackground: Color
    get() = ThemeManager.cosmicCardBackground

val CosmicInputBackground: Color
    get() = ThemeManager.cosmicInputBackground

val BorderGlow: Color
    get() = ThemeManager.borderGlow

val TextPrimary: Color
    get() = ThemeManager.textPrimary

val TextSecondary: Color
    get() = ThemeManager.textSecondary

val SparkleGold: Color
    get() = ThemeManager.sparkleGold

val TransparentGlow: Color
    get() = ThemeManager.transparentGlow

val CosmicBackgroundGradient: List<Color>
    get() = ThemeManager.backgroundGradientColors
