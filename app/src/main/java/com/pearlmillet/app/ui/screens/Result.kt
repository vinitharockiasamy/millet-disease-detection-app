package com.pearlmillet.app.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.data.StringsRepository
import com.pearlmillet.app.utils.getDiseaseInfo
import com.pearlmillet.app.utils.getDiseaseId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.pearlmillet.app.utils.ScanSessionManager
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.theme.AppColors

// ---------------------------------------------------------------------------
// Result Screen – shown after a live camera scan
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    result: String,
    confidence: Float,
    imageCount: Int = 1,
    isSoft: Boolean = false,
    imageUri: String,
    language: String,
    navController: NavController,
    onNewScan: () -> Unit,
    onBackClick: () -> Unit,
) {
    val isTamil = language == "ta"
    val strings = StringsRepository.getStrings(language)
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showFullImage by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    // Load bitmap from ScanSessionManager (in-memory path for live scans)
    LaunchedEffect(imageUri) {
        if (imageUri == "in_memory") {
            bitmap = ScanSessionManager.getCurrentImage()
        }
    }

    val diseaseId = getDiseaseId(result)
    val diseaseInfo = getDiseaseInfo(result, language)
    val isHealthy = result.equals("Healthy", ignoreCase = true)
    val isNotLeaf = result.equals("Not Millet Leaf", ignoreCase = true)
    val isDisease = !isHealthy && !isNotLeaf

    val stateColor = diseaseStateColor(result, isHealthy, isNotLeaf)
    val pageBackground = Color(0xFFF8F5F4)

    // Full screen image overlay
    if (showFullImage && bitmap != null) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black).windowInsetsPadding(WindowInsets.systemBars)) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
                IconButton(
                    onClick = { showFullImage = false },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopEnd)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
        }
    }

    PearlMilletScaffold(
        containerColor = pageBackground
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
                    .wrapContentHeight()
            ) {

                // ── Hero image card ──────────────────────────────────────
                ResultImageCard(
                    bitmap = bitmap,
                    onBackClick = onBackClick,
                    onFullScreenClick = { showFullImage = true }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 4.dp)
                ) {
                    // Disease title
                    Text(
                        text = diseaseInfo.name,
                        fontSize = if (isTamil) 24.sp else 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextDark,
                        lineHeight = if (isTamil) 30.sp else 34.sp
                    )
                    if (diseaseInfo.botanicalName.isNotEmpty()) {
                        Text(
                            text = diseaseInfo.botanicalName,
                            fontSize = if (isTamil) 14.sp else 16.sp,
                            color = if (isDisease) {
                                if (stateColor == AppColors.VividGreen) AppColors.DeepGreen else stateColor
                            } else Color.Gray,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    }

                    if (imageCount > 1 && !isSoft) {
                        Surface(
                            color = AppColors.DeepGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(bottom = 12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.VerifiedUser, 
                                    contentDescription = null, 
                                    tint = AppColors.DeepGreen, 
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isTamil) "$imageCount படங்களைப் பயன்படுத்தி உறுதிப்படுத்தப்பட்டது" else "Diagnosis confirmed using $imageCount images",
                                    color = AppColors.DeepGreen,
                                    fontSize = if (isTamil) 10.5.sp else 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Status card
                    ResultStatusCard(
                        isDisease = isDisease,
                        isHealthy = isHealthy,
                        isTamil = isTamil,
                        stateColor = stateColor,
                        diseaseId = diseaseId,
                        strings_recommendation = if (isTamil) "பரிந்துரை" else "Recommendation",
                        strings_viewTreatment = if (isTamil) "சிகிச்சையை காண்க" else "View Treatment",
                        onViewTreatment = {
                            if (diseaseId != null) navController.navigate("disease_detail/$diseaseId")
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description card
                    ResultDescriptionCard(
                        description = diseaseInfo.description,
                        isDisease = isDisease,
                        isTamil = isTamil,
                        stateColor = stateColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // New Scan button
                    Button(
                        onClick = onNewScan,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(50),
                                spotColor = AppColors.DeepGreen.copy(alpha = 0.5f)
                            ),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepGreen)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = strings.newScan,
                                fontSize = if (isTamil) 15.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared composables — reused by ResultScreen & HistoryResultScreen
// ---------------------------------------------------------------------------

/** Hero image card with back button and fullscreen toggle. */
@Composable
fun ResultImageCard(
    bitmap: Bitmap?,
    topContent: @Composable BoxScope.() -> Unit = {},
    onBackClick: () -> Unit,
    onFullScreenClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val imageHeight = if (isLandscape) 140.dp else 260.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(AppLayout.DefaultHorizontalPadding)
            .height(imageHeight)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.Black)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Scanned Leaf",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(modifier = Modifier.fillMaxSize().background(Color(0xFF1A1A1A)))
        }

        // Vignette
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Black.copy(0.3f), Color.Transparent, Color.Black.copy(0.3f))
                )
            )
        )

        // Slot for caller-specific top-bar content (back + optional date pill)
        Box(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            topContent()
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .size(40.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
            }
        }

        // Fullscreen toggle
        IconButton(
            onClick = onFullScreenClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
        ) {
            Icon(Icons.Default.Fullscreen, null, tint = Color.White)
        }
    }
}

/** Coloured status card: Disease action / Healthy / Not-millet */
@Composable
fun ResultStatusCard(
    isDisease: Boolean,
    isHealthy: Boolean,
    isTamil: Boolean,
    stateColor: Color,
    diseaseId: String?,
    strings_recommendation: String,
    strings_viewTreatment: String,
    onViewTreatment: () -> Unit
) {
    when {
        isDisease -> Card(
            onClick = onViewTreatment,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = stateColor.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, stateColor.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(stateColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.MedicalServices, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings_recommendation,
                        color = stateColor, fontSize = 13.sp, fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings_viewTreatment,
                        color = AppColors.TextDark,
                        fontSize = if (isTamil) 16.sp else 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = stateColor)
            }
        }

        isHealthy -> Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            border = BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.3f))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFF2E7D32)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isTamil) "பயிர் ஆரோக்கியமாக உள்ளது" else "Crop is Healthy",
                        color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold,
                        fontSize = if (isTamil) 16.sp else 18.sp
                    )
                    Text(
                        text = if (isTamil) "நடவடிக்கை தேவையில்லை" else "No action required",
                        color = Color(0xFF2E7D32), fontSize = if (isTamil) 13.sp else 14.sp
                    )
                }
            }
        }

        else -> Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
            border = BorderStroke(1.dp, AppColors.WarningOrange.copy(alpha = 0.3f))
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(AppColors.WarningOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isTamil) "அடையாளம் தெரியவில்லை" else "Unrecognised Image",
                        color = Color(0xFFE65100), fontWeight = FontWeight.Bold,
                        fontSize = if (isTamil) 16.sp else 18.sp
                    )
                    Text(
                        text = if (isTamil) "இலை கண்டறியப்படவில்லை" else "Leaf not detected",
                        color = AppColors.WarningOrange, fontSize = if (isTamil) 13.sp else 14.sp
                    )
                }
            }
        }
    }
}

/** White card with description text below the status card. */
@Composable
fun ResultDescriptionCard(
    description: String,
    isDisease: Boolean,
    isTamil: Boolean,
    stateColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isDisease) stateColor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f),
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Description, null,
                            tint = if (isDisease) stateColor else Color.Gray,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = if (isTamil) "விவரங்கள்" else "Description",
                    fontSize = if (isTamil) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                thickness = 1.dp,
                color = Color.LightGray.copy(alpha = 0.2f)
            )
            Text(
                text = description,
                fontSize = if (isTamil) 14.sp else 15.sp,
                color = AppColors.TextDark.copy(alpha = 0.8f),
                lineHeight = if (isTamil) 22.sp else 24.sp
            )
        }
    }
}

/**
 * Maps a disease result string to its display color.
 * Single source of truth — used by both ResultScreen and HistoryResultScreen.
 */
fun diseaseStateColor(result: String, isHealthy: Boolean, isNotLeaf: Boolean): Color = when {
    isHealthy -> AppColors.VividGreen
    isNotLeaf -> AppColors.WarningOrange
    result.contains("Blast", ignoreCase = true)      -> AppColors.ErrorRed
    result.contains("Downy", ignoreCase = true)      -> AppColors.VividGreen
    result.contains("Ergot", ignoreCase = true)      -> AppColors.SeverePurple
    result.contains("Rust",  ignoreCase = true)      -> AppColors.WarningOrange
    result.contains("Smut",  ignoreCase = true)      -> AppColors.InfoBlue
    else                                              -> AppColors.ErrorRed
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, name = "Result - Disease (Blast)")
@Composable
fun ResultScreenDiseasePreview() {
    ResultScreen(
        result = "Blast",
        confidence = 0.95f,
        imageCount = 2,
        isSoft = false,
        imageUri = "",
        language = "en",
        navController = rememberNavController(),
        onNewScan = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Result - Disease (Blast)")
@Composable
fun ResultScreenDiseaseTaPreview() {
    ResultScreen(
        result = "Blast",
        confidence = 0.95f,
        imageCount = 2,
        isSoft = false,
        imageUri = "",
        language = "ta",
        navController = rememberNavController(),
        onNewScan = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Result - Healthy")
@Composable
fun ResultScreenHealthyPreview() {
    ResultScreen(
        result = "Healthy",
        confidence = 0.98f,
        imageCount = 1,
        isSoft = false,
        imageUri = "",
        language = "en",
        navController = rememberNavController(),
        onNewScan = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Result - Not Millet")
@Composable
fun ResultScreenUnrecognisedPreview() {
    ResultScreen(
        result = "Not Millet Leaf",
        confidence = 0.90f,
        imageCount = 1,
        isSoft = false,
        imageUri = "",
        language = "en",
        navController = rememberNavController(),
        onNewScan = {},
        onBackClick = {}
    )
}

@Preview(showBackground = true, name = "Result - Tamil (Ergot)")
@Composable
fun ResultScreenTamilPreview() {
    ResultScreen(
        result = "Ergot",
        confidence = 0.99f,
        imageCount = 3,
        isSoft = false,
        imageUri = "",
        language = "ta",
        navController = rememberNavController(),
        onNewScan = {},
        onBackClick = {}
    )
}
