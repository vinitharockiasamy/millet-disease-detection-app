package com.pearlmillet.app.ui.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.ViewGroup
import androidx.camera.core.ImageProxy
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview as ComposePreview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import com.pearlmillet.app.utils.saveImageToGallery
import com.pearlmillet.app.utils.ScanSessionManager
import com.pearlmillet.app.data.StringsRepository
import com.pearlmillet.app.ui.theme.AppLayout
import java.util.concurrent.Executor

// ─────────────────────────────────────────────────────────────────────────────
//  CameraScanScreen — Single scan environment
//
//  FLOW DESIGN (farmer-first):
//  • Camera capture → AUTO-proceed to analysis. No confirmation step.
//    Farmers just tap the shutter and it goes. Zero extra steps.
//  • Gallery pick → Show confirmation (because gallery images can be anything).
//  • Multi-image continuation → Shown as a subtle banner ON the camera screen.
//    User just taps shutter again. No navigation away.
//
//  The "continuation state" (needs more images) is passed in via the
//  `continuationScanCount` parameter so the camera screen knows to show
//  a progress banner instead of a plain viewfinder.
// ─────────────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScanScreen(
    language: String,
    continuationScanCount: Int = 0,   // 0 = fresh scan; 1,2 = continuation
    onImageCaptured: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val isInspection = LocalInspectionMode.current
    val cameraPermissionState = if (!isInspection) rememberPermissionState(android.Manifest.permission.CAMERA) else null
    val isCameraGranted = isInspection || cameraPermissionState?.status?.isGranted == true

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    val strings = StringsRepository.getStrings(language)
    val isTamil = language == "ta"
    val isContinuation = continuationScanCount > 0

    var flashEnabled by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var isFrontCamera by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var cameraSelector = if (isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA
    var isProcessing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val cameraRef = remember { mutableStateOf<androidx.camera.core.Camera?>(null) }

    val resolutionSelector = remember {
        ResolutionSelector.Builder()
            .setResolutionStrategy(
                ResolutionStrategy(
                    android.util.Size(1280, 720),
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                )
            ).build()
    }
    val preview = remember { Preview.Builder().setResolutionSelector(resolutionSelector).build() }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setResolutionSelector(resolutionSelector)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Gallery picker — loads bitmap and auto-proceeds, identical to camera flow
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { galleryUri ->
            if (ScanSessionManager.currentSession == null) ScanSessionManager.createSession()
            isProcessing = true
            scope.launch(Dispatchers.IO) {
                try {
                    val loaded = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val src = ImageDecoder.createSource(context.contentResolver, galleryUri)
                        ImageDecoder.decodeBitmap(src) { decoder, info, _ ->
                            val maxDim = maxOf(info.size.width, info.size.height)
                            if (maxDim > 1024) {
                                val r = maxDim / 1024f
                                decoder.setTargetSize((info.size.width / r).toInt(), (info.size.height / r).toInt())
                            }
                            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        }
                    } else {
                        @Suppress("DEPRECATION")
                        val orig = MediaStore.Images.Media.getBitmap(context.contentResolver, galleryUri)
                        if (orig.width > 1024 || orig.height > 1024) {
                            val r = maxOf(orig.width, orig.height) / 1024f
                            val scaled = Bitmap.createScaledBitmap(orig, (orig.width / r).toInt(), (orig.height / r).toInt(), true)
                            if (scaled != orig) orig.recycle()
                            scaled
                        } else orig
                    }
                    val mlBitmap = com.pearlmillet.app.utils.prepareBitmapForInference(loaded)
                    withContext(Dispatchers.Main) {
                        ScanSessionManager.currentSession?.originalBitmap = loaded
                        ScanSessionManager.currentSession?.mlBitmap = mlBitmap
                        isProcessing = false
                        onImageCaptured("in_memory")
                    }
                } catch (e: Exception) {
                    Log.e("CameraScan", "Gallery load error", e)
                    withContext(Dispatchers.Main) { isProcessing = false }
                }
            }
        }
    }

    LaunchedEffect(isCameraGranted) {
        if (!isInspection && cameraPermissionState?.status?.isGranted == false) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    // ── Root box: always the camera environment ───────────────────────────────
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        // ── Camera preview ────────────────────────────────────────────────────
        if (isCameraGranted) {
            if (!isInspection) {
                val previewViewRef = remember { mutableStateOf<PreviewView?>(null) }

                DisposableEffect(cameraSelector) {
                    val future = ProcessCameraProvider.getInstance(context)
                    var provider: ProcessCameraProvider? = null
                    future.addListener({
                        provider = future.get()
                        try {
                            provider?.unbindAll()
                            val camera = provider?.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture)
                            previewViewRef.value?.let { preview.setSurfaceProvider(it.surfaceProvider) }
                            cameraRef.value = camera
                            if (camera?.cameraInfo?.hasFlashUnit() == true) camera.cameraControl.enableTorch(flashEnabled)
                        } catch (e: Exception) { Log.e("CameraScan", "Binding failed", e) }
                    }, ContextCompat.getMainExecutor(context))

                    onDispose {
                        preview.setSurfaceProvider(null)
                        cameraRef.value?.cameraControl?.enableTorch(false)
                        cameraRef.value = null
                        provider?.unbindAll()
                    }
                }

                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).apply {
                            scaleType = PreviewView.ScaleType.FILL_CENTER
                            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        }.also { previewViewRef.value = it }
                    },
                    modifier = Modifier.fillMaxSize(),
                    update = { pv ->
                        if (previewViewRef.value != pv) previewViewRef.value = pv
                        preview.setSurfaceProvider(pv.surfaceProvider)
                    }
                )
            } else {
                // Mock background for Compose Preview
                Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))
            }
        }

        // ── Viewfinder overlay (darkening + corner marks) ─────────────────────
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val frameSize = minOf(size.width, size.height) * if (isLandscape) 0.5f else 0.75f
            val frameLeft = (size.width - frameSize) / 2
            val landscapeOffset = if (isLandscape) -30.dp.toPx() else 0f
            val frameTop = (size.height - frameSize) / 2 + landscapeOffset
            drawRect(color = Color.Black.copy(alpha = 0.45f), size = size)
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(frameLeft, frameTop),
                size = Size(frameSize, frameSize),
                cornerRadius = CornerRadius(24.dp.toPx()),
                blendMode = androidx.compose.ui.graphics.BlendMode.Clear
            )
            val cl = 40.dp.toPx(); val sw = 4.dp.toPx()
            drawLine(Color.White, Offset(frameLeft, frameTop), Offset(frameLeft + cl, frameTop), sw)
            drawLine(Color.White, Offset(frameLeft, frameTop), Offset(frameLeft, frameTop + cl), sw)
            drawLine(Color.White, Offset(frameLeft + frameSize, frameTop), Offset(frameLeft + frameSize - cl, frameTop), sw)
            drawLine(Color.White, Offset(frameLeft + frameSize, frameTop), Offset(frameLeft + frameSize, frameTop + cl), sw)
            drawLine(Color.White, Offset(frameLeft, frameTop + frameSize), Offset(frameLeft + cl, frameTop + frameSize), sw)
            drawLine(Color.White, Offset(frameLeft, frameTop + frameSize), Offset(frameLeft, frameTop + frameSize - cl), sw)
            drawLine(Color.White, Offset(frameLeft + frameSize, frameTop + frameSize), Offset(frameLeft + frameSize - cl, frameTop + frameSize), sw)
            drawLine(Color.White, Offset(frameLeft + frameSize, frameTop + frameSize), Offset(frameLeft + frameSize, frameTop + frameSize - cl), sw)
        }

        // ── Top bar ───────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            IconButton(onClick = {
                flashEnabled = !flashEnabled
                val cam = cameraRef.value
                if (cam != null && cam.cameraInfo.hasFlashUnit()) cam.cameraControl.enableTorch(flashEnabled)
            }) {
                Icon(if (flashEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff, contentDescription = "Flash", tint = Color.White)
            }
        }

        // ── Continuation banner (shown when this is scan 2 or 3) ─────────────
        // Sits below the viewfinder frame, above the shutter controls
        // Tells farmer "you're making progress" — NOT a failure state
        if (isContinuation) {
            val bannerMsg = when (continuationScanCount) {
                1 -> if (isTamil) "✓ முதல் படம் ஆனது • இன்னொரு கோணம் எடுக்கவும்"
                     else "✓ Scan 1 done • Capture another angle"
                2 -> if (isTamil) "✓ இரண்டு படங்கள் ஆனது • கடைசி கோணம் எடுக்கவும்"
                     else "✓ Scan 2 done • Capture the final angle"
                else -> if (isTamil) "இன்னொரு கோணம் எடுக்கவும்" else "Capture another angle"
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = if (isLandscape) 16.dp else 150.dp) // Above the viewfinder square
            ) {
                Surface(
                    shape = RoundedCornerShape(50.dp),
                    color = Color(0xFF2E7D32).copy(alpha = 0.90f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AutoGraph, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = bannerMsg,
                            color = Color.White,
                            fontSize = if (isTamil) 10.sp else 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // ── Bottom controls ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .widthIn(max = AppLayout.BottomSheetMaxWidth)
                .windowInsetsPadding(androidx.compose.foundation.layout.WindowInsets.safeDrawing)
                .padding(bottom = if (isLandscape) 8.dp else 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Guidance text
            Text(
                text = if (isContinuation) {
                    if (isTamil) "வேறு கோணத்தில் இலையை வைக்கவும்" else "Place the leaf at a different angle"
                } else {
                    strings.cameraGuidance
                },
                color = Color.White,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            Spacer(Modifier.height(if (isLandscape) 8.dp else 24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gallery button
                IconButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.18f), CircleShape)
                ) {
                    Icon(Icons.Default.Image, contentDescription = "Gallery", tint = Color.White)
                }

                // Shutter — auto-proceeds, no confirmation
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(if (isProcessing) Color.Gray else Color.White)
                        .clickable(enabled = !isProcessing) {
                            if (ScanSessionManager.currentSession == null) ScanSessionManager.createSession()
                            isProcessing = true
                            takePhoto(
                                imageCapture = imageCapture,
                                executor = ContextCompat.getMainExecutor(context),
                                onImageCaptured = { bitmap ->
                                    ScanSessionManager.currentSession?.originalBitmap = bitmap
                                    // Prepare ML bitmap off main thread, then proceed
                                    scope.launch(Dispatchers.Default) {
                                        ScanSessionManager.currentSession?.mlBitmap =
                                            com.pearlmillet.app.utils.prepareBitmapForInference(bitmap)
                                        launch(Dispatchers.IO) {
                                            saveImageToGallery(
                                                context, bitmap,
                                                "Pearl_Millet_${System.currentTimeMillis()}",
                                                "Scanned via Pearl Millet Care App"
                                            )
                                        }
                                        withContext(Dispatchers.Main) {
                                            isProcessing = false
                                            onImageCaptured("in_memory")
                                        }
                                    }
                                },
                                onError = {
                                    isProcessing = false
                                    Log.e("CameraScan", "Capture error", it)
                                }
                            )
                        }
                ) {
                    if (isProcessing) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(32.dp),
                                strokeWidth = 3.dp
                            )
                        }
                    }
                }

                // Switch camera
                IconButton(
                    onClick = {
                        isFrontCamera = !isFrontCamera
                    },
                    modifier = Modifier.size(52.dp).background(Color.White.copy(alpha = 0.18f), CircleShape)
                ) {
                    Icon(Icons.Default.Cameraswitch, contentDescription = "Switch Camera", tint = Color.White)
                }
            }
        }

    }
}


// ─────────────────────────────────────────────────────────────────────────────
//  Photo capture helper
// ─────────────────────────────────────────────────────────────────────────────

private fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    onImageCaptured: (Bitmap) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val bitmap = image.toBitmap()
                    val rotation = image.imageInfo.rotationDegrees
                    val finalBitmap = if (rotation != 0) {
                        val matrix = android.graphics.Matrix().apply { postRotate(rotation.toFloat()) }
                        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
                        bitmap.recycle()
                        rotated
                    } else bitmap
                    android.os.Handler(android.os.Looper.getMainLooper()).post { onImageCaptured(finalBitmap) }
                } catch (e: Exception) {
                    Log.e("CameraScan", "Error processing image proxy", e)
                } finally {
                    image.close()
                }
            }
            override fun onError(exc: ImageCaptureException) = onError(exc)
        }
    )
}

// ============================================================
// CAMERA SCAN SCREEN PREVIEWS
// ============================================================

@ComposePreview(showBackground = true, name = "1. Fresh Scan (EN)")
@Composable
fun PreviewCameraScanScreenFreshEn() {
    CameraScanScreen(language = "en", continuationScanCount = 0, onImageCaptured = {}, onBackClick = {})
}

@ComposePreview(showBackground = true, name = "1. Fresh Scan (TA)")
@Composable
fun PreviewCameraScanScreenFreshTa() {
    CameraScanScreen(language = "ta", continuationScanCount = 0, onImageCaptured = {}, onBackClick = {})
}

@ComposePreview(showBackground = true, name = "2. Scan 1 Done Banner (EN)")
@Composable
fun PreviewCameraScanScreenContinuation1En() {
    CameraScanScreen(language = "en", continuationScanCount = 1, onImageCaptured = {}, onBackClick = {})
}

@ComposePreview(showBackground = true, name = "2. Scan 1 Done Banner (TA)")
@Composable
fun PreviewCameraScanScreenContinuation1Ta() {
    CameraScanScreen(language = "ta", continuationScanCount = 1, onImageCaptured = {}, onBackClick = {})
}

@ComposePreview(showBackground = true, name = "3. Scan 2 Done Banner (EN)")
@Composable
fun PreviewCameraScanScreenContinuation2En() {
    CameraScanScreen(language = "en", continuationScanCount = 2, onImageCaptured = {}, onBackClick = {})
}

@ComposePreview(showBackground = true, name = "3. Scan 2 Done Banner (TA)")
@Composable
fun PreviewCameraScanScreenContinuation2Ta() {
    CameraScanScreen(language = "ta", continuationScanCount = 2, onImageCaptured = {}, onBackClick = {})
}
