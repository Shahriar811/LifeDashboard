package com.example.lifedashboard.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val RefreshingLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightAccent.copy(alpha = 0.2f),
    onPrimaryContainer = LightPrimary,
    
    secondary = LightSecondary,
    onSecondary = LightOnPrimary,
    secondaryContainer = LightSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = LightSecondary,
    
    tertiary = LightAccent,
    onTertiary = LightOnPrimary,
    tertiaryContainer = LightAccent.copy(alpha = 0.2f),
    onTertiaryContainer = LightAccent,
    
    background = LightBackground,
    onBackground = LightOnBackground,
    
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurface.copy(alpha = 0.7f),
    
    error = ErrorRed,
    onError = LightOnPrimary,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    
    outline = LightOnSurface.copy(alpha = 0.12f),
    outlineVariant = LightOnSurface.copy(alpha = 0.08f),
    
    scrim = LightOnBackground.copy(alpha = 0.32f),
    inverseSurface = LightOnBackground,
    inverseOnSurface = LightSurface,
    inversePrimary = LightPrimary.copy(alpha = 0.8f)
)

private val RefreshingDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = DarkPrimary,
    
    secondary = DarkSecondary,
    onSecondary = DarkOnPrimary,
    secondaryContainer = DarkSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = DarkSecondary,
    
    tertiary = DarkAccent,
    onTertiary = DarkOnPrimary,
    tertiaryContainer = DarkAccent.copy(alpha = 0.2f),
    onTertiaryContainer = DarkAccent,
    
    background = DarkBackground,
    onBackground = DarkOnBackground,
    
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurface.copy(alpha = 0.7f),
    
    error = ErrorRed,
    onError = DarkOnPrimary,
    errorContainer = ErrorRed.copy(alpha = 0.2f),
    onErrorContainer = ErrorRed,
    
    outline = DarkOnSurface.copy(alpha = 0.12f),
    outlineVariant = DarkOnSurface.copy(alpha = 0.08f),
    
    scrim = DarkOnBackground.copy(alpha = 0.32f),
    inverseSurface = DarkOnBackground,
    inverseOnSurface = DarkSurface,
    inversePrimary = DarkPrimary.copy(alpha = 0.8f)
)

@Composable
fun LifeDashboardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) RefreshingDarkColorScheme else RefreshingLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Set status bar color to match background
            window.statusBarColor = colorScheme.background.toArgb()
            // Set status bar icons to be light or dark depending on the theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RefreshingTypography,
        content = content
    )
}
