package com.example.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

enum class AppThemeMode {
    WARM_LIGHT,
    DEEP_FOREST_DARK,
    HIGH_CONTRAST
}

/**
 * Syntaxem & CalcMaster Material 3 Theme with dynamic multi-color set support.
 * Integrates SyntaxemThemeManager palettes (Midnight Navy, Warm Ivory, Cosmic Obsidian,
 * Emerald Bio-Lab, Solar Plasma, Cyber Titanium) and provides LocalSyntaxemPalette.
 */
@Composable
fun CalcMasterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    content: @Composable () -> Unit
) {
    val activePalette by SyntaxemThemeManager.currentPalette.collectAsState()
    val colorScheme = when {
        highContrast -> HighContrastColorScheme
        else -> activePalette.toColorScheme()
    }
    val customColors = if (activePalette.isDark) DarkCustomColors else LightCustomColors
    val spacing = Spacing()
    val scientificTypography = ScientificTypography()

    CompositionLocalProvider(
        LocalSyntaxemPalette provides activePalette,
        LocalSpacing provides spacing,
        LocalCalcMasterColors provides customColors,
        LocalScientificTypography provides scientificTypography
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content
        )
    }
}
