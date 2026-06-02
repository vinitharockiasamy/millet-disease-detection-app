package com.pearlmillet.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.pearlmillet.app.R
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.ui.theme.AppLayout


private data class FertilizerRecommendation(
    val diseaseNameEn: String,
    val diseaseNameTa: String,
    val chemical: String,
    val chemicalTa: String,
    val dosage: String,
    val dosageTa: String,
    val application: String,
    val applicationTa: String,
    val color: Color
)

private val fertilizerData = listOf(
    FertilizerRecommendation(
        diseaseNameEn = "Blast",
        diseaseNameTa = "குலை நோய்",
        chemical = "Carbendazim 50% WP",
        chemicalTa = "கார்பென்டாசிம் 50% WP",
        dosage = "250 g/ha",
        dosageTa = "250 கிராம்/எக்டர்",
        application = "Spray thoroughly on affected plants during early morning or evening.",
        applicationTa = "பாதிக்கப்பட்ட தாவரங்களில் நன்கு தெளிக்கவும். அதிகாலை அல்லது மாலை நேரத்தில் பயன்படுத்தவும்.",
        color = AppColors.ErrorRed
    ),
    FertilizerRecommendation(
        diseaseNameEn = "Downy Mildew",
        diseaseNameTa = "அடிச்சாம்பல் நோய்",
        chemical = "Metalaxyl + Mancozeb",
        chemicalTa = "மெட்டாலாக்ஸில் + மேன்கோசெப்",
        dosage = "500 g/ha (Mix)",
        dosageTa = "500 கிராம்/எக்டர்",
        application = "Spray on leaves early in disease development to stop spread.",
        applicationTa = "நோய் பரவலைத் தடுக்க ஆரம்பத்திலேயே இலைகளில் தெளிக்கவும்.",
        color = AppColors.VividGreen
    ),
    FertilizerRecommendation(
        diseaseNameEn = "Ergot",
        diseaseNameTa = "தேன் ஒழுகல் நோய்",
        chemical = "Carbendazim / Mancozeb",
        chemicalTa = "கார்பென்டாசிம் / மேன்கோசெப்",
        dosage = "500g - 1000g / ha",
        dosageTa = "500கி - 1000கி / எக்டர்",
        application = "Spray when 5-10% flowers open and again at 50% flowering.",
        applicationTa = "5-10% பூக்கும் போதும் மற்றும் 50% பூக்கும் போதும் தெளிக்கவும்.",
        color = AppColors.SeverePurple
    ),
    FertilizerRecommendation(
        diseaseNameEn = "Rust",
        diseaseNameTa = "துரு நோய்",
        chemical = "Wettable Sulphur",
        chemicalTa = "நனையும் கந்தகம்",
        dosage = "2500 g/ha",
        dosageTa = "2.5 கிலோ/எக்டர்",
        application = "Spray at first sign. Repeat after 10 days if needed.",
        applicationTa = "நோய் அறிகுறி தென்பட்டவுடன் தெளிக்கவும். தேவைப்பட்டால் 10 நாட்கள் கழித்து மீண்டும் செய்யவும்.",
        color = AppColors.WarningOrange
    ),
    FertilizerRecommendation(
        diseaseNameEn = "Smut",
        diseaseNameTa = "கரிப்பூட்டை நோய்",
        chemical = "Zineb",
        chemicalTa = "சினெப்",
        dosage = "1000 g/ha",
        dosageTa = "1000 கிராம்/எக்டர்",
        application = "Spray on panicle at boot leaf stage for best results.",
        applicationTa = "கதிர் இலை பருவத்தில் பூங்கொத்தின் மீது தெளிக்கவும்.",
        color = AppColors.InfoBlue
    )
)

@Composable
fun FertilizerGuideScreen(
    language: String,
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    val isTamil = language == "ta"
    val scrollState = rememberScrollState()
    val pageBackground = Color(0xFFF8F5F4)
    val headerGradient = Brush.linearGradient(
        colors = listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f)),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 115.dp else 170.dp
    val cornerRadius = if (isLandscape) 30.dp else 65.dp
    val titleFontSize = if (isLandscape) (if (isTamil) 20.sp else 24.sp) else (if (isTamil) 24.sp else 30.sp)
    val bottomPadding = if (isLandscape) 12.dp else 16.dp

    PearlMilletScaffold {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
                    .wrapContentHeight()
                    .background(AppColors.SurfaceBg)
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 0.dp, shape = RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                        .clip(RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                        .background(headerGradient)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                            .padding(top = 16.dp, bottom = 32.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                            }
                            IconButton(
                                onClick = onMenuClick,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(Icons.Default.Menu, "Menu", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.padding(start = 48.dp)
                        ) {
                            val isNarrow = LocalConfiguration.current.screenWidthDp < 400
                            Text(
                                text = if (isTamil) "நோய் மேலாண்மை" else "Disease Management",
                                fontSize = if (isNarrow) {
                                    if (isTamil) 20.sp else 24.sp
                                } else {
                                    if (isTamil) 24.sp else 30.sp
                                },
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = if (isTamil) 32.sp else 40.sp
                            )
                        }
                    }
                }
            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))


            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = AppLayout.DefaultHorizontalPadding)) {
                Surface(
                    color = Color(0xFFFFF8E1),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD54F)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFA000), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if(isTamil) "பாதுகாப்பு கவசங்களை அணியவும்." else "Wear protective gear while handling chemicals.",
                            fontSize = 12.sp,
                            color = Color(0xFF5D4037),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(
                modifier = Modifier.padding(start = AppLayout.DefaultHorizontalPadding, end = AppLayout.DefaultHorizontalPadding, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fertilizerData.forEach { data ->
                    CompactTreatmentCard(data = data, isTamil = isTamil)
                }
            }
        }
    }
}
}

@Composable
private fun CompactTreatmentCard(
    data: FertilizerRecommendation,
    isTamil: Boolean
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // ==========================================
            // HEADER STRIP (Just Title + Color Bar)
            // ==========================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 52.dp)
                    .background(data.color)
            ) {
                // Title Only
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTamil) data.diseaseNameTa else data.diseaseNameEn,
                        fontSize = if (isTamil) 16.sp else 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }

            Column(modifier = Modifier.padding(16.dp)) {

                CompactInfoRow(
                    icon = Icons.Default.Science,
                    label = if (isTamil) "மருந்து" else "Chemical",
                    value = if (isTamil) data.chemicalTa else data.chemical,
                    iconTint = data.color,
                    isTamil = isTamil
                )
                Spacer(modifier = Modifier.height(12.dp))

                CompactInfoRow(
                    icon = Icons.Default.WaterDrop,
                    label = if (isTamil) "அளவு" else "Dosage",
                    value = if (isTamil) data.dosageTa else data.dosage,
                    iconTint = data.color,
                    isTamil = isTamil
                )
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if(isTamil) "பயன்பாட்டு முறை:" else "Application:",
                    fontSize = if (isTamil) 11.sp else 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isTamil) data.applicationTa else data.application,
                    fontSize = if (isTamil) 13.sp else 14.sp,
                    color = AppColors.TextDark,
                    lineHeight = if (isTamil) 18.sp else 20.sp
                )
            }
        }
    }
}

@Composable
private fun CompactInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconTint: Color,
    isTamil: Boolean
) {
    Row(verticalAlignment = Alignment.Top) {
        Surface(
            shape = CircleShape,
            color = iconTint.copy(alpha = 0.1f),
            modifier = Modifier.size(32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = if (isTamil) 10.sp else 11.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Text(text = value, fontSize = if (isTamil) 13.sp else 14.sp, color = AppColors.TextDark, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true, heightDp = 1710)
@Composable
fun CompactFertilizerPreview() {
    MaterialTheme {
        FertilizerGuideScreen(language = "en", onBack = {}, onMenuClick = {})
    }
}

@Preview(showBackground = true, heightDp = 1850)
@Composable
fun CompactFertilizertaPreview() {
    MaterialTheme {
        FertilizerGuideScreen(language = "ta", onBack = {}, onMenuClick = {})
    }
}

@Preview(showBackground = true, name = "English - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", heightDp = 1000)
@Composable
fun FertilizerGuideTabletPreview() {
    MaterialTheme {
        FertilizerGuideScreen(language = "en", onBack = {}, onMenuClick = {})
    }
}