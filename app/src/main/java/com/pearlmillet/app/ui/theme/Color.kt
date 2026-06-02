package com.pearlmillet.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Pearl Millet Care - Brand Growth Palette
 * Organized by "Life Cycle" colors: Authority, Research, Growth, and Harvest.
 */


val BrandDeepGreen = Color(0xFF1B5E20)    // Dark, prestigious green
val BrandMediumGreen = Color(0xFF388E3C)  // Vibrant agricultural green
val OnDeepSurface = Color.White


val SproutLight = Color(0xFFF1F8E9)      // Very soft sage
val SproutMedium = Color(0xFFDCEDC8)     // Soft leaf green
val SproutDark = Color(0xFF33691E)       // For text on sprout backgrounds


val ActionGreen = Color(0xFF2E7D32)      // Standard interactive green
val LeafVibrant = Color(0xFF81C784)      // Glow effect / highlight green


val HarvestGold = Color(0xFFFFA000)      // Deep amber/gold
val HarvestSand = Color(0xFFFFD54F)      // Light golden highlight


val AppBackground = Color(0xFFFDFDFD)    // Clean off-white
val SurfaceWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF121212)      // High contrast for outdoor use
val TextSecondary = Color(0xFF575757)    // Muted descriptions
val SeverityError = Color(0xFFD32F2F)    // Professional Red
val SeverityWarning = Color(0xFFF57C00)  // Deep Orange
val InfoBlue = Color(0xFF1976D2)         // Scientific Blue

/**
 * App Brushes - The "Four Gradient" Placement System
 */
object AppBrushes {

    val Authority = Brush.verticalGradient(
        colors = listOf(BrandDeepGreen, BrandMediumGreen)
    )

    val ResearchSubtle = Brush.horizontalGradient(
        colors = listOf(SproutLight, SproutMedium)
    )

    val Interaction = Brush.linearGradient(
        colors = listOf(ActionGreen, LeafVibrant)
    )

    val Harvest = Brush.verticalGradient(
        colors = listOf(HarvestGold, HarvestSand)
    )
}