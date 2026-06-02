package com.pearlmillet.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pearlmillet.app.R
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.ui.theme.AppTypography
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.BrandDeepGreen


private data class DiseaseCard(
    val id: String,
    val nameEn: String,
    val nameTa: String,
    val botanicalNameEn: String,
    val botanicalNameTa: String,
    val imageRes: Int,
    val color: Color
)

private val diseases = listOf(
    DiseaseCard("blast", "Blast", "குலை நோய்", "Magnaporthe grisea", "மேக்னபோர்தா கிரைசியா", R.drawable.blast_img, AppColors.ErrorRed),
    DiseaseCard("downy_mildew", "Downy Mildew", "அடிச்சாம்பல் நோய்", "Sclerospora graminicola", "ஸ்க்லெரோஸ்போரா கிராமினிகோலா", R.drawable.downymildew_img, AppColors.VividGreen),
    DiseaseCard("ergot", "Ergot", "தேன் ஒழுகல் நோய்", "Claviceps fusiformis", "கிளாவிசெப்ஸ் ஃபுசிபார்மிஸ்", R.drawable.ergot_img, AppColors.SeverePurple),
    DiseaseCard("rust", "Rust", "துரு நோய்", "Puccinia substriata", "பச்சினியா சப்ஸ்ட்ரியாட்டா", R.drawable.rust_img, AppColors.WarningOrange),
    DiseaseCard("smut", "Smut", "கரிப்பூட்டை நோய்", "Tolyposporium penicillariae", "டோலிபோஸ்போரியம் பெனிசில்லேரியே", R.drawable.smut_img, AppColors.InfoBlue)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiseaseLibraryScreen(
    language: String,
    navController: NavController,
    onBack: () -> Unit,
    onMenuClick: () -> Unit
) {
    val isTamil = language == "ta"
    val headerGradient = Brush.verticalGradient(
        colors = listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))
    )

    val pageBackground = Color(0xFFF8F5F4)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 115.dp else 170.dp
    val cornerRadius = if (isLandscape) 30.dp else 65.dp
    val titleFontSize = if (isLandscape) (if (isTamil) 20.sp else 24.sp) else (if (isTamil) 24.sp else 30.sp)
    val bottomPadding = if (isLandscape) 12.dp else 16.dp

    PearlMilletScaffold(
        containerColor = pageBackground
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = AppLayout.ContentMaxWidth)
            ) {


                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 0.dp, shape = RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                        .clip(RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                        .background(headerGradient)
                ) {
                    // Header Content
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
                                text = if (isTamil) "நோய் நூலகம்" else "Disease Library",
                                fontSize = if (isNarrow) {
                                    if (isTamil) 20.sp else 24.sp
                                } else {
                                    if (isTamil) 24.sp else 30.sp
                                },
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                                lineHeight = if (isTamil) 32.sp else 40.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))


                Column(modifier = Modifier.padding(horizontal = AppLayout.DefaultHorizontalPadding)) {
                    diseases.forEach { disease ->
                        DiseaseMenuItem(
                            disease = disease,
                            isTamil = isTamil,
                            onClick = {
                                navController.navigate("disease_detail/${disease.id}")
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DiseaseMenuItem(
    disease: DiseaseCard,
    isTamil: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 115.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Layout(
            content = {
                Image(
                    painter = painterResource(id = disease.imageRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(100.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
                )

                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (isTamil) disease.nameTa else disease.nameEn,
                        fontSize = if (isTamil) 16.sp else 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = disease.color,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (isTamil) disease.botanicalNameTa else disease.botanicalNameEn,
                        fontSize = AppTypography.smallSize(isTamil),
                        fontStyle = FontStyle.Italic,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) { measurables, constraints ->
            val imageMeasurable = measurables[0]
            val contentMeasurable = measurables[1]
            val chevronMeasurable = measurables[2]

            // 1. Measure chevron first
            val chevronPlaceable = chevronMeasurable.measure(constraints.copy(minWidth = 0, minHeight = 0))

            // 2. Measure content column
            val imageWidth = 100.dp.roundToPx()
            val paddingRight = 12.dp.roundToPx()
            val remainingWidth = (constraints.maxWidth - imageWidth - chevronPlaceable.width - paddingRight).coerceAtLeast(0)
            val contentPlaceable = contentMeasurable.measure(
                constraints.copy(minWidth = 0, maxWidth = remainingWidth, minHeight = 0)
            )

            // 3. Determine row height based on content height plus padding
            val contentVerticalPadding = 16.dp.roundToPx() // 8.dp top + 8.dp bottom
            val minHeight = 115.dp.roundToPx()
            val rowHeight = maxOf(minHeight, contentPlaceable.height + contentVerticalPadding)

            // 4. Measure image with fixed width and computed row height
            val imagePlaceable = imageMeasurable.measure(
                androidx.compose.ui.unit.Constraints.fixed(width = imageWidth, height = rowHeight)
            )

            // 5. Layout all children
            layout(width = constraints.maxWidth, height = rowHeight) {
                imagePlaceable.placeRelative(0, 0)

                val contentY = (rowHeight - contentPlaceable.height) / 2
                contentPlaceable.placeRelative(imageWidth, contentY)

                val chevronX = constraints.maxWidth - chevronPlaceable.width - 12.dp.roundToPx()
                val chevronY = (rowHeight - chevronPlaceable.height) / 2
                chevronPlaceable.placeRelative(chevronX, chevronY)
            }
        }
    }
}


