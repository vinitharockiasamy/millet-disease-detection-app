package com.pearlmillet.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// NOTE: All color values are imported from Color.kt. No local definitions here!


private val LightColors = lightColorScheme(
    // Primary: Institutional Authority
    primary = BrandDeepGreen,
    onPrimary = OnDeepSurface,
    primaryContainer = SproutLight, // Soft Sprout for container
    onPrimaryContainer = BrandDeepGreen,

    // Secondary: Interactive Growth
    secondary = ActionGreen,
    onSecondary = OnDeepSurface,
    secondaryContainer = SproutMedium,

    // Tertiary: Harvest & Value
    tertiary = HarvestGold,
    onTertiary = TextPrimary,

    // Backgrounds
    background = AppBackground,
    onBackground = TextPrimary,

    // Surface
    surface = SurfaceWhite,
    onSurface = TextPrimary,
    surfaceVariant = SproutLight,
    onSurfaceVariant = TextSecondary,

    error = SeverityError,
    onError = Color.White,
    outline = BrandMediumGreen
)

private val DarkColors = darkColorScheme(
    primary = BrandDeepGreen, // Keep branding consistent
    onPrimary = OnDeepSurface,
    primaryContainer = SproutDark,
    onPrimaryContainer = SproutLight,

    secondary = BrandMediumGreen,
    onSecondary = OnDeepSurface,
    secondaryContainer = SproutDark,

    tertiary = HarvestGold,
    onTertiary = TextPrimary,

    background = Color(0xFF121212), // Dark mode backup
    onBackground = Color.White,

    surface = Color(0xFF1E1E1E),
    onSurface = Color.White,
    surfaceVariant = SproutDark,
    onSurfaceVariant = SproutLight,

    error = SeverityError,
    onError = Color.White,
    outline = BrandMediumGreen
)


/**
 * PearlMilletCareTheme
 * - Uses correct color roles for light and dark schemes.
 * - Handles system UI bar colors based on the scheme.
 */
@Composable
fun PearlMilletCareTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val primaryColor = colorScheme.primary.toArgb()
            val surfaceColor = colorScheme.surface.toArgb()
            val isLandscape = view.resources.configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (isLandscape) {
                    WindowCompat.setDecorFitsSystemWindows(window, false)
                    window.statusBarColor = Color.Transparent.toArgb()
                    window.navigationBarColor = Color.Transparent.toArgb()
                } else {
                    WindowCompat.setDecorFitsSystemWindows(window, true)
                    window.statusBarColor = primaryColor
                    window.navigationBarColor = surfaceColor
                }
            }

            // Controls status bar icon visibility based on the background color.
            WindowCompat.getInsetsController(window, view).apply {
                // If status bar is transparent (landscape) or primary is light/dark, customize icons
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Assuming Typography is defined elsewhere
        shapes = Shapes, // Assuming Shapes is defined elsewhere
        content = content
    )
}