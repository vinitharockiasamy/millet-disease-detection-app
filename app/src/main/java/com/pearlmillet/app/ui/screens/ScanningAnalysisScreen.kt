package com.pearlmillet.app.ui.screens

import com.pearlmillet.app.ui.components.HealthySuccessCard
import com.pearlmillet.app.ui.components.SimpleResultCard
import android.util.Log
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.data.StringsRepository
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.data.database.ScanResult
import com.pearlmillet.app.tflite.InferenceProvider
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppTypography
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.utils.ScanSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import com.pearlmillet.app.engine.DiagnosticEngine
import com.pearlmillet.app.engine.DiagnosticOutcome
import com.pearlmillet.app.engine.DiagnosticRules

import com.pearlmillet.app.quality.QualityIssue


private const val TAG = "ScanningAnalysis"

// ---------------------------------------------------------------------------
// Validation State
// ---------------------------------------------------------------------------

sealed class ValidationState {
    object Idle : ValidationState()
    data class QualityError(val issue: QualityIssue) : ValidationState()
    data class NonMilletError(val message: String) : ValidationState()
    data class NeedsConfirmation(val imageCount: Int) : ValidationState()
    data class HealthySuccess(val label: String, val confidence: Float, val imageCount: Int) : ValidationState()
    object UncertainDiagnosis : ValidationState()
}

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun ScanningAnalysisScreen(
    imageUri: String,
    language: String,
    database: AppDatabase,
    onDetectionComplete: (String, Float, Int, Boolean) -> Unit,
    onBackClick: () -> Unit,
    onNewScan: (Int) -> Unit   // imageCount passed so caller knows which continuation scan
) {
    val context = LocalContext.current
    val strings = StringsRepository.getStrings(language)
    var validationState by remember { mutableStateOf<ValidationState>(ValidationState.Idle) }
    var savedOutcomeStr by rememberSaveable { mutableStateOf<String?>(null) }

    val displayBitmap = ScanSessionManager.getCurrentImage()
    val session = ScanSessionManager.currentSession

    // Run analysis off the Main Thread
    LaunchedEffect(savedOutcomeStr) {
        if (savedOutcomeStr != null) {
            val parts = savedOutcomeStr!!.split("_")
            when (parts[0]) {
                "NeedsConfirmation" -> validationState = ValidationState.NeedsConfirmation(parts[1].toInt())
                "HealthyCrop" -> validationState = ValidationState.HealthySuccess(parts[1], parts[2].toFloat(), parts[3].toInt())
                "Uncertain" -> validationState = ValidationState.UncertainDiagnosis
                "QualityError" -> validationState = ValidationState.QualityError(com.pearlmillet.app.quality.QualityIssue.valueOf(parts[1]))
                "NonMilletError" -> validationState = ValidationState.NonMilletError(parts[1])
            }
            return@LaunchedEffect
        }

        val currentSession = session ?: return@LaunchedEffect
        val startTime = System.currentTimeMillis()

        val outcome = withContext(Dispatchers.Default) {
            DiagnosticEngine.analyzeSession(context, currentSession)
        }

        // Save display image to internal storage (on IO thread)
        val imagePath = withContext(Dispatchers.IO) {
            displayBitmap?.let { com.pearlmillet.app.utils.saveImageToInternalStorage(context, it) } ?: ""
        }

        withContext(Dispatchers.Main) {
            // Enforce minimum 1.0s analysis feel (Down from 1.8s for better speed)
            val elapsed = System.currentTimeMillis() - startTime
            val remaining = 1000L - elapsed
            if (remaining > 0) delay(remaining)

            when (outcome) {
                is DiagnosticOutcome.Success -> {

                    com.pearlmillet.app.utils.DebugTools.logSessionState(currentSession)
                    // Persist scan to DB
                    withContext(Dispatchers.IO) {
                        database.scanResultDao().insertScanResult(
                            ScanResult(
                                userName = "Farmer",
                                result = outcome.label,
                                confidence = outcome.confidence,
                                imagePath = imagePath,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    onDetectionComplete(outcome.label, outcome.confidence, outcome.imageCount, false)
                }
                is DiagnosticOutcome.SoftSuccess -> {
                    com.pearlmillet.app.utils.DebugTools.logSessionState(currentSession)
                    // Persist scan to DB
                    withContext(Dispatchers.IO) {
                        database.scanResultDao().insertScanResult(
                            ScanResult(
                                userName = "Farmer",
                                result = outcome.label,
                                confidence = outcome.confidence,
                                imagePath = imagePath,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    onDetectionComplete(outcome.label, outcome.confidence, outcome.imageCount, true)
                }
                is DiagnosticOutcome.NeedsMoreImages -> {
                    com.pearlmillet.app.utils.DebugTools.logSessionState(currentSession)
                    savedOutcomeStr = "NeedsConfirmation_${outcome.imageCount}"
                    validationState = ValidationState.NeedsConfirmation(
                        imageCount = outcome.imageCount
                    )
                }
                is DiagnosticOutcome.UncertainDiagnosis -> {
                    com.pearlmillet.app.utils.DebugTools.logSessionState(currentSession)
                    com.pearlmillet.app.utils.DebugTools.logFailureLocally("LowConfidence", mapOf("count" to currentSession.imageResults.size))
                    savedOutcomeStr = "Uncertain"
                    validationState = ValidationState.UncertainDiagnosis
                }
                is DiagnosticOutcome.QualityFailure -> {
                    savedOutcomeStr = "QualityError_${outcome.issue}"
                    validationState = ValidationState.QualityError(outcome.issue)
                }
                is DiagnosticOutcome.HealthyCrop -> {
                    com.pearlmillet.app.utils.DebugTools.logSessionState(currentSession)
                    // Persist healthy scan to DB
                    withContext(Dispatchers.IO) {
                        database.scanResultDao().insertScanResult(
                            ScanResult(
                                userName = "Farmer",
                                result = outcome.label,
                                confidence = outcome.confidence,
                                imagePath = imagePath,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    savedOutcomeStr = "HealthyCrop_${outcome.label}_${outcome.confidence}_${outcome.imageCount}"
                    validationState = ValidationState.HealthySuccess(outcome.label, outcome.confidence, outcome.imageCount)
                }
                is DiagnosticOutcome.MilletFailure -> {
                    savedOutcomeStr = "NonMilletError_${outcome.message}"
                    validationState = ValidationState.NonMilletError(outcome.message)
                }
                is DiagnosticOutcome.SystemFailure -> {
                    Log.e(TAG, "System failure: ${outcome.error}")
                    onBackClick()
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        displayBitmap?.let { b ->
            Image(
                bitmap = b.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Deep professional overlay (Lighter for continuation to feel non-blocking)
            val overlayAlpha = if (validationState is ValidationState.NeedsConfirmation) 0.3f else 0.75f
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = overlayAlpha))
            )
            
            // Radial vignette for "blur" feel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f)),
                            radius = 2000f
                        )
                    )
            )

            // Subtle grid texture
            Canvas(modifier = Modifier.fillMaxSize().alpha(0.08f)) {
                val step = 40.dp.toPx()
                for (x in 0..size.width.toInt() step step.toInt()) {
                    drawLine(Color.White, Offset(x.toFloat(), 0f), Offset(x.toFloat(), size.height), 1f)
                }
                for (y in 0..size.height.toInt() step step.toInt()) {
                    drawLine(Color.White, Offset(0f, y.toFloat()), Offset(size.width, y.toFloat()), 1f)
                }
            }

            // Central animation while idle
            if (validationState is ValidationState.Idle) {
                var debugTapCount by remember { mutableStateOf(0) }
                var showDebugPanel by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier.fillMaxSize().clickable(enabled = com.pearlmillet.app.BuildConfig.DEBUG) {
                        debugTapCount++
                        if (debugTapCount >= 5) showDebugPanel = true
                    },
                    contentAlignment = Alignment.Center
                ) {
                    CentralHubAnimation(language)
                    
                    if (com.pearlmillet.app.BuildConfig.DEBUG && showDebugPanel) {
                        DebugInfoPanel(session)
                    }
                }
            }
        }

        // ── Simple, direct result overlays ────────────────────────────────────
        ValidationStateOverlay(
            state = validationState,
            language = language,
            onNewScan = onNewScan,
            onBackClick = onBackClick
        )
    }
}

// ---------------------------------------------------------------------------
// Validation State Overlay (Extracted for exact Preview syncing)
// ---------------------------------------------------------------------------

@Composable
fun ValidationStateOverlay(
    state: ValidationState,
    language: String,
    onNewScan: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    when (state) {
        is ValidationState.Idle -> {}

        is ValidationState.NeedsConfirmation -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
                com.pearlmillet.app.ui.components.DiagnosticProgressCard(
                    imageCount = state.imageCount,
                    language = language,
                    onContinue = { onNewScan(state.imageCount) },
                    onCancel = onBackClick
                )
            }
        }

        is ValidationState.HealthySuccess -> {
            HealthySuccessCard(
                language = language,
                onAction = {
                    ScanSessionManager.clearSession()
                    onNewScan(0)
                },
                onBack = {
                    ScanSessionManager.clearSession()
                    onBackClick()
                }
            )
        }


        is ValidationState.NonMilletError,
        is ValidationState.QualityError,
        is ValidationState.UncertainDiagnosis -> {
            SimpleResultCard(
                icon = Icons.Default.CameraAlt,
                iconColor = Color(0xFFE65100),
                cardColor = Color(0xFFFFF3E0),
                message =
                    if (language == "ta") "கம்பு பகுதி தெளிவாக இல்லை"
                    else "Pearl millet area is unclear",
                subMessage =
                    if (language == "ta") "தெளிவான படத்தை எடுக்கவும்"
                    else "Please capture a clear image",
                buttonText = if (language == "ta") "மீண்டும்" else "Try Again",
                buttonColor = Color(0xFFE65100),
                backText = if (language == "ta") "← பின்செல்" else "← Back",
                language = language,
                onAction = {
                    ScanSessionManager.clearSession()
                    onNewScan(0)
                },
                onBack = {
                    ScanSessionManager.clearSession()
                    onBackClick()
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Scanning Animation
// ---------------------------------------------------------------------------

@Composable
fun CentralHubAnimation(language: String) {
    val isTamil = language == "ta"
    val infiniteTransition = rememberInfiniteTransition(label = "hub")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "rotation"
    )
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "pulse"
    )
    val iconAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "iconAlpha"
    )

    val primaryColor = AppColors.VividGreen
    val secondaryColor = AppColors.WarningOrange

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(160.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                rotate(rotation) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                primaryColor.copy(alpha = 0f),
                                primaryColor.copy(alpha = 0.5f),
                                primaryColor
                            )
                        ),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                rotate(-rotation * 1.5f) {
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                secondaryColor.copy(alpha = 0f),
                                secondaryColor.copy(alpha = 0.5f),
                                secondaryColor
                            )
                        ),
                        radius = size.minDimension / 2,
                        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
                    .background(primaryColor.copy(alpha = 0.1f), CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(primaryColor.copy(alpha = 0.3f), Color.Transparent)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = primaryColor.copy(alpha = iconAlpha),
                    modifier = Modifier.size(40.dp)
                )
            }
        }

        Spacer(Modifier.height(40.dp))

        Text(
            text = if (isTamil) "பகுப்பாய்வு செய்யப்படுகிறது..." else "Analyzing Crop Health...",
            color = Color.White,
            fontSize = AppTypography.headerSize(isTamil),
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (isTamil) "AI தொழில்நுட்பம்" else "AI Powered Analysis",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = AppTypography.bodySize(isTamil)
        )
    }
}

@Composable
fun BoxScope.DebugInfoPanel(session: com.pearlmillet.app.engine.DiagnosticSession?) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = AppLayout.ContentMaxWidth)
            .padding(AppLayout.DefaultHorizontalPadding)
            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.Green.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp)
            .align(Alignment.TopCenter)
    ) {
        Column {
            Text("INTERNAL DEBUG PANEL", color = Color.Green, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Spacer(Modifier.height(8.dp))
            session?.let { s ->
                Text("Image Count: ${s.imageResults.size}", color = Color.White, fontSize = 11.sp)
                s.imageResults.lastOrNull()?.let { last ->
                    Text("Label: ${last.predictedLabel}", color = Color.White, fontSize = 11.sp)
                    Text("Conf: ${String.format("%.4f", last.confidence)}", color = Color.White, fontSize = 11.sp)
                    Text("Validation: ${last.validationState}", color = Color.White, fontSize = 11.sp)
                }
            } ?: Text("No active session", color = Color.Red, fontSize = 11.sp)
        }
    }
}

