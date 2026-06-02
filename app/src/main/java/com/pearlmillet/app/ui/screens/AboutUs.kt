package com.pearlmillet.app.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.R
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.TextUnit
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.ui.theme.BrandDeepGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    language: String,
    onBack: () -> Unit
) {
    val isTamil = language == "ta"
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 120.dp else 180.dp

    // Standard Header Gradient
    val headerGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1B5E20), Color(0xFF4CAF50)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    PearlMilletScaffold(
        enableScrolling = true,
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

            /* 1. HEADER (Responsive Curved Box + Canvas design in both orientations) */
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
                    drawPath(curvePath, Brush.verticalGradient(listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))))
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                        .padding(top = 24.dp, bottom = 56.dp) // Increased bottom padding to match MenuScreen's natural height so the curve bends the same amount
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(42.dp)
                            .background(Color.White.copy(0.2f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White, modifier = Modifier.size(23.dp))
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isTamil) "எங்களை பற்றி" else "About Us",
                        fontSize = if (isLandscape) 22.sp else (if (isTamil) 26.sp else 36.sp),
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
            }


            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppLayout.DefaultHorizontalPadding)
            ) {
                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Logo Badge
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        tonalElevation = 2.dp,
                        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                        modifier = Modifier.size(100.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.app_logo),
                                contentDescription = "Pearl Millet Care Logo",
                                modifier = Modifier.size(140.dp),
                                contentScale = androidx.compose.ui.layout.ContentScale.Fit
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Project Title
                    Text(
                        text = if (isTamil) "முத்துச்சோள நோய் பராமரிப்பு" else "Pearl Millet Care",
                        fontSize = if (isTamil) 17.sp else 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.DeepGreen,
                        textAlign = TextAlign.Center,
                        lineHeight = if (isTamil) 26.sp else 30.sp,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Small elegant gold divider
                    Box(
                        modifier = Modifier
                            .width(45.dp)
                            .height(3.dp)
                            .background(AppColors.GoldAccent, CircleShape)
                    )

                }

                Spacer(modifier = Modifier.height(20.dp))


                ModernContentCard(
                    title = if (isTamil) "எங்கள் நோக்கம்" else "Our Mission",
                    icon = Icons.Default.Flag,
                    iconColor = AppColors.VividGreen,
                    isTamil = isTamil
                ) {
                    Text(
                        text = if (isTamil)
                            "விவசாயிகள் மற்றும் விவசாய ஆராய்ச்சியாளர்களுக்கு மேம்பட்ட இயந்திர கற்றல் கருவிகள் மூலம் துல்லியமான நோய் கண்டறிதலை வழங்கி, நிலையான விவசாய முறைகளை ஊக்குவித்தல்."
                        else
                            "To empower farmers and agricultural researchers with advanced machine learning tools for accurate, real-time crop disease detection, fostering sustainable farming practices and securing crop yields.",
                        fontSize = if (isTamil) 13.5.sp else 15.sp,
                        color = Color.Gray,
                        lineHeight = if (isTamil) 20.sp else 22.sp,
                        textAlign = TextAlign.Left
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                ModernContentCard(
                    title = if (isTamil) "தொழில்நுட்பம் & ஆராய்ச்சி" else "Technology & Research",
                    icon = Icons.Default.Science,
                    iconColor = AppColors.InfoBlue,
                    isTamil = isTamil
                ) {
                    Text(
                        text = if (isTamil)
                            "இச்சேவையானது கணினி பார்வை மாதிரிகள் மற்றும் சரிபார்க்கப்பட்ட தரவுத்தொகுப்புகள் மூலம் பயிர் நோய்களை உடனடியாகக் கண்டறிந்து மேலாண்மை முறைகளை பரிந்துரைக்கிறது."
                        else
                            "This platform leverages state-of-the-art computer vision models trained on curated agricultural datasets to identify crop diseases instantly and suggest evidence-based management practices.",
                        fontSize = if (isTamil) 13.5.sp else 15.sp,
                        color = Color.Gray,
                        lineHeight = if (isTamil) 20.sp else 22.sp,
                        textAlign = TextAlign.Left
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                Text(
                    text = if (isTamil) "செயலி அம்சங்கள்" else "App Features",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DeepGreen,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FeatureTile(
                        icon = Icons.Default.CameraAlt,
                        title = if (isTamil) "நோய்களை கண்டறிதல்" else "Disease Identification",
                        fontSize = if (isTamil) 13.sp else 16.sp,
                        color = AppColors.WarningOrange
                    )
                    FeatureTile(
                        icon = Icons.AutoMirrored.Filled.LibraryBooks,
                        title = if (isTamil) "நோய் மேலாண்மைக்கான முறைகள்" else "Disease Management Practices",
                        fontSize = if (isTamil) 13.sp else 16.sp,
                        color = AppColors.InfoBlue
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))


                Text(
                    text = if (isTamil) "திட்ட வழிகாட்டுதல்" else "Project Guidance",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(AppLayout.CardCornerRadius),
                    color = Color(0xFFF5F5F5),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isTamil)
                                "வேளாண் ஆராய்ச்சி ஒத்துழைப்பு"
                            else
                                "Agricultural Research Collaboration",
                            fontSize = if (isTamil) 20.sp else 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(3.dp)
                                .background(AppColors.GoldAccent, CircleShape)
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = if (isTamil)
                                "பயிர் நோய் கண்டறிதல் மற்றும் பயிர் ஆரோக்கிய மேலாண்மை தொடர்பான தொழில்நுட்ப மற்றும் ஆராய்ச்சி வழிகாட்டுதல்"
                            else
                                "Technical and research guidance inspired by agricultural disease monitoring and crop health management practices.",
                            fontSize = if (isTamil) 14.sp else 16.sp,
                            color = AppColors.DeepGreen,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            lineHeight = if (isTamil) 22.sp else 24.sp
                        )
                    }

                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = if (isTamil) "உருவாக்கியவர்" else "Developed By",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    shape = RoundedCornerShape(AppLayout.CardCornerRadius),
                    color = Color(0xFFF5F5F5),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = if (isTamil)
                                "வினித் அரோக்கியசாமி"
                            else
                                "Vinith Arockiasamy",
                            fontSize = if (isTamil) 16.sp else 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextDark,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(6.dp))

                        Text(
                            text = if (isTamil)
                                "AI மற்றும் மொபைல் பயன்பாட்டு உருவாக்கம்"
                            else
                                "AI & Mobile Application Development",
                            fontSize = if (isTamil) 12.sp else 14.sp,
                            color = Color.Gray,
                            lineHeight = if (isTamil) 20.sp else 22.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
}

@Composable
fun ModernContentCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    isTamil: Boolean,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(AppLayout.CardCornerRadius),
        color = Color.White,
        shadowElevation = AppLayout.CardElevation,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = title,
                    fontSize = if (isTamil) 15.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
            }
            Spacer(Modifier.height(16.dp))
            content()
        }
    }
}

@Composable
fun FeatureTile(
    icon: ImageVector,
    title: String,
    color: Color,
    fontSize: TextUnit
) {
    Surface(
        shape = RoundedCornerShape(AppLayout.CardCornerRadius),
        color = Color.White,
        shadowElevation = AppLayout.CardElevation,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = fontSize,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextDark
            )
        }
    }
}


@Preview(showBackground = true, heightDp = 1550)
@Composable
fun AboutUsPreview() {
    MaterialTheme {
        AboutUsScreen("en", onBack = {})
    }
}

@Preview(showBackground = true, heightDp = 1520)
@Composable
fun AboutUstaPreview() {
    MaterialTheme {
        AboutUsScreen("ta", onBack = {})
    }
}

@Preview(showBackground = true, name = "English - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun AboutUsTabletPreview() {
    MaterialTheme {
        AboutUsScreen("en", onBack = {})
    }
}
