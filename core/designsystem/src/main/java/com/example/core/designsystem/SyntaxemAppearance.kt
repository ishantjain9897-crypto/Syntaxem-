package com.example.core.designsystem

import android.content.Context
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supported color sets for dynamic appearance customization in Syntaxem.
 */
enum class SyntaxemColorSet(
    val title: String,
    val subtitle: String,
    val isDark: Boolean
) {
    MIDNIGHT_NAVY(
        title = "Midnight Navy",
        subtitle = "Command Centre deep navy with cyan & periwinkle",
        isDark = true
    ),
    WARM_IVORY(
        title = "Warm Ivory",
        subtitle = "Textbook paper with deep teal, mint & terracotta",
        isDark = false
    ),
    COSMIC_OBSIDIAN(
        title = "Cosmic Obsidian",
        subtitle = "Orbit Lab ultra-deep space with neon cyan & solar gold",
        isDark = true
    ),
    EMERALD_BIOLAB(
        title = "Emerald Bio-Lab",
        subtitle = "Deep botanical spruce with vibrant emerald & amber",
        isDark = true
    ),
    SOLAR_PLASMA(
        title = "Solar Plasma",
        subtitle = "Quantum vacuum with radiant violet flame & ion beam",
        isDark = true
    ),
    CYBER_TITANIUM(
        title = "Cyber Titanium",
        subtitle = "Brushed steel gunmetal with high-voltage blue",
        isDark = true
    )
}

/**
 * Cohesive palette tokens consumed across all application screens.
 */
data class SyntaxemPalette(
    val id: SyntaxemColorSet,
    val name: String,
    val subtitle: String,
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val previewColors: List<Color>
) {
    fun toColorScheme(): ColorScheme {
        return if (isDark) {
            darkColorScheme(
                primary = primary,
                onPrimary = background,
                primaryContainer = surfaceVariant,
                onPrimaryContainer = textPrimary,
                secondary = secondary,
                onSecondary = background,
                secondaryContainer = surfaceVariant,
                onSecondaryContainer = textPrimary,
                tertiary = tertiary,
                onTertiary = background,
                background = background,
                onBackground = textPrimary,
                surface = surface,
                onSurface = textPrimary,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = textSecondary,
                outline = border,
                outlineVariant = border.copy(alpha = 0.5f)
            )
        } else {
            lightColorScheme(
                primary = primary,
                onPrimary = Color.White,
                primaryContainer = secondary.copy(alpha = 0.15f),
                onPrimaryContainer = primary,
                secondary = secondary,
                onSecondary = Color.White,
                secondaryContainer = surfaceVariant,
                onSecondaryContainer = textPrimary,
                tertiary = tertiary,
                onTertiary = Color.White,
                background = background,
                onBackground = textPrimary,
                surface = surface,
                onSurface = textPrimary,
                surfaceVariant = surfaceVariant,
                onSurfaceVariant = textSecondary,
                outline = border,
                outlineVariant = border.copy(alpha = 0.5f)
            )
        }
    }

    companion object {
        val MIDNIGHT_NAVY_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.MIDNIGHT_NAVY,
            name = "Midnight Navy",
            subtitle = "Command Centre signature laboratory appearance",
            isDark = true,
            background = Color(0xFF0B101E),
            surface = Color(0xFF151C2E),
            surfaceVariant = Color(0xFF1E263D),
            primary = Color(0xFF6C6CF6),
            secondary = Color(0xFF2ED8A7),
            tertiary = Color(0xFFFDB043),
            border = Color(0xFF25304C),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF8E9BB5),
            previewColors = listOf(Color(0xFF0B101E), Color(0xFF151C2E), Color(0xFF2ED8A7), Color(0xFF6C6CF6), Color(0xFFFDB043))
        )

        val WARM_IVORY_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.WARM_IVORY,
            name = "Warm Ivory",
            subtitle = "Lumen Flow textbook calm and deep teal clarity",
            isDark = false,
            background = Color(0xFFF7F8F3),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFEFF2EC),
            primary = Color(0xFF356B59),
            secondary = Color(0xFF2F855A),
            tertiary = Color(0xFFC86F58),
            border = Color(0xFFDDE2DA),
            textPrimary = Color(0xFF202824),
            textSecondary = Color(0xFF59635D),
            previewColors = listOf(Color(0xFFF7F8F3), Color(0xFFFFFFFF), Color(0xFF356B59), Color(0xFF2F855A), Color(0xFFC86F58))
        )

        val COSMIC_OBSIDIAN_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.COSMIC_OBSIDIAN,
            name = "Cosmic Obsidian",
            subtitle = "Deep space carbon with high-luminance telemetry",
            isDark = true,
            background = Color(0xFF08090C),
            surface = Color(0xFF12141C),
            surfaceVariant = Color(0xFF1A1C28),
            primary = Color(0xFF00E5FF),
            secondary = Color(0xFF00FFA3),
            tertiary = Color(0xFFFFB300),
            border = Color(0xFF232638),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFF90A4AE),
            previewColors = listOf(Color(0xFF08090C), Color(0xFF12141C), Color(0xFF00E5FF), Color(0xFF00FFA3), Color(0xFFFFB300))
        )

        val EMERALD_BIOLAB_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.EMERALD_BIOLAB,
            name = "Emerald Bio-Lab",
            subtitle = "Botanical spruce with vivid chlorophyll & amber",
            isDark = true,
            background = Color(0xFF06140E),
            surface = Color(0xFF0E2219),
            surfaceVariant = Color(0xFF163327),
            primary = Color(0xFF10B981),
            secondary = Color(0xFF34D399),
            tertiary = Color(0xFFF59E0B),
            border = Color(0xFF1B4233),
            textPrimary = Color(0xFFF0FDF4),
            textSecondary = Color(0xFF86A796),
            previewColors = listOf(Color(0xFF06140E), Color(0xFF0E2219), Color(0xFF10B981), Color(0xFF34D399), Color(0xFFF59E0B))
        )

        val SOLAR_PLASMA_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.SOLAR_PLASMA,
            name = "Solar Plasma",
            subtitle = "Quantum high-energy physics with radiant violet & ion pink",
            isDark = true,
            background = Color(0xFF0E0A17),
            surface = Color(0xFF181226),
            surfaceVariant = Color(0xFF241B38),
            primary = Color(0xFFA855F7),
            secondary = Color(0xFFEC4899),
            tertiary = Color(0xFFFB923C),
            border = Color(0xFF2D2245),
            textPrimary = Color(0xFFFFFFFF),
            textSecondary = Color(0xFFA396B8),
            previewColors = listOf(Color(0xFF0E0A17), Color(0xFF181226), Color(0xFFA855F7), Color(0xFFEC4899), Color(0xFFFB923C))
        )

        val CYBER_TITANIUM_PALETTE = SyntaxemPalette(
            id = SyntaxemColorSet.CYBER_TITANIUM,
            name = "Cyber Titanium",
            subtitle = "Steel gunmetal instrument console with laser cyan",
            isDark = true,
            background = Color(0xFF0D1117),
            surface = Color(0xFF161B22),
            surfaceVariant = Color(0xFF21262D),
            primary = Color(0xFF38BDF8),
            secondary = Color(0xFF4ADE80),
            tertiary = Color(0xFFFACC15),
            border = Color(0xFF30363D),
            textPrimary = Color(0xFFF0F6FC),
            textSecondary = Color(0xFF8B949E),
            previewColors = listOf(Color(0xFF0D1117), Color(0xFF161B22), Color(0xFF38BDF8), Color(0xFF4ADE80), Color(0xFFFACC15))
        )

        val ALL_PALETTES = listOf(
            MIDNIGHT_NAVY_PALETTE,
            WARM_IVORY_PALETTE,
            COSMIC_OBSIDIAN_PALETTE,
            EMERALD_BIOLAB_PALETTE,
            SOLAR_PLASMA_PALETTE,
            CYBER_TITANIUM_PALETTE
        )

        fun forSet(set: SyntaxemColorSet): SyntaxemPalette {
            return when (set) {
                SyntaxemColorSet.MIDNIGHT_NAVY -> MIDNIGHT_NAVY_PALETTE
                SyntaxemColorSet.WARM_IVORY -> WARM_IVORY_PALETTE
                SyntaxemColorSet.COSMIC_OBSIDIAN -> COSMIC_OBSIDIAN_PALETTE
                SyntaxemColorSet.EMERALD_BIOLAB -> EMERALD_BIOLAB_PALETTE
                SyntaxemColorSet.SOLAR_PLASMA -> SOLAR_PLASMA_PALETTE
                SyntaxemColorSet.CYBER_TITANIUM -> CYBER_TITANIUM_PALETTE
            }
        }
    }
}

/**
 * Singleton managing appearance persistence and live dynamic updates.
 */
object SyntaxemThemeManager {

    private const val PREFS_NAME = "syntaxem_appearance_prefs"
    private const val KEY_COLOR_SET = "selected_color_set"

    private val _currentColorSet = MutableStateFlow(SyntaxemColorSet.MIDNIGHT_NAVY)
    val currentColorSet: StateFlow<SyntaxemColorSet> = _currentColorSet.asStateFlow()

    private val _currentPalette = MutableStateFlow(SyntaxemPalette.MIDNIGHT_NAVY_PALETTE)
    val currentPalette: StateFlow<SyntaxemPalette> = _currentPalette.asStateFlow()

    private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedName = prefs.getString(KEY_COLOR_SET, SyntaxemColorSet.MIDNIGHT_NAVY.name)
        val set = try {
            SyntaxemColorSet.valueOf(savedName ?: SyntaxemColorSet.MIDNIGHT_NAVY.name)
        } catch (e: Exception) {
            SyntaxemColorSet.MIDNIGHT_NAVY
        }
        setColorSetInternal(set, persist = false)
    }

    fun setColorSet(colorSet: SyntaxemColorSet) {
        setColorSetInternal(colorSet, persist = true)
    }

    private fun setColorSetInternal(colorSet: SyntaxemColorSet, persist: Boolean) {
        _currentColorSet.value = colorSet
        _currentPalette.value = SyntaxemPalette.forSet(colorSet)

        if (persist) {
            appContext?.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                ?.edit()
                ?.putString(KEY_COLOR_SET, colorSet.name)
                ?.apply()
        }
    }
}

val LocalSyntaxemPalette = staticCompositionLocalOf { SyntaxemPalette.MIDNIGHT_NAVY_PALETTE }

object SyntaxemTheme {
    val palette: SyntaxemPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalSyntaxemPalette.current
}
