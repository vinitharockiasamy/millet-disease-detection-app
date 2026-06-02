package com.pearlmillet.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.ui.theme.AppLayout

// Reverted TransliterationUtils import

@Composable
fun FirstTimeSetupScreen(
    currentLanguage: String,
    onSetupComplete: (name: String, role: String) -> Unit,
    onBackClick: () -> Unit = {}
) {
    var userName by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    var selectedRole by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf("") }
    val isTamil = currentLanguage == "ta"

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 120.dp else 180.dp
    val textBottomPadding = if (isLandscape) 12.dp else 16.dp

    val contextLabel = if (isTamil) "பதிவு செய்க" else "GET STARTED"
    val mainTitle = if (isTamil) "சுயவிவரம்" else "Create Profile"
    val namePlaceholder = if (isTamil) "உங்கள் பெயர்" else "Full Name"
    val roleHeader = if (isTamil) "நான் ஒரு..." else "I am a..."
    val buttonText = if (isTamil) "தொடரவும்" else "CONTINUE"

    val roleOptions =
        if (isTamil) listOf("விவசாயி", "மாணவர்", "நிபுணர்")
        else listOf("Farmer", "Student", "Professional")

    val roleIcons = listOf(
        Icons.Filled.Agriculture,
        Icons.Filled.School,
        Icons.Filled.Science
    )

    val roleCodeMap = mapOf(
        "விவசாயி" to "farmer",
        "Farmer" to "farmer",
        "மாணவர்" to "student",
        "Student" to "student",
        "நிபுணர்" to "professional",
        "Professional" to "professional"
    )

    val headerGradient = Brush.verticalGradient(
        colors = listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))
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

            /* ---------------- HEADER ---------------- */
            Box(
                modifier = Modifier.fillMaxWidth() // Responsive, wraps content height
            ) {
                // Original perfect portrait curve restored universally
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
                        .padding(top = 16.dp, bottom = 32.dp) // Universal solid paddings
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            contextLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFA5D6A7),
                            letterSpacing = 2.sp
                        )
                        Text(
                            mainTitle,
                            fontSize = if (isLandscape) (if (isTamil) 20.sp else 24.sp) else (if (isTamil) 28.sp else 32.sp),
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = if (isLandscape) 8.dp else 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = {
                        Text(namePlaceholder, color = Color.Gray)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(12.dp)),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Person,
                            null,
                            tint = lerp(
                                AppColors.DeepGreen,
                                Color(0xFF4CAF50),
                                0.5f
                            )
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = AppColors.TextDark,
                        unfocusedTextColor = AppColors.TextDark,
                        cursorColor = AppColors.DeepGreen,
                        focusedBorderColor = lerp(
                            AppColors.DeepGreen,
                            Color(0xFF4CAF50),
                            0.5f
                        ),
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )

                // Reverted hint
                Text(
                    text = if (isTamil) "உங்கள் பெயர் மட்டும்" else "Your name only",
                    fontSize = 11.sp,
                    color = AppColors.DeepGreen.copy(alpha = 0.7f),
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp)
                )

                Text(
                    roleHeader,
                    fontSize = if (isTamil) 14.sp else 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    roleOptions.forEachIndexed { index, role ->
                        SetupRoleTile(
                            text = role,
                            icon = roleIcons[index],
                            isSelected = selectedRole == role,
                            isTamil = isTamil,
                            onClick = { selectedRole = role }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSetupComplete(userName.trim(), roleCodeMap[selectedRole] ?: "farmer")
                    },
                    enabled = userName.isNotBlank() && selectedRole.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = lerp(
                            AppColors.DeepGreen,
                            Color(0xFF4CAF50),
                            0.5f
                        ),
                        disabledContainerColor = Color.LightGray.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        disabledContentColor = Color.White.copy(alpha = 0.6f)
                        ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                    border = BorderStroke(
                        1.dp,
                        Color.LightGray.copy(alpha = 0.3f)
                    )
                ) {
                    Text(
                        buttonText,
                        fontSize = if (isTamil) 18.sp else 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        null,
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
}

/* ---------------- ROLE TILE ---------------- */
@Composable
private fun SetupRoleTile(
    text: String,
    icon: ImageVector,
    isSelected: Boolean,
    isTamil: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        if (isSelected)
            lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f)
        else Color.White,
        label = "bg"
    )

    val contentColor by animateColorAsState(
        if (isSelected) Color.White else AppColors.TextDark,
        label = "content"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = if (isSelected) null
        else BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                null,
                tint = if (isSelected) Color.White
                else lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(16.dp))
            Text(
                text,
                fontSize = if (isTamil) 15.sp else 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
            Spacer(Modifier.weight(1f))
            if (isSelected) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "First Time Setup - Phone")
@Composable
fun FirstTimeSetupScreenPreview() {
    MaterialTheme {
        FirstTimeSetupScreen(currentLanguage = "en", onSetupComplete = { _, _ -> })
    }
}

@Preview(showBackground = true, name = "First Time Setup - Tablet", device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun FirstTimeSetupScreenTabletPreview() {
    MaterialTheme {
        FirstTimeSetupScreen(currentLanguage = "en", onSetupComplete = { _, _ -> })
    }
}

