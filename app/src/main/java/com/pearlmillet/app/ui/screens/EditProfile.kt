package com.pearlmillet.app.ui.screens

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.BrandDeepGreen

// Reverted TransliterationUtils import

@Composable
fun EditProfileScreen(
    currentLanguage: String,
    currentName: String,
    currentRole: String,
    onBack: () -> Unit = {},
    onSave: (name: String, role: String) -> Unit = { _, _ -> }
) {
    val isTamil = currentLanguage == "ta"

    val roleOptions = if (isTamil) listOf("விவசாயி", "மாணவர்", "நிபுணர்") else listOf("Farmer", "Student", "Professional")
    val roleIcons = listOf(Icons.Filled.Agriculture, Icons.Filled.School, Icons.Filled.Science)
    val roleCodeMap = mapOf("விவசாயி" to "farmer", "Farmer" to "farmer", "மாணவர்" to "student", "Student" to "student", "நிபுணர்" to "professional", "Professional" to "professional")

    var userName by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(currentName) }
    var selectedRole by androidx.compose.runtime.saveable.rememberSaveable { 
        mutableStateOf(roleOptions.find { roleCodeMap[it] == currentRole } ?: "") 
    }

    val contextLabel = if (isTamil) "சுயவிவர விவரங்கள்" else "PROFILE DETAILS"
    val mainTitle = if (isTamil) "சுயவிவரம்" else "Edit Profile"
    val themeMixedColor = lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f)

    val configuration = androidx.compose.ui.platform.LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 120.dp else 180.dp
    PearlMilletScaffold(
        enableScrolling = true,
        containerColor = Color(0xFFF9FBF9) // Softer surface background
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            /* 1. HEADER (Responsive Curved Box + Canvas design in both orientations) */
            Box(modifier = Modifier.fillMaxWidth()) {
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
                        .padding(horizontal = 24.dp)
                        .padding(top = 16.dp, bottom = 32.dp)
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.White.copy(0.25f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column {
                        Text(
                            contextLabel,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFA5D6A7),
                            letterSpacing = 2.sp,
                            textAlign = TextAlign.Start
                        )
                        Text(
                            mainTitle,
                            fontSize = if (isLandscape) 22.sp else (if (isTamil) 28.sp else 32.sp),
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

            /* 2. FORM SECTION */
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Name Field Group
                Column {
                    Text(if (isTamil) "முழு பெயர்" else "Full Name", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78909C))
                    Spacer(Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = userName,
                        onValueChange = { userName = it },
                        modifier = Modifier.fillMaxWidth().height(60.dp).shadow(2.dp, RoundedCornerShape(16.dp)),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        leadingIcon = { Icon(Icons.Default.Person, null, tint = lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f), modifier = Modifier.size(24.dp)) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF263238),
                            unfocusedTextColor = Color(0xFF263238),
                            focusedBorderColor = themeMixedColor,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )

                    // Reverted hints to normal
                    Spacer(Modifier.height(5.dp))
                    Text(
                        text = if (isTamil) "உங்கள் பெயர் மட்டும்" else "Your name only",
                        fontSize = 11.sp,
                        color = themeMixedColor.copy(alpha = 0.75f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Role Selector Group
                Column {
                    Text(if (isTamil) "நான் ஒரு..." else "I am a...", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF78909C))
                    Spacer(Modifier.height(12.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        roleOptions.forEachIndexed { index, role ->
                            FixedRoleTile(
                                text = role,
                                icon = roleIcons[index],
                                isSelected = selectedRole == role,
                                onClick = { selectedRole = role },
                                activeColor = themeMixedColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            /* 3. FOOTER BUTTON */
            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Button(
                    onClick = { 
                        onSave(userName.trim(), roleCodeMap[selectedRole] ?: "farmer") 
                    },
                    enabled = userName.isNotBlank() && selectedRole.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = themeMixedColor)
                ) {
                    Text(if (isTamil) "சேமி" else "SAVE CHANGES", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FixedRoleTile(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, activeColor: Color) {
    val bgColor by animateColorAsState(if (isSelected) activeColor else Color.White)
    val contentColor by animateColorAsState(if (isSelected) Color.White else Color(0xFF263238))

    Surface(
        modifier = Modifier.fillMaxWidth().height(68.dp).clickable { onClick() }.shadow(if(isSelected) 6.dp else 2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = bgColor,
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray.copy(0.3f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = if (isSelected) Color.White else lerp(AppColors.DeepGreen, Color(0xFF4CAF50), 0.5f), modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(16.dp))
            Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = contentColor)
            Spacer(Modifier.weight(1f))
            if (isSelected) Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(22.dp))
        }
    }
}

@Preview(showBackground = true, heightDp = 800)
@Composable
fun FinalAlignedPreview() {
    MaterialTheme {
        EditProfileScreen(
            currentLanguage = "en",
            currentName = "Ramu",
            currentRole = "farmer"
        )
    }
}