package com.example.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Standardized Spacing Tokens adhering strictly to the Material 3 8dp grid.
 * Guarantees clean, consistent density and accessible 48dp touch targets.
 */
@Immutable
data class Spacing(
    val none: Dp = 0.dp,
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 12.dp,
    val default: Dp = 16.dp,
    val large: Dp = 20.dp,
    val extraLarge: Dp = 24.dp,
    val huge: Dp = 32.dp,
    val massive: Dp = 48.dp,

    // Domain Specific Semantic Tokens
    val touchTargetMin: Dp = 48.dp,
    val screenHorizontalGutter: Dp = 16.dp,
    val screenVerticalGutter: Dp = 12.dp,
    val cardPadding: Dp = 16.dp,
    val cardCornerRadius: Dp = 16.dp,
    val displayBezelCornerRadius: Dp = 22.dp,
    val keyGap: Dp = 5.dp,
    val keyHeight: Dp = 48.dp,
    val keyCornerRadius: Dp = 12.dp,
    val chipSpacing: Dp = 6.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

val MaterialTheme.spacing: Spacing
    @Composable
    @ReadOnlyComposable
    get() = LocalSpacing.current
