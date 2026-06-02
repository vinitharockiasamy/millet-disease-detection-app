package com.pearlmillet.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.R
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.ui.theme.AppLayout

@Composable
fun WelcomeScreen(
    onStartClick: () -> Unit
) {
    val isCompact = LocalConfiguration.current.screenHeightDp < 640
    val dynamicVerticalPadding = if (isCompact) AppLayout.SpaceMedium else AppLayout.SpaceLarge

    Box(modifier = Modifier.fillMaxSize()) {

        Image(
            painter = painterResource(id = R.drawable.welcome_splash),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .blur(12.dp),
            contentScale = ContentScale.Crop
        )


        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.8f)
                        )
                    )
                )
        )


        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isCompact) {
                // Compact layouts (small height, landscape)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AppLayout.ContentMaxWidth)
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = dynamicVerticalPadding)
                        .systemBarsPadding()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    InstitutionalHeader()

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        AppBrandingSection()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoBadge(
                                icon = Icons.Default.Shield,
                                title = "Crop Defense",
                                modifier = Modifier.weight(1f)
                            )
                            InfoBadge(
                                icon = Icons.Default.TipsAndUpdates,
                                title = "AI Insights",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onStartClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = lerp(
                                    AppColors.DeepGreen.copy(alpha = 0.6f),
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GET STARTED",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 15.sp
                                )
                                Spacer(Modifier.width(12.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                // Standard height layout: uses weights for full-height distribution (no scrolling, gorgeous cinematic spacing)
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = AppLayout.ContentMaxWidth)
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = dynamicVerticalPadding)
                        .systemBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    InstitutionalHeader()

                    Spacer(modifier = Modifier.weight(1.1f))

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        AppBrandingSection()
                    }

                    Spacer(modifier = Modifier.weight(1.3f))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            InfoBadge(
                                icon = Icons.Default.Shield,
                                title = "Crop Defense",
                                modifier = Modifier.weight(1f)
                            )
                            InfoBadge(
                                icon = Icons.Default.TipsAndUpdates,
                                title = "AI Insights",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = onStartClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = lerp(
                                    AppColors.DeepGreen.copy(alpha = 0.6f),
                                    Color(0xFF4CAF50).copy(alpha = 0.2f),
                                    0.5f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GET STARTED",
                                    fontWeight = FontWeight.Black,
                                    color = Color.White,
                                    letterSpacing = 1.5.sp,
                                    fontSize = 18.sp
                                )
                                Spacer(Modifier.width(12.dp))
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(0.3f))
                }
            }
        }
    }
}

@Composable
private fun InstitutionalHeader() {
    val isCompact = LocalConfiguration.current.screenHeightDp < 640
    val topPadding = if (isCompact) 8.dp else 20.dp
    val imageSize = if (isCompact) 52.dp else 65.dp
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = topPadding)
    ) {
        Image(
            painter = painterResource(id = R.drawable.app_logo),
            contentDescription = "MILLET",
            modifier = Modifier.size(imageSize)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text = "AI-POWERED CROP DISEASE DETECTION",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF1B5E20),
                letterSpacing = 0.5.sp,
                fontSize = if (isCompact) 9.sp else 11.sp
            )
            Text(
                text = "Pearl Millet Disease Monitoring System",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                fontSize = if (isCompact) 9.sp else 11.sp
            )
        }
    }
}

@Composable
private fun AppBrandingSection() {
    val isCompact = LocalConfiguration.current.screenHeightDp < 640
    val isNarrow = LocalConfiguration.current.screenWidthDp < 360
    
    val nextGenSize = if (isNarrow) 14.sp else if (isCompact) 16.sp else 20.sp
    val nextGenTracking = if (isCompact || isNarrow) 3.sp else 4.sp
    val title1Size = if (isNarrow) 30.sp else if (isCompact) 36.sp else 46.sp
    val title1Height = if (isNarrow) 30.sp else if (isCompact) 36.sp else 46.sp
    val title2Size = if (isNarrow) 36.sp else if (isCompact) 44.sp else 54.sp
    val title2Height = if (isNarrow) 36.sp else if (isCompact) 44.sp else 54.sp
    val descSize = if (isNarrow) 12.sp else if (isCompact) 14.sp else 16.sp
    val descHeight = if (isNarrow) 18.sp else if (isCompact) 20.sp else 24.sp
    val spacing = if (isCompact || isNarrow) 12.dp else 20.dp

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "NEXT-GEN",
            fontSize = nextGenSize,
            fontWeight = FontWeight.Black,
            color = Color(0xFF5D4037),
            letterSpacing = nextGenTracking
        )
        Text(
            text = "Pearl Millet",
            fontSize = title1Size,
            fontWeight = FontWeight.Normal,
            color = BrandDeepGreen,
            lineHeight = title1Height
        )
        Text(
            text = "CARE",
            fontSize = title2Size,
            fontWeight = FontWeight.Black,
            color = BrandDeepGreen,
            letterSpacing = 2.sp,
            lineHeight = title2Height
        )

        Spacer(modifier = Modifier.height(spacing))

        Text(
            text = "Bridging scientific research and field expertise to protect your yield.",
            fontSize = descSize,
            color = Color.Black.copy(alpha = 0.7f),
            lineHeight = descHeight,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InfoBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, modifier: Modifier) {
    val isCompact = LocalConfiguration.current.screenHeightDp < 640
    val verticalPadding = if (isCompact) 10.dp else 16.dp
    val horizontalPadding = if (isCompact) 10.dp else 16.dp
    val iconSize = if (isCompact) 20.dp else 28.dp
    val fontSize = if (isCompact) 11.sp else 13.sp
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.35f), // Frosted glass effect
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = verticalPadding, horizontal = horizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = Color.White, modifier = Modifier.size(iconSize))
            Spacer(Modifier.height(if (isCompact) 4.dp else 8.dp))
            Text(text = title, color = Color.White, fontSize = fontSize, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, name = "Welcome Screen - Phone")
@Composable
fun WelcomeScreenPreview() {
    MaterialTheme {
        WelcomeScreen(onStartClick = {})
    }
}

@Preview(showBackground = true, name = "Welcome Screen - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun WelcomeScreenTabletPreview() {
    MaterialTheme {
        WelcomeScreen(onStartClick = {})
    }
}


