package com.pearlmillet.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.R // Make sure this points to your actual R file
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.ui.theme.AppLayout
import kotlinx.coroutines.delay

@Composable
fun LanguageSelectScreen(onLanguageSelected: (String) -> Unit) {
    var selected by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(selected) {
        if (selected != null) {
            delay(500)
            onLanguageSelected(selected!!)
        }
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val canvasHeight = if (isLandscape) 180.dp else 285.dp
 
    // Dynamic dimensions for premium responsive scaling
    val topSpacerHeight = if (isLandscape) 12.dp else 44.dp
    val logoSize = if (isLandscape) 48.dp else 65.dp
    val logoToTitleSpacer = if (isLandscape) 8.dp else 21.dp
    val titleToSubtitleSpacer = if (isLandscape) 4.dp else 6.dp
    val appTitleFontSize = if (isLandscape) 11.sp else 12.sp
    val subtitleFontSize = if (isLandscape) 22.sp else 32.sp
    val headerToTileSpacer = if (isLandscape) 16.dp else 32.dp

    PearlMilletScaffold(
        containerColor = Color(0xFFF9FBF9),
        enableScrolling = true
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            /* 1. HEADER (Dynamic Height & Background Canvas) */
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(canvasHeight)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val curveStartY = if (isLandscape) size.height * 0.95f else size.height * 0.85f
                    val curveControlY = if (isLandscape) size.height * 1.02f else size.height * 1.1f
                    val curveEndY = if (isLandscape) size.height * 0.9f else size.height * 0.65f
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, curveStartY)
                        quadraticTo(
                            size.width * 0.5f, curveControlY,
                            0f, curveEndY
                        )
                        close()
                    }
                    drawPath(
                        path = path,
                        brush = Brush.verticalGradient(
                            colors = listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))
                        )
                    )
                }

                // Centered Header UI Elements
                Column(
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(topSpacerHeight))

                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.1f),
                        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier.size(logoSize)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(1.5f),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(logoToTitleSpacer))

                    Text(
                        text = "PEARL MILLET CARE",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color(0xFFA5D6A7),
                            fontSize = appTitleFontSize,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Spacer(modifier = Modifier.height(titleToSubtitleSpacer))
                    Text(
                        text = "Select Language",
                        fontSize = subtitleFontSize,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            /* 2. TILES SECTION (Sequential Layout below Header) */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
                    .padding(horizontal = AppLayout.DefaultHorizontalPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(headerToTileSpacer))

                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    PremiumLanguageTile(
                        title = "தமிழ்",
                        subtitle = "உங்கள் விவசாய சேவைகளுக்கு",
                        isSelected = selected == "ta",
                        onSelect = { selected = "ta" }
                    )

                    PremiumLanguageTile(
                        title = "English",
                        subtitle = "For farming services",
                        isSelected = selected == "en",
                        onSelect = { selected = "en" }
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun PremiumLanguageTile(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val bgColor by animateColorAsState(if (isSelected) Color.White else Color(0xFFF2F5F2))
    val elevation by animateDpAsState(if (isSelected) 12.dp else 2.dp)
    val textColor by animateColorAsState(if (isSelected) AppColors.DeepGreen else Color(0xFF455A64))


    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(95.dp)
            .clickable { onSelect() }
            .shadow(elevation, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        color = if (isSelected) lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f)
        else bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) Color.White else Color.Gray
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White else Color.Gray,
                    fontWeight = FontWeight.Medium
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewLanguageSelect() {
    MaterialTheme {
        LanguageSelectScreen(onLanguageSelected = {})
    }
}

@Preview(showBackground = true, name = "Language Select - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun LanguageSelectTabletPreview() {
    MaterialTheme {
        LanguageSelectScreen(onLanguageSelected = {})
    }
}
