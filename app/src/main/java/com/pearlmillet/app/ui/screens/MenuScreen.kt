package com.pearlmillet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppTypography
import com.pearlmillet.app.ui.theme.AppLayout
import androidx.compose.foundation.Canvas
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import androidx.compose.ui.graphics.Path

@Composable
fun MenuScreen(
    userName: String = "Ramu",
    userRole: String = "farmer",
    language: String,
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    onChangeLanguage: () -> Unit,
    onAboutUs: () -> Unit
) {
    val isTamil = language == "ta"

    val headerGradient = Brush.verticalGradient(
        colors = listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))
    )

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 120.dp else 180.dp
    val textBottomPadding = if (isLandscape) 12.dp else 16.dp

    PearlMilletScaffold(
        enableScrolling = true, // Enable scrolling for the whole page
        containerColor = AppColors.SurfaceBg
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
            ) {
            

            /* ---------------- HEADER ---------------- */
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    val curveEndY = size.height * 0.95f
                    val curveControlY = size.height * 1.02f
                    val curveRightY = size.height * 0.85f
                    val curvePath = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, curveRightY)
                        quadraticTo(
                            size.width * 0.5f,
                            curveControlY,
                            0f,
                            curveEndY
                        )
                        close()
                    }
                    drawPath(curvePath, headerGradient)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                        .padding(top = 16.dp, bottom = 32.dp)
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.25f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Header Text: "Hi, Name" + Role Subtitle
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {

                            Text(
                                text = if (isTamil) "வணக்கம்," else "Hi,",
                                fontSize = if (isLandscape) 16.sp else (if (isTamil) 19.sp else 25.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = userName,
                                fontSize = if (isLandscape) 20.sp else (if (isTamil) 24.sp else 32.sp),
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                lineHeight = if (isLandscape) 26.sp else 40.sp,
                                modifier = Modifier.alignByBaseline()
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        val displayRole = when(userRole.lowercase()) {
                            "farmer" -> if (isTamil) "விவசாயி" else "FARMER"
                            "student" -> if (isTamil) "மாணவர்" else "STUDENT"
                            "professional" -> if (isTamil) "நிபுணர்" else "PROFESSIONAL"
                            else -> userRole.uppercase()
                        }
                        
                        Text(
                            text = displayRole,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFA5D6A7),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppLayout.DefaultHorizontalPadding)
            ) {

                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

                // Menu Items
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    MenuCard(
                        title = if (isTamil) "சுயவிவரம் திருத்து" else "Edit Profile",
                        subtitle = if (isTamil) "உங்கள் பெயர் மற்றும் பங்கு" else "Update name & role",
                        icon = Icons.Default.Edit,
                        color = AppColors.InfoBlue,
                        onClick = onEditProfile,
                        isTamil = isTamil
                    )

                    MenuCard(
                        title = if (isTamil) "மொழி மாற்றம்" else "Change Language",
                        subtitle = if (isTamil) "தமிழ், English" else "Tamil, English",
                        icon = Icons.Default.Language,
                        color = AppColors.VividGreen,
                        onClick = onChangeLanguage,
                        isTamil = isTamil,
                        trailingContent = {
                            Surface(
                                shape = CircleShape,
                                color = AppColors.VividGreen.copy(alpha = 0.1f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.VividGreen)
                            ) {
                                Text(
                                    text = if (language == "ta") "தமிழ்" else "Eng",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AppColors.DeepGreen
                                )
                            }
                        }
                    )

                    MenuCard(
                        title = if (isTamil) "எங்களை பற்றி" else "About Us",
                        subtitle = if (isTamil) "திட்டம் மற்றும் செயலி விவரம்" else "Project & App Info",
                        icon = Icons.Default.Info,
                        color = AppColors.WarningOrange,
                        onClick = onAboutUs,
                        isTamil = isTamil
                    )
                }
                
                Spacer(modifier = Modifier.height(30.dp))
                // Footer (Optional here since using Scaffold content, but consistent spacing helps)
            }
        }
    }
}
}

@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    isTamil: Boolean,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(AppLayout.CardCornerRadius),
        color = Color.White,
        shadowElevation = AppLayout.CardElevation
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Box
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Text Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = AppTypography.bodySize(isTamil),
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            // Trailing Content
            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.LightGray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MenuScreen(
        language = "en",
        onBack = {},
        onEditProfile = {},
        onChangeLanguage = {},
        onAboutUs = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MenuScreenTaPreview() {
    MenuScreen(
        language = "ta",
        onBack = {},
        onEditProfile = {},
        onChangeLanguage = {},
        onAboutUs = {}
    )
}

@Preview(showBackground = true, name = "English - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun MenuScreenTabletPreview() {
    MaterialTheme {
        MenuScreen(
            language = "en",
            onBack = {},
            onEditProfile = {},
            onChangeLanguage = {},
            onAboutUs = {}
        )
    }
}
