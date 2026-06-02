package com.pearlmillet.app.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

// Premium Color Palette (Centralized)
object AppColors {
    // Mapped to new Brand Palette
    val DeepGreen = BrandDeepGreen
    val VividGreen = LeafVibrant
    val VibrantGreen = ActionGreen
    val MintGreen = SproutLight
    val GlassWhite = Color(0xCCFFFFFF)
    val SurfaceBg = AppBackground
    val TextDark = TextPrimary
    val GoldAccent = HarvestGold
    
    // Additional functional colors
    val ErrorRed = SeverityError
    val WarningOrange = SeverityWarning
    val InfoBlue = com.pearlmillet.app.ui.theme.InfoBlue
    val CardDark = BrandDeepGreen
    val CardDarkGradientEnd = BrandMediumGreen
    val CardBg = SurfaceWhite
    val SeverePurple = Color(0xFF9C27B0)
}

// Dynamic Typography Logic
object AppTypography {
    
    // Helper to get font size based on language
    fun getFontSize(isTamil: Boolean, tamilSize: TextUnit, englishSize: TextUnit): TextUnit {
        return if (isTamil) tamilSize else englishSize
    }

    // Standardized Text Sizes
    fun headerSize(isTamil: Boolean) = getFontSize(isTamil, 19.sp, 20.sp)
    fun subHeaderSize(isTamil: Boolean) = getFontSize(isTamil, 18.sp, 19.sp)
    fun bodySize(isTamil: Boolean) = getFontSize(isTamil, 12.sp, 13.sp)
    fun smallSize(isTamil: Boolean) = getFontSize(isTamil, 10.sp, 11.sp)
    fun tinySize(isTamil: Boolean) = getFontSize(isTamil, 9.sp, 10.sp)
    
    // Card Specifics
    fun cardTitleSize(isTamil: Boolean) = getFontSize(isTamil, 11.sp, 12.sp)
    fun cardDescSize(isTamil: Boolean) = getFontSize(isTamil, 11.sp, 12.sp)
}

// Layout Constants
object AppLayout {
    val ScreenPadding = 24.dp
    val DefaultHorizontalPadding = 16.dp
    val CardCornerRadius = 14.dp
    val CardElevation = 4.dp
    
    // Responsive Max Widths
    val DialogMaxWidth = 400.dp
    val BottomSheetMaxWidth = 600.dp
    val ContentMaxWidth = 800.dp
    
    // Heights
    val ButtonMinHeight = 48.dp
    val DialogMaxHeight = 500.dp
    
    // Spacing Scale
    val SpaceExtraSmall = 4.dp
    val SpaceSmall = 8.dp
    val SpaceMedium = 16.dp
    val SpaceLarge = 24.dp
    val SpaceExtraLarge = 32.dp
}

