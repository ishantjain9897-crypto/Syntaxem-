package com.example.core.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ============================================================================
// PRIMITIVE COLOR PALETTE TOKENS (Titanium Slate, Precision Cyan, Jewel Accents)
// ============================================================================

// Precision Cyan (Primary Action, Brand, Verified Status)
val Cyan900 = Color(0xFF0369A1)
val Cyan800 = Color(0xFF0284C7)
val Cyan600 = Color(0xFF0EA5E9)
val Cyan400 = Color(0xFF38BDF8)
val Cyan200 = Color(0xFFBAE6FD)
val Cyan100 = Color(0xFFE0F2FE)

// Titanium Slate (Deep Neutral Surfaces & Instrument Bezels)
val Slate950 = Color(0xFF080C14) // Deepest instrument chassis
val Slate900 = Color(0xFF0F172A) // LCD & secondary background
val Slate850 = Color(0xFF15192C) // Card & panel surface
val Slate800 = Color(0xFF1E293B) // Keypad keys base
val Slate700 = Color(0xFF334155) // Bezel borders & dividers
val Slate600 = Color(0xFF475569) // Muted icons
val Slate500 = Color(0xFF64748B) // Subtitle & helper labels
val Slate400 = Color(0xFF94A3B8) // Secondary typography
val Slate200 = Color(0xFFE2E8F0) // High contrast text
val Slate50  = Color(0xFFF8FAFC) // Primary display output

// Gold & Amber (Physics Energy, Highlights, Warning Badges)
val Amber600 = Color(0xFFD97706)
val Amber500 = Color(0xFFF59E0B)
val Amber400 = Color(0xFFFBBF24)
val Amber100 = Color(0xFFFEF3C7)

// Emerald Green (Deterministic Verification, Success, Calibration Valid)
val Emerald700 = Color(0xFF047857)
val Emerald600 = Color(0xFF059669)
val Emerald500 = Color(0xFF10B981)
val Emerald400 = Color(0xFF34D399)
val Emerald100 = Color(0xFFD1FAE5)

// Coral Crimson (Clear AC, Domain Errors, Danger Bounds)
val Crimson700 = Color(0xFFBE123C)
val Crimson600 = Color(0xFFE11D48)
val Crimson500 = Color(0xFFF43F5E)
val Crimson100 = Color(0xFFFFE4E6)

// Violet & Indigo (Trigonometry, Advanced Mathematical Operators)
val Indigo600 = Color(0xFF4F46E5)
val Indigo500 = Color(0xFF6366F1)
val Indigo400 = Color(0xFF818CF8)

// ============================================================================
// LUMEN FLOW DESIGN SYSTEM TOKENS (Warm canvas, forest green, mint, lavender, terracotta)
// ============================================================================

val LumenBackground = Color(0xFFF7F8F3)       // Warm, calm canvas
val LumenSurface = Color(0xFFFFFFFF)          // Main content surfaces
val LumenPrimary = Color(0xFF356B59)          // Main actions and progress
val LumenPrimaryDark = Color(0xFF244C40)      // Strong emphasis
val LumenLavender = Color(0xFFE9E4F5)         // Secondary learning areas
val LumenMint = Color(0xFFDDEFE5)             // Positive learning feedback
val LumenTerracotta = Color(0xFFC86F58)       // Carefully used emphasis
val LumenTextPrimary = Color(0xFF202824)      // Main text
val LumenTextSecondary = Color(0xFF59635D)    // Supporting text
val LumenBorder = Color(0xFFDDE2DA)           // Dividers and boundaries
val LumenError = Color(0xFFB42332)            // Errors with explicit labels

// Syntaxem Midnight Blue & Vibrant Accent Tokens (Matching UI Mockups)
val ForestDarkBackground = Color(0xFF0B101E)       // Deep midnight navy
val ForestDarkSurface = Color(0xFF151C2E)          // Elevated card/container
val ForestDarkSurfaceVariant = Color(0xFF1E263D)   // Sub-card / node surfaces
val ForestDarkPrimary = Color(0xFF6C6CF6)          // Royal periwinkle accent
val ForestDarkPrimaryContainer = Color(0xFF252952) // Active tab container
val ForestDarkOnPrimaryContainer = Color(0xFFE4E4FF)
val ForestDarkSecondary = Color(0xFF2ED8A7)        // Vibrant mint
val ForestDarkSecondaryContainer = Color(0xFF1A382F)
val ForestDarkTertiary = Color(0xFFFDB043)         // Warm amber accent
val ForestDarkTextPrimary = Color(0xFFFFFFFF)      // Crisp white text
val ForestDarkTextSecondary = Color(0xFF8E9BB5)    // Muted slate label text
val ForestDarkBorder = Color(0xFF25304C)           // Subtle card dividers

val SyntaxemBackground = ForestDarkBackground
val SyntaxemSurface = ForestDarkSurface
val SyntaxemSurfaceVariant = ForestDarkSurfaceVariant
val SyntaxemPrimary = ForestDarkPrimary
val SyntaxemMint = Color(0xFF2ED8A7)
val SyntaxemAmber = Color(0xFFFDB043)
val SyntaxemPink = Color(0xFFFF6B8B)
val SyntaxemTextPrimary = ForestDarkTextPrimary
val SyntaxemTextSecondary = ForestDarkTextSecondary
val SyntaxemBorder = ForestDarkBorder

// High Contrast Tokens
val HighContrastBackground = Color(0xFF000000)
val HighContrastSurface = Color(0xFF121212)
val HighContrastPrimary = Color(0xFF00FF88)
val HighContrastTextPrimary = Color(0xFFFFFFFF)
val HighContrastTextSecondary = Color(0xFFFFFF00)
val HighContrastBorder = Color(0xFFFFFFFF)

// ============================================================================
// MATERIAL 3 COLOR SCHEMES
// ============================================================================

val DarkColorScheme: ColorScheme = darkColorScheme(
    primary = ForestDarkPrimary,
    onPrimary = ForestDarkBackground,
    primaryContainer = ForestDarkPrimaryContainer,
    onPrimaryContainer = ForestDarkOnPrimaryContainer,

    secondary = ForestDarkSecondary,
    onSecondary = ForestDarkBackground,
    secondaryContainer = ForestDarkSecondaryContainer,
    onSecondaryContainer = Color(0xFFE9E4F5),

    tertiary = ForestDarkTertiary,
    onTertiary = ForestDarkBackground,
    tertiaryContainer = Color(0xFF5C2D22),
    onTertiaryContainer = Color(0xFFFFDBCF),

    error = LumenError,
    onError = Color.White,
    errorContainer = Color(0xFF54121A),
    onErrorContainer = Color(0xFFFFDAD6),

    background = ForestDarkBackground,
    onBackground = ForestDarkTextPrimary,

    surface = ForestDarkSurface,
    onSurface = ForestDarkTextPrimary,
    surfaceVariant = ForestDarkSurfaceVariant,
    onSurfaceVariant = ForestDarkTextSecondary,

    outline = ForestDarkBorder,
    outlineVariant = Color(0xFF26332C)
)

val LightColorScheme: ColorScheme = lightColorScheme(
    primary = LumenPrimary,
    onPrimary = Color.White,
    primaryContainer = LumenMint,
    onPrimaryContainer = LumenPrimaryDark,

    secondary = Color(0xFF5C527F),
    onSecondary = Color.White,
    secondaryContainer = LumenLavender,
    onSecondaryContainer = Color(0xFF231846),

    tertiary = LumenTerracotta,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBCF),
    onTertiaryContainer = Color(0xFF3B0900),

    error = LumenError,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    background = LumenBackground,
    onBackground = LumenTextPrimary,

    surface = LumenSurface,
    onSurface = LumenTextPrimary,
    surfaceVariant = Color(0xFFEFF2EC),
    onSurfaceVariant = LumenTextSecondary,

    outline = LumenBorder,
    outlineVariant = Color(0xFFECEFE8)
)

val HighContrastColorScheme: ColorScheme = darkColorScheme(
    primary = HighContrastPrimary,
    onPrimary = Color.Black,
    secondary = Color(0xFF00E5FF),
    onSecondary = Color.Black,
    tertiary = Color(0xFFFFD600),
    onTertiary = Color.Black,
    background = HighContrastBackground,
    onBackground = HighContrastTextPrimary,
    surface = HighContrastSurface,
    onSurface = HighContrastTextPrimary,
    outline = HighContrastBorder
)

// ============================================================================
// EXTENDED SEMANTIC DESIGN TOKENS (Scientific Instrument Aesthetics)
// ============================================================================

@Immutable
data class CalcMasterCustomColors(
    val lcdBackground: Color,
    val lcdBorder: Color,
    val lcdGridLines: Color,
    val lcdResultText: Color,
    val keyNumericBgTop: Color,
    val keyNumericBgBottom: Color,
    val keyOperatorBgTop: Color,
    val keyOperatorBgBottom: Color,
    val keyActionBgTop: Color,
    val keyActionBgBottom: Color,
    val keyEqualBgTop: Color,
    val keyEqualBgBottom: Color,
    val badgeSuccessBg: Color,
    val badgeSuccessText: Color,
    val badgeWarningBg: Color,
    val badgeWarningText: Color
)

val DarkCustomColors = CalcMasterCustomColors(
    lcdBackground = ForestDarkBackground,
    lcdBorder = ForestDarkBorder,
    lcdGridLines = Color(0x1A529D84),
    lcdResultText = ForestDarkPrimary,
    keyNumericBgTop = ForestDarkSurface,
    keyNumericBgBottom = ForestDarkSurfaceVariant,
    keyOperatorBgTop = ForestDarkPrimaryContainer,
    keyOperatorBgBottom = Color(0xFF1E3C33),
    keyActionBgTop = Crimson600,
    keyActionBgBottom = Crimson700,
    keyEqualBgTop = ForestDarkPrimary,
    keyEqualBgBottom = ForestDarkPrimaryContainer,
    badgeSuccessBg = ForestDarkOnPrimaryContainer.copy(alpha = 0.2f),
    badgeSuccessText = ForestDarkPrimary,
    badgeWarningBg = ForestDarkTertiary.copy(alpha = 0.2f),
    badgeWarningText = ForestDarkTertiary
)

val LightCustomColors = CalcMasterCustomColors(
    lcdBackground = LumenBackground,
    lcdBorder = LumenBorder,
    lcdGridLines = Color(0x14356B59),
    lcdResultText = LumenPrimaryDark,
    keyNumericBgTop = Color(0xFFFFFFFF),
    keyNumericBgBottom = Color(0xFFEFF2EC),
    keyOperatorBgTop = LumenMint,
    keyOperatorBgBottom = Color(0xFFC7E3D3),
    keyActionBgTop = Crimson500,
    keyActionBgBottom = Crimson600,
    keyEqualBgTop = LumenPrimary,
    keyEqualBgBottom = LumenPrimaryDark,
    badgeSuccessBg = LumenMint,
    badgeSuccessText = LumenPrimaryDark,
    badgeWarningBg = Color(0xFFFFDBCF),
    badgeWarningText = LumenTerracotta
)

val LocalCalcMasterColors = staticCompositionLocalOf { DarkCustomColors }
