package com.pearlmillet.app.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.R
import com.pearlmillet.app.data.database.ScanResultListItem
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.utils.ScanSessionManager
import com.pearlmillet.app.utils.getDiseaseInfo
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.ui.draw.shadow
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight

// -------------------------------------------------------------------------
// CUSTOM PREVIEW-ONLY COMPONENTS (To avoid touching production files)
// -------------------------------------------------------------------------

@Composable
fun READMEHistoryMediumCard(scan: ScanResultListItem, language: String, onClick: () -> Unit) {
    val isTamil = language == "ta"
    val diseaseInfo = getDiseaseInfo(scan.result, language)

    Card(
        modifier = Modifier.width(140.dp).height(170.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Gray.copy(alpha = 0.1f))) {
                // LOAD DRAWABLE DIRECTLY FOR THE PREVIEW
                val imageRes = if (scan.result.equals("Blast", ignoreCase = true)) R.drawable.blast_img else R.drawable.downymildew_img
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp))
                )
            }
            Column(modifier = Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = if (isTamil) "சமீபத்திய" else "Recent",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
                Text(
                    text = diseaseInfo.name,
                    fontSize = if (isTamil) 12.sp else 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.DeepGreen
                )
            }
        }
    }
}

@Composable
fun READMEHomeScreenContent(
    userName: String,
    language: String,
    weatherData: WeatherDataState,
    recentScans: List<ScanResultListItem>,
    navController: NavController,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val strings = getLocalHomeStrings(language)
    val isTamil = language == "ta"

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
                HeaderSection(userName, weatherData, strings.welcome, strings.fallbackFarmer, language, onMenuClick)
                Spacer(Modifier.height(24.dp))
                HeroScanSection(strings, isTamil, onScanClick)
                Spacer(Modifier.height(32.dp))

                if (recentScans.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppLayout.DefaultHorizontalPadding),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = strings.history,
                            fontSize = if (isTamil) 18.sp else 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.TextDark
                        )
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color.White,
                            border = BorderStroke(1.dp, Color.LightGray.copy(alpha=0.5f)),
                            modifier = Modifier.clickable(onClick = onHistoryClick)
                        ) {
                            Text(
                                text = if (isTamil) "மேலும் பார்க்க" else "View More",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.TextDark,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = AppLayout.DefaultHorizontalPadding),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recentScans) { scan ->
                            // USE OUR PREVIEW CARD INSTEAD
                            READMEHistoryMediumCard(scan, language, onHistoryClick)
                        }
                    }
                    Spacer(Modifier.height(28.dp))
                }

                Text(
                    text = strings.library,
                    fontSize = if (isTamil) 18.sp else 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark,
                    modifier = Modifier.padding(horizontal = AppLayout.DefaultHorizontalPadding)
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    KnowledgeHubCard(
                        title = strings.diseaseLib,
                        icon = Icons.Default.BugReport,
                        iconColor = AppColors.ErrorRed,
                        modifier = Modifier.weight(1f),
                        isTamil = isTamil,
                        onClick = { navController.navigate("disease_library") }
                    )
                    KnowledgeHubCard(
                        title = strings.fertilizerGuide,
                        icon = Icons.Default.Science,
                        iconColor = AppColors.WarningOrange,
                        modifier = Modifier.weight(1f),
                        isTamil = isTamil,
                        onClick = { navController.navigate("fertilizer_guide") }
                    )
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

// -------------------------------------------------------------------------
// PREVIEWS
// -------------------------------------------------------------------------

@Preview(showBackground = true, name = "1 - Welcome Screen", widthDp = 360, heightDp = 800)
@Composable
fun READMEWelcomePreview() {
    MaterialTheme {
        WelcomeScreen(onStartClick = {})
    }
}

@Preview(showBackground = true, name = "2 - Home Screen", widthDp = 360, heightDp = 800)
@Composable
fun READMEHomePreview() {
    MaterialTheme {
        // USE OUR PREVIEW-ONLY HOMESCREEN CONTENT
        READMEHomeScreenContent(
            userName = "Dr. Vinith",
            language = "en",
            weatherData = WeatherDataState("28°C", "Sunny", Icons.Default.Cloud),
            recentScans = listOf(
                ScanResultListItem(id = 1, result = "Downy Mildew", confidence = 0.95f, timestamp = System.currentTimeMillis()),
                ScanResultListItem(id = 2, result = "Blast", confidence = 0.88f, timestamp = System.currentTimeMillis() - 86400000)
            ),
            navController = rememberNavController(),
            onScanClick = {},
            onHistoryClick = {},
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true, name = "3 - Camera Scan", widthDp = 360, heightDp = 800)
@Composable
fun READMECameraPreview() {
    MaterialTheme {
        CameraScanScreen(
            language = "en",
            continuationScanCount = 0,
            onImageCaptured = {},
            onBackClick = {}
        )
    }
}

@Composable
fun READMEResultScreenContent(
    result: String,
    language: String,
    navController: NavController,
    onNewScan: () -> Unit,
    onBackClick: () -> Unit
) {
    val isTamil = language == "ta"
    val diseaseInfo = getDiseaseInfo(result, language)
    val isHealthy = result.equals("Healthy", ignoreCase = true)
    val isNotLeaf = result.equals("Not Millet Leaf", ignoreCase = true)
    val isDisease = !isHealthy && !isNotLeaf

    val diseaseId = if (result.equals("Blast", ignoreCase = true)) "blast" else "downy_mildew"
    // Hardcode color for the preview to avoid missing internal function access
    val stateColor = AppColors.ErrorRed
    val pageBackground = Color(0xFFF8F5F4)
    
    // Hardcode the dummy bitmap to guarantee rendering in the IDE
    val dummyBitmap = androidx.compose.ui.graphics.ImageBitmap.imageResource(id = R.drawable.blast_img)

    PearlMilletScaffold(containerColor = pageBackground) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = AppLayout.ContentMaxWidth).wrapContentHeight()) {
                
                // Use the real ResultImageCard with our hardcoded dummyBitmap!
                com.pearlmillet.app.ui.screens.ResultImageCard(
                    bitmap = dummyBitmap.asAndroidBitmap(),
                    onBackClick = onBackClick,
                    onFullScreenClick = { }
                )

                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 4.dp)) {
                    Text(
                        text = diseaseInfo.name,
                        fontSize = if (isTamil) 24.sp else 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextDark,
                        lineHeight = if (isTamil) 30.sp else 34.sp
                    )
                    Text(
                        text = diseaseInfo.botanicalName,
                        fontSize = if (isTamil) 14.sp else 16.sp,
                        color = AppColors.TextDark,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    com.pearlmillet.app.ui.screens.ResultStatusCard(
                        isDisease = isDisease,
                        isHealthy = isHealthy,
                        isTamil = isTamil,
                        stateColor = stateColor,
                        diseaseId = diseaseId,
                        strings_recommendation = "Recommendation",
                        strings_viewTreatment = "View Treatment",
                        onViewTreatment = {}
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    com.pearlmillet.app.ui.screens.ResultDescriptionCard(
                        description = diseaseInfo.description,
                        isDisease = isDisease,
                        isTamil = isTamil,
                        stateColor = stateColor
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onNewScan,
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.DeepGreen)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(text = "New Scan", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "4 - Result Analysis", widthDp = 360, heightDp = 800)
@Composable
fun READMEResultPreview() {
    MaterialTheme {
        READMEResultScreenContent(
            result = "blast",
            language = "en",
            navController = rememberNavController(),
            onNewScan = {},
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true, name = "5 - Disease Management", widthDp = 360, heightDp = 1800)
@Composable
fun READMEFertilizerPreview() {
    MaterialTheme {
        FertilizerGuideScreen(
            language = "en",
            onBack = {},
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true, name = "6 - Tamil Localization", widthDp = 360, heightDp = 800)
@Composable
fun READMETamilPreview() {
    MaterialTheme {
        // USE OUR PREVIEW-ONLY HOMESCREEN CONTENT
        READMEHomeScreenContent(
            userName = "விவசாயி",
            language = "ta",
            weatherData = WeatherDataState("28°C", "தெளிவான வானம்", Icons.Default.Cloud),
            recentScans = listOf(
                ScanResultListItem(id = 1, result = "Downy Mildew", confidence = 0.95f, timestamp = System.currentTimeMillis())
            ),
            navController = rememberNavController(),
            onScanClick = {},
            onHistoryClick = {},
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true, name = "7 - Disease Library", widthDp = 360, heightDp = 850)
@Composable
fun READMEDiseaseLibraryPreview() {
    MaterialTheme {
        DiseaseLibraryScreen(
            language = "en",
            navController = rememberNavController(),
            onBack = {},
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true, name = "8 - Disease Detail", widthDp = 360, heightDp = 1430)
@Composable
fun READMEDiseaseDetailPreview() {
    MaterialTheme {
        DiseaseDetailScreen(
            diseaseId = "ergot",
            language = "ta",
            onBack = {}
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun READMEHistoryItem(
    item: com.pearlmillet.app.data.database.ScanResultListItem,
    isTamil: Boolean
) {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(containerColor = Color.White),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.Transparent)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp),
                color = Color.LightGray.copy(alpha = 0.2f)
            ) {
                val drawableRes = when (item.result) {
                    "Blast" -> R.drawable.blast_img
                    "Downy Mildew" -> R.drawable.downymildew_img
                    "Rust" -> R.drawable.rust_img
                    else -> R.drawable.millet_plant
                }
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = drawableRes),
                    contentDescription = null,
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                val diseaseInfo = getDiseaseInfo(item.result, if (isTamil) "ta" else "en")
                Text(
                    text = diseaseInfo.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTamil) 15.sp else 17.sp,
                    color = AppColors.TextDark
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(androidx.compose.material.icons.Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = java.text.SimpleDateFormat("dd MMM, hh:mm a", java.util.Locale.getDefault()).format(java.util.Date(item.timestamp)),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            androidx.compose.material3.Surface(shape = androidx.compose.foundation.shape.CircleShape, color = Color.LightGray.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun READMEHistoryScreenContent(allScanResults: List<com.pearlmillet.app.data.database.ScanResultListItem>) {
    val headerGradient = androidx.compose.ui.graphics.Brush.linearGradient(
        colors = listOf(AppColors.DeepGreen, com.pearlmillet.app.ui.theme.BrandDeepGreen.copy(alpha = 0.85f)),
        start = androidx.compose.ui.geometry.Offset(0f, 0f),
        end = androidx.compose.ui.geometry.Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )

    PearlMilletScaffold(containerColor = Color(0xFFF8F5F4)) {
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(modifier = Modifier.fillMaxWidth().widthIn(max = AppLayout.ContentMaxWidth).wrapContentHeight()) {
                Box(modifier = Modifier.fillMaxWidth().shadow(elevation = 0.dp, shape = RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp)).clip(RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp)).background(headerGradient)) {
                    Column(modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = AppLayout.DefaultHorizontalPadding).padding(top = 16.dp, bottom = 32.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            androidx.compose.material3.IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.25f), androidx.compose.foundation.shape.CircleShape).size(40.dp)) {
                                Icon(androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                            }
                            androidx.compose.material3.IconButton(onClick = { }, modifier = Modifier.background(Color.White.copy(alpha = 0.25f), androidx.compose.foundation.shape.CircleShape).size(40.dp)) {
                                Icon(androidx.compose.material.icons.Icons.Default.Menu, null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.padding(start = 48.dp)) {
                            Text(text = "DETECTION LOGS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5D6A7), letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "History", fontSize = 28.sp, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 0.5.sp, lineHeight = 34.sp)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Column(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)) {
                    allScanResults.forEach { item ->
                        Box(modifier = Modifier.padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 6.dp)) {
                            READMEHistoryItem(item = item, isTamil = false)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "9 - History List", widthDp = 360, heightDp = 800)
@Composable
fun READMEHistoryPreview() {
    MaterialTheme {
        READMEHistoryScreenContent(
            allScanResults = listOf(
                com.pearlmillet.app.data.database.ScanResultListItem(id = 1, result = "Downy Mildew", confidence = 0.95f, timestamp = System.currentTimeMillis()),
                com.pearlmillet.app.data.database.ScanResultListItem(id = 2, result = "Blast", confidence = 0.88f, timestamp = System.currentTimeMillis() - 86400000), // 1 day ago
                com.pearlmillet.app.data.database.ScanResultListItem(id = 3, result = "Rust", confidence = 0.91f, timestamp = System.currentTimeMillis() - (86400000L * 3)) // 3 days ago
            )
        )
    }
}
