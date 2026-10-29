package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryCyan,
    onPrimary = Color(0xFF003549),
    primaryContainer = Color(0xFF004D69),
    onPrimaryContainer = Color(0xFFBEE9FF),
    secondary = SecondaryIndigo,
    onSecondary = Color(0xFF1E2878),
    secondaryContainer = Color(0xFF353E90),
    onSecondaryContainer = Color(0xFFE0E0FF),
    tertiary = AccentAmber,
    onTertiary = Color(0xFF452B00),
    tertiaryContainer = Color(0xFF634000),
    onTertiaryContainer = Color(0xFFFFDDB4),
    error = ErrorRose,
    onError = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimaryDark,
    surface = SurfaceDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = SurfaceCardDark,
    onSurfaceVariant = TextSecondaryDark
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryCyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFBEE9FF),
    onPrimaryContainer = Color(0xFF001F2B),
    secondary = Color(0xFF4D56A9),
    onSecondary = Color.White,
    tertiary = Color(0xFF825500),
    onTertiary = Color.White,
    error = ErrorRose,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = SurfaceCardLight,
    onSurfaceVariant = TextSecondaryLight
)

@Composable
fun CalcMasterTheme(
    darkTheme: Boolean = true, // Default to scientific dark theme for optimal contrast & battery
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    com.example.core.designsystem.CalcMasterTheme(
        darkTheme = darkTheme,
        content = content
    )
}

// Retain alias for existing usages
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) = CalcMasterTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
