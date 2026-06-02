package com.pearlmillet.app.ui.screens

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.R
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.data.StringsRepository
import kotlinx.coroutines.launch

data class DiseaseStageImage(
    val imageRes: Int,
    val captionEn: String,
    val captionTa: String
)

private fun getDiseaseImages(diseaseId: String): List<DiseaseStageImage> {
    return when (diseaseId) {
        "blast" -> listOf(
            DiseaseStageImage(R.drawable.b1, "Diamond shaped greyish lesion on leaves", "வைர வடிவிலான சாம்பல் நிறப் புள்ளிகள்"),
            DiseaseStageImage(R.drawable.b2, "Necrosis of sheath tissue", "இலை உறை திசுக்கள் கருகி காணப்படுதல்")
        )
        "downy_mildew" -> listOf(
            DiseaseStageImage(R.drawable.d1, "White downy growth on under surface", "இலையின் அடிப்பகுதியில் வெண்மை நிறப் பூசண வளர்ச்சி"),
            DiseaseStageImage(R.drawable.d2, "Downy growth on lower surface and chlorotic streaks on upper surface", "அடிப்பகுதியில் வெண்மை பூஞ்சை மற்றும் மேல்பகுதியில் வெளிறிய கோடுகள்"),
            DiseaseStageImage(R.drawable.d3, "Partial green ear", "பாதியான  பசுங்கதிர்"),
            DiseaseStageImage(R.drawable.d4, "Complete green ear formation", "முழுமையான பசுங்கதிர்")
        )
        "ergot" -> listOf(
            DiseaseStageImage(R.drawable.e1, "Honeydew secretion from florets", "பூக்களில் இருந்து தேன் போன்ற திரவம் ஒழுகுதல்"),
            DiseaseStageImage(R.drawable.e2, "Sticky liquid on inflorescence", "கதிரில் பிசுபிசுப்பான திரவம்"),
            DiseaseStageImage(R.drawable.e3, "Dark brown sclerotia formation", "அடர் பழுப்பு நிற ஸ்கிளிரோசியா உருவாக்கம்")
        )
        "rust" -> listOf(
            DiseaseStageImage(R.drawable.r1, "Reddish-brown pustules on leaves", "இலைகளில் செம்பழுப்பு நிற புள்ளிகள் ")
        )
        "smut" -> listOf(
            DiseaseStageImage(R.drawable.s1, "Green Smut Sori", "பச்சை நிறமாக மாறிய மணிகள்"),
            DiseaseStageImage(R.drawable.s2, "Black Smut Sori", "கரும்பழுப்பு  நிறமாக மாறிய மணிகள்"),
            DiseaseStageImage(R.drawable.s3, "Smut Sori", "கரிப்பூட்டை ")
        )
        else -> emptyList()
    }
}

private fun getDiseaseMainImage(diseaseId: String): Int {
    return when (diseaseId) {
        "blast" -> R.drawable.blast_img
        "downy_mildew" -> R.drawable.downymildew_img
        "ergot" -> R.drawable.ergot_img
        "rust" -> R.drawable.rust_img
        "smut" -> R.drawable.smut_img
        else -> R.drawable.blast_img
    }
}

private fun getDiseaseColor(diseaseId: String): Color {
    return when (diseaseId) {
        "blast" -> AppColors.ErrorRed
        "downy_mildew" -> AppColors.VividGreen
        "ergot" -> AppColors.SeverePurple
        "rust" -> AppColors.WarningOrange
        "smut" -> AppColors.InfoBlue
        else -> AppColors.ErrorRed
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseDetailScreen(
    diseaseId: String,
    language: String,
    onBack: () -> Unit
) {
    val isTamil = language == "ta"
    val strings = StringsRepository.getStrings(language)
    val disease = com.pearlmillet.app.data.DiseaseMetadata.getDiseaseData(diseaseId, language)
    
    val diseaseColor = getDiseaseColor(diseaseId)
    val mainImage = getDiseaseMainImage(diseaseId)
    val galleryImages = getDiseaseImages(diseaseId).ifEmpty { 
        listOf(DiseaseStageImage(mainImage, disease.name, disease.name)) 
    }

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val imageCardHeight = if (isLandscape) 160.dp else 260.dp

    var initialFullImageIndex by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(0) }
    var showFullGallery by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    val pageBackground = Color(0xFFF8F5F4)

    // Full Screen Image Dialog
    if (showFullGallery) {
        val galleryToShow = galleryImages
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showFullGallery = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            val fullPagerState = androidx.compose.foundation.pager.rememberPagerState(
                initialPage = initialFullImageIndex,
                pageCount = { galleryImages.size }
            )

            Box(modifier = Modifier.fillMaxSize().background(Color.Black).windowInsetsPadding(WindowInsets.systemBars)) {
                androidx.compose.foundation.pager.HorizontalPager(
                    state = fullPagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val stageImage = galleryToShow[page]
                    Box(modifier = Modifier.fillMaxSize()) {
                        Image(
                            painter = painterResource(id = stageImage.imageRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(vertical = 24.dp, horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isTamil) stageImage.captionTa else stageImage.captionEn,
                                fontSize = if (isTamil) 14.sp else 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                lineHeight = if (isTamil) 20.sp else 22.sp
                            )
                        }
                    }
                }
                
                val coroutineScope = rememberCoroutineScope()
                if (galleryToShow.size > 1) {
                    val infiniteTransition = androidx.compose.animation.core.rememberInfiniteTransition(label = "arrowAlpha")
                    val arrowAlpha by infiniteTransition.animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable<Float>(
                            animation = tween<Float>(800, easing = FastOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "alpha"
                    )

                    if (fullPagerState.currentPage > 0) {
                        IconButton(
                            onClick = { coroutineScope.launch { fullPagerState.animateScrollToPage(fullPagerState.currentPage - 1) } },
                            modifier = Modifier.align(Alignment.CenterStart).padding(16.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowLeft, null, tint = Color.White.copy(alpha = arrowAlpha), modifier = Modifier.size(32.dp))
                        }
                    }

                    if (fullPagerState.currentPage < galleryToShow.size - 1) {
                        IconButton(
                            onClick = { coroutineScope.launch { fullPagerState.animateScrollToPage(fullPagerState.currentPage + 1) } },
                            modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp).background(Color.Black.copy(alpha = 0.3f), CircleShape)
                        ) {
                            Icon(Icons.Default.KeyboardArrowRight, null, tint = Color.White.copy(alpha = arrowAlpha), modifier = Modifier.size(32.dp))
                        }
                    }
                    
                    Row(
                        Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(galleryToShow.size) { iteration ->
                            val color = if (fullPagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.4f)
                            Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(6.dp))
                        }
                    }
                }
                
                IconButton(
                    onClick = { showFullGallery = false },
                    modifier = Modifier.padding(16.dp).align(Alignment.TopEnd).background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = strings.back, tint = Color.White)
                }
            }
        }
    }

    PearlMilletScaffold(containerColor = pageBackground) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppLayout.DefaultHorizontalPadding)
                        .height(imageCardHeight)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.Black)
                ) {
                    val pagerState = rememberPagerState(pageCount = { galleryImages.size })
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        Image(
                            painter = painterResource(id = galleryImages[page].imageRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clickable {
                                initialFullImageIndex = page
                                showFullGallery = true
                            }
                        )
                    }

                    if (galleryImages.size > 1) {
                        Row(
                            Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            repeat(galleryImages.size) { iteration ->
                                val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.4f)
                                Box(modifier = Modifier.padding(4.dp).clip(CircleShape).background(color).size(6.dp))
                            }
                        }
                    }
                    
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.align(Alignment.TopStart).padding(12.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back, tint = Color.White)
                    }

                    IconButton(
                        onClick = {
                            initialFullImageIndex = pagerState.currentPage
                            showFullGallery = true
                        },
                        modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Fullscreen, contentDescription = strings.viewDetails, tint = Color.White)
                    }
                }

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 4.dp)) {
                    Text(
                        text = disease.name,
                        fontSize = if (isTamil) 24.sp else 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextDark,
                        lineHeight = if (isTamil) 30.sp else 34.sp
                    )
                    Text(
                        text = disease.botanicalName,
                        fontSize = if (isTamil) 14.sp else 16.sp,
                        fontStyle = FontStyle.Italic,
                        color = if (diseaseColor == AppColors.VividGreen) AppColors.DeepGreen else diseaseColor,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    DetailedSectionCard(
                        title = strings.symptoms,
                        icon = Icons.Default.Eco,
                        color = diseaseColor,
                        isTamil = isTamil
                    ) {
                        disease.symptoms.forEach { symptom ->
                            IconListItem(Icons.Default.FiberManualRecord, symptom, AppColors.TextDark, isTamil)
                        }
                    }

                    DetailedSectionCard(
                        title = strings.favourableConditions,
                        icon = Icons.Default.WbSunny,
                        color = AppColors.WarningOrange,
                        isTamil = isTamil
                    ) {
                        Text(
                            text = disease.favourableConditions,
                            fontSize = if (isTamil) 14.sp else 15.sp,
                            color = AppColors.TextDark.copy(alpha = 0.8f),
                            lineHeight = if (isTamil) 20.sp else 22.sp,
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        colors = CardDefaults.cardColors(containerColor = diseaseColor.copy(alpha = 0.05f)),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, diseaseColor.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(diseaseColor.copy(alpha = 0.9f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.MedicalServices, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(strings.recommendation, fontSize = if (isTamil) 16.sp else 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextDark)
                                    Text(strings.chemicalControl, fontSize = if (isTamil) 11.sp else 12.sp, color = diseaseColor, fontWeight = FontWeight.SemiBold)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            disease.treatment.forEach { treatmentStep ->
                                Text(treatmentStep, fontWeight = FontWeight.Bold, color = AppColors.TextDark, fontSize = if (isTamil) 15.sp else 16.sp, lineHeight = if (isTamil) 24.sp else 26.sp, modifier = Modifier.padding(bottom = 8.dp))
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), thickness = 1.dp, color = diseaseColor.copy(alpha = 0.2f))

                            Row(verticalAlignment = Alignment.Top) {
                                Icon(Icons.Default.WaterDrop, null, tint = diseaseColor, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(strings.applicationMethod, fontSize = if (isTamil) 11.sp else 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                    Text(disease.application, fontSize = if (isTamil) 13.sp else 14.sp, color = AppColors.TextDark.copy(alpha = 0.8f), lineHeight = if (isTamil) 18.sp else 20.sp)
                                }
                            }

                            if (disease.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Info, null, tint = diseaseColor, modifier = Modifier.size(20.dp).padding(top = 2.dp))
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(strings.note, fontSize = if (isTamil) 11.sp else 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                                        Text(disease.notes, fontSize = if (isTamil) 13.sp else 14.sp, fontStyle = FontStyle.Italic, color = AppColors.TextDark.copy(alpha = 0.7f), lineHeight = if (isTamil) 18.sp else 20.sp)
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun DetailedSectionCard(
    title: String,
    icon: ImageVector,
    color: Color,
    isTamil: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = CircleShape, color = color.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontSize = if (isTamil) 16.sp else 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextDark)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))
            content()
        }
    }
}

@Composable
fun IconListItem(
    icon: ImageVector,
    text: String,
    color: Color,
    isTamil: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(icon, null, tint = color.copy(alpha = 0.6f), modifier = Modifier.size(12.dp).padding(top = 6.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = if (isTamil) 14.sp else 15.sp,
            color = AppColors.TextDark.copy(alpha = 0.8f),
            lineHeight = if (isTamil) 20.sp else 22.sp
        )
    }
}

@Preview(showBackground = true, name = "English - Phone")
@Composable
fun DiseaseDetailScreenEnglishPreview() {
    MaterialTheme {
        DiseaseDetailScreen(
            diseaseId = "blast",
            language = "en",
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Tamil - Phone")
@Composable
fun DiseaseDetailScreenTamilPreview() {
    MaterialTheme {
        DiseaseDetailScreen(
            diseaseId = "blast",
            language = "ta",
            onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "English - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun DiseaseDetailScreenTabletPreview() {
    MaterialTheme {
        DiseaseDetailScreen(
            diseaseId = "blast",
            language = "en",
            onBack = {}
        )
    }
}