package com.pearlmillet.app.ui.screens

import android.graphics.Bitmap
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.data.StringsRepository
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.utils.getDiseaseId
import com.pearlmillet.app.utils.getDiseaseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ---------------------------------------------------------------------------
// History Result Screen — shown when viewing a past scan from History
// ---------------------------------------------------------------------------

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryResultScreen(
    result: String = "",
    confidence: Float = 0f,
    imageUri: String = "",
    timestamp: Long = 0L,
    scanId: Long? = null,
    database: AppDatabase? = null,
    language: String,
    navController: NavController,
    onNewScan: () -> Unit,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    val isTamil = language == "ta"
    val strings = StringsRepository.getStrings(language)

    var currentResult by remember { mutableStateOf(result) }
    var currentTimestamp by remember { mutableStateOf(timestamp) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showFullImage by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    // Load scan record from DB if arrived via history_result_by_id route
    LaunchedEffect(scanId) {
        if (scanId != null && database != null) {
            try {
                val scanResult = withContext(Dispatchers.IO) {
                    database.scanResultDao().getScanResultById(scanId)
                }
                if (scanResult != null) {
                    currentResult = scanResult.result
                    currentTimestamp = scanResult.timestamp
                    if (scanResult.imagePath.isNotEmpty()) {
                        val file = java.io.File(scanResult.imagePath)
                        if (file.exists()) {
                            val loaded = android.graphics.BitmapFactory.decodeFile(file.absolutePath)
                            withContext(Dispatchers.Main) {
                                bitmap?.recycle()
                                bitmap = loaded
                            }
                        }
                    }
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            bitmap?.recycle()
            bitmap = null
        }
    }

    val diseaseId = getDiseaseId(currentResult)
    val diseaseInfo = getDiseaseInfo(currentResult, language)
    val isHealthy = currentResult.equals("Healthy", ignoreCase = true)
    val isNotLeaf = currentResult.equals("Not Millet Leaf", ignoreCase = true)
    val isDisease = !isHealthy && !isNotLeaf
    val stateColor = diseaseStateColor(currentResult, isHealthy, isNotLeaf)

    val pageBackground = Color(0xFFF8F5F4)

    // Full screen image overlay
    if (showFullImage && bitmap != null) {
        Dialog(
            onDismissRequest = { showFullImage = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier.fillMaxSize().background(Color.Black).windowInsetsPadding(WindowInsets.systemBars)
            ) {
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
        androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxWidth()) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
            ) {
                // ── Hero image (with date pill in top-right) ─────────────
                ResultImageCard(
                    bitmap = bitmap,
                    onBackClick = onBackClick,
                    onFullScreenClick = { showFullImage = true },
                    topContent = {
                        // Date pill — shown only for history scans
                        if (currentTimestamp > 0) {
                            Surface(
                                shape = RoundedCornerShape(50),
                                color = Color.Black.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                                        .format(Date(currentTimestamp)),
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                )

                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = diseaseInfo.name,
                        fontSize = if (isTamil) 24.sp else 28.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.TextDark,
                        lineHeight = if (isTamil) 30.sp else 34.sp
                    )
                    if (diseaseInfo.botanicalName.isNotEmpty()) {
                        androidx.compose.material3.Text(
                            text = diseaseInfo.botanicalName,
                            fontSize = if (isTamil) 14.sp else 16.sp,
                            color = if (isDisease) {
                                if (stateColor == AppColors.VividGreen) AppColors.DeepGreen else stateColor
                            } else Color.Gray,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                    }

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(12.dp))

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

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))

                    ResultDescriptionCard(
                        description = diseaseInfo.description,
                        isDisease = isDisease,
                        isTamil = isTamil,
                        stateColor = stateColor
                    )

                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(name = "History Blast — EN", showBackground = true, heightDp = 1000)
@Composable
fun PreviewBlastEn() {
    MaterialTheme {
        HistoryResultScreen("Blast", 0.9f, "", 1678888888L, null, null, "en", rememberNavController(), {}, {}, {})
    }
}

@Preview(name = "History Blast — TA", showBackground = true, heightDp = 1000)
@Composable
fun PreviewBlastTa() {
    MaterialTheme {
        HistoryResultScreen("Blast", 0.9f, "", 1678888888L, null, null, "ta", rememberNavController(), {}, {}, {})
    }
}