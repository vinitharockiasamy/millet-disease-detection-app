package com.pearlmillet.app.ui.components
import androidx.compose.foundation.background

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout

// Note: PearlMilletAssistantDialog and HealthySuccessDialog have been removed.
// The app now uses the cleaner, farmer-friendly SimpleResultCard defined in ScanningAnalysisScreen.kt.

/**
 * Inline progress card for multi-image continuation.
 * Non-blocking — sits at bottom of analysis screen.
 * Buttons stacked vertically to prevent Tamil text overflow.
 */
@Composable
fun DiagnosticProgressCard(
    imageCount: Int,
    language: String,
    onContinue: () -> Unit,
    onCancel: () -> Unit
) {
    val isTamil = language == "ta"

    // Short, action-oriented copy — prioritise visual cleanliness
    val stepLabel = when (imageCount) {
        1 -> if (isTamil) "✓ முதல் படம் பதிவானது" else "✓ First scan captured"
        2 -> if (isTamil) "✓ இரண்டு படங்கள் தயார்" else "✓ Second scan captured"
        else -> if (isTamil) "✓ படம் ${imageCount} தயார்" else "✓ Scan $imageCount captured"
    }
    val subLabel = when (imageCount) {
        1 -> if (isTamil) "இன்னொரு கோணம் எடுக்கவும்" else "Taking another angle helps"
        else -> if (isTamil) "இறுதி உறுதிப்படுத்தல்" else "Final confirmation"
    }
    val continueText = if (isTamil) "மேலும் படம் எடுக்கவும்" else "Capture Another Angle"
    val cancelText = if (isTamil) "நிறுத்து" else "Stop"

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val maxH = configuration.screenHeightDp.dp * 0.85f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .widthIn(max = AppLayout.BottomSheetMaxWidth)
            .heightIn(max = maxH)
            .wrapContentHeight()
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
            .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = if (isLandscape) 12.dp else 20.dp)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Step progress bar ───────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                repeat(3) { index ->
                    val filled = index < imageCount
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(5.dp)
                            .clip(CircleShape)
                            .background(if (filled) Color(0xFF2E7D32) else Color(0xFFE0E0E0))
                    )
                }
            }

            Spacer(Modifier.height(if (isLandscape) 10.dp else 18.dp))

            // ── Step context ────────────────────────────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "$imageCount / 3",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF2E7D32)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = stepLabel,
                    fontSize = if (isTamil) 13.sp else 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1A1A1A)
                )
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = subLabel,
                fontSize = if (isTamil) 9.5.sp else 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier.padding(start = 76.dp)
            )

            Spacer(Modifier.height(if (isLandscape) 10.dp else 18.dp))

            // ── Continue button — full width, always fits ───────────────────
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(8.dp))
                Text(
                    text = continueText,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTamil) 13.sp else 15.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(if (isLandscape) 4.dp else 10.dp))

            // ── Cancel — ghost text button, full width ──────────────────────
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth().heightIn(min = 40.dp)
            ) {
                Text(
                    text = cancelText,
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (isTamil) 13.sp else 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}



// ---------------------------------------------------------------------------
// HealthySuccessCard — A unique, celebratory card for healthy crops
// ---------------------------------------------------------------------------

@Composable
fun HealthySuccessCard(
    language: String,
    onAction: () -> Unit,
    onBack: () -> Unit
) {
    val isTa = language == "ta"
    val title = if (isTa) "ஆரோக்கியமான பயிர்!" else "Healthy Crop!"
    val subMessage = if (isTa) "நோய் அறிகுறி எதுவும் இல்லை" else "No disease symptoms detected"
    val buttonText = if (isTa) "புதிய இலை" else "Scan Another Leaf"
    val backText = if (isTa) "← பின்செல்" else "← Back"

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val maxH = configuration.screenHeightDp.dp * 0.85f

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AppLayout.DialogMaxWidth)
                .heightIn(max = maxH)
                .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                .wrapContentHeight(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Celebratory Icon Area
                Box(contentAlignment = Alignment.Center) {
                    // Outer glow
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9)
                    ) {}
                    // Inner solid
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = CircleShape,
                        color = Color(0xFF4CAF50),
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = title,
                    fontSize = if (isTa) 20.sp else 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = subMessage,
                    fontSize = if (isTa) 14.sp else 17.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.Bold,
                        fontSize = if (isTa) 17.sp else 21.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(16.dp))

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Text(
                        text = backText,
                        color = Color.Gray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isTa) 13.sp else 17.sp
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// SimpleResultCard — replaces all complex dialogs for farmer-friendly UX
// One icon. One message. One button. No confusion.
// ---------------------------------------------------------------------------

@Composable
fun SimpleResultCard(
    icon: ImageVector,
    iconColor: Color,
    cardColor: Color,
    message: String,
    subMessage: String,
    buttonText: String,
    buttonColor: Color,
    backText: String,
    language: String,
    onAction: () -> Unit,
    onBack: () -> Unit
) {
    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val maxH = configuration.screenHeightDp.dp * 0.85f

    val isTa = language == "ta"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = AppLayout.DialogMaxWidth)
                .heightIn(max = maxH)
                .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                Surface(
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    color = iconColor.copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(34.dp)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Main message — short, direct
                Text(
                    text = message,
                    fontSize = if (isTa) 16.sp else 21.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(6.dp))

                // Sub message — one line guidance
                Text(
                    text = subMessage,
                    fontSize = if (isTa) 12.sp else 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(24.dp))

                // Single primary action button
                Button(
                    onClick = onAction,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = buttonText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (isTa) 16.sp else 18.5.sp,
                        color = Color.White
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Back — subtle but visible text link
                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)
                ) {
                    Text(
                        text = backText,
                        color = Color.DarkGray,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = if (isTa) 14.sp else 18.sp
                    )
                }
            }
        }
    }
}

// ============================================================
// DIAGNOSTIC PROGRESS CARD PREVIEWS
// ============================================================

@Preview(
    name = "Progress Card 1 EN",
    showBackground = true,
    backgroundColor = 0xFF121212,
    heightDp = 740
)
@Composable
fun PreviewProgressCard1EN() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.BottomCenter
    ) {
        DiagnosticProgressCard(
            imageCount = 1,
            language = "en",
            onContinue = {},
            onCancel = {}
        )
    }
}


@Preview(
    name = "Progress Card 2 EN",
    showBackground = true,
    backgroundColor = 0xFF121212,
    heightDp = 740
)
@Composable
fun PreviewProgressCard2EN() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.BottomCenter
    ) {
        DiagnosticProgressCard(
            imageCount = 2,
            language = "en",
            onContinue = {},
            onCancel = {}
        )
    }
}


@Preview(
    name = "Progress Card 1 TA",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 360,
    heightDp = 740
)
@Composable
fun PreviewProgressCard1TA() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.BottomCenter
    ) {
        DiagnosticProgressCard(
            imageCount = 1,
            language = "ta",
            onContinue = {},
            onCancel = {}
        )
    }
}


@Preview(
    name = "Progress Card 2 TA",
    showBackground = true,
    backgroundColor = 0xFF121212,
    widthDp = 360,
    heightDp = 740
)
@Composable
fun PreviewProgressCard2TA() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212)),
        contentAlignment = Alignment.BottomCenter
    ) {
        DiagnosticProgressCard(
            imageCount = 2,
            language = "ta",
            onContinue = {},
            onCancel = {}
        )
    }
}


// ============================================================
// RESULT CARDS PREVIEWS
// ============================================================

@Preview(showBackground = true, name = "Healthy Success EN", backgroundColor = 0xFF121212)
@Composable
fun PreviewAssistantHealthySuccessEn() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        HealthySuccessCard(language = "en", onAction = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Healthy Success TA", backgroundColor = 0xFF121212)
@Composable
fun PreviewAssistantHealthySuccessTa() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        HealthySuccessCard(language = "ta", onAction = {}, onBack = {})
    }
}

@Preview(showBackground = true, name = "Generic Error EN", backgroundColor = 0xFF121212)
@Composable
fun PreviewAssistantGenericErrorEn() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        SimpleResultCard(
            icon = Icons.Default.CameraAlt, iconColor = Color(0xFFE65100), cardColor = Color(0xFFFFF3E0),
            message = "Pearl millet area is unclear",
            subMessage = "Please capture a clear image", buttonText = "Try Again", buttonColor = Color(0xFFE65100), backText = "← Back", language = "en", onAction = {}, onBack = {}
        )
    }
}

@Preview(showBackground = true, name = "Generic Error TA", backgroundColor = 0xFF121212)
@Composable
fun PreviewAssistantGenericErrorTa() {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212)), contentAlignment = Alignment.Center) {
        SimpleResultCard(
            icon = Icons.Default.CameraAlt, iconColor = Color(0xFFE65100), cardColor = Color(0xFFFFF3E0),
            message = "கம்பு பகுதி தெளிவாக இல்லை",
            subMessage = "தெளிவான படத்தை எடுக்கவும்", buttonText = "மீண்டும்", buttonColor = Color(0xFFE65100), backText = "← பின்செல்", language = "ta", onAction = {}, onBack = {}
        )
    }
}