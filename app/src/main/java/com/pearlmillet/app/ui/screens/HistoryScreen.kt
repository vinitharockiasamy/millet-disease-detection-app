package com.pearlmillet.app.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.data.StringsRepository
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.data.database.ScanResultListItem
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.ui.theme.AppTypography
import com.pearlmillet.app.ui.components.HistoryImage
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.utils.getDiseaseInfo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    language: String,
    database: AppDatabase,
    navController: NavController,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var allScanResults by remember { mutableStateOf(emptyList<ScanResultListItem>()) }
    var isLoading by remember { mutableStateOf(true) }

    var isSelectionMode by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }
    var selectedIds by androidx.compose.runtime.saveable.rememberSaveable(
        stateSaver = androidx.compose.runtime.saveable.Saver<Set<Long>, String>(
            save = { it.joinToString(",") },
            restore = { str -> str.split(",").filter { it.isNotEmpty() }.map { it.toLong() }.toSet() }
        )
    ) { mutableStateOf(setOf<Long>()) }
    var showDeleteConfirmation by androidx.compose.runtime.saveable.rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        database.scanResultDao().getAllScanResults().collect { results ->
            allScanResults = results
            isLoading = false
        }
    }

    HistoryScreenContent(
        language = language,
        allScanResults = allScanResults,
        isLoading = isLoading,
        isSelectionMode = isSelectionMode,
        selectedIds = selectedIds,
        showDeleteConfirmation = showDeleteConfirmation,
        onBackClick = onBackClick,
        onMenuClick = onMenuClick,
        onToggleSelectionMode = {
            isSelectionMode = !isSelectionMode
            if (!isSelectionMode) selectedIds = emptySet()
        },
        onSelectAll = {
            selectedIds = if (selectedIds.size == allScanResults.size) emptySet() else allScanResults.map { it.id }.toSet()
        },
        onDeleteRequest = { showDeleteConfirmation = true },
        onConfirmDelete = {
            scope.launch {
                selectedIds.forEach { id ->
                    database.scanResultDao().deleteScanResult(id)
                }
                isSelectionMode = false
                selectedIds = emptySet()
                showDeleteConfirmation = false
            }
        },
        onCancelDelete = { showDeleteConfirmation = false },
        onItemClick = { item ->
            if (isSelectionMode) {
                selectedIds = if (selectedIds.contains(item.id)) {
                    selectedIds - item.id
                } else {
                    selectedIds + item.id
                }
            } else {
                navController.navigate("history_result_by_id/${item.id}")
            }
        },
        onItemLongClick = { item ->
            if (!isSelectionMode) {
                isSelectionMode = true
                selectedIds = selectedIds + item.id
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenContent(
    language: String,
    allScanResults: List<ScanResultListItem>,
    isLoading: Boolean,
    isSelectionMode: Boolean,
    selectedIds: Set<Long>,
    showDeleteConfirmation: Boolean,
    onBackClick: () -> Unit,
    onMenuClick: () -> Unit,
    onToggleSelectionMode: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteRequest: () -> Unit,
    onConfirmDelete: () -> Unit,
    onCancelDelete: () -> Unit,
    onItemClick: (ScanResultListItem) -> Unit,
    onItemLongClick: (ScanResultListItem) -> Unit
) {
    val isTamil = language == "ta"
    val strings = StringsRepository.getStrings(language)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val headerHeight = if (isLandscape) 115.dp else 170.dp
    val cornerRadius = if (isLandscape) 30.dp else 65.dp
    val bottomPadding = if (isLandscape) 12.dp else 16.dp
    val titleFontSize = if (isLandscape) (if (isTamil) 20.sp else 24.sp) else (if (isTamil) 24.sp else 28.sp)

    if (showDeleteConfirmation) {
        Dialog(
            onDismissRequest = onCancelDelete,
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f)) // Deeper, more focused backdrop
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .clickable(onClick = onCancelDelete),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(32.dp), // Ultra-smooth corners
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = AppLayout.DialogMaxWidth)
                        .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                        .clickable(onClick = {})
                        .shadow(24.dp, RoundedCornerShape(32.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .padding(32.dp)
                            .verticalScroll(rememberScrollState()), // More breathing room
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // High-Impact Warning Icon
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = Color(0xFFFFEBEE)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = null,
                                    tint = Color(0xFFD32F2F),
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = if (isTamil) "பதிவுகளை அழிக்கவா?" else "Delete Records?",
                            fontSize = if (isTamil) 20.sp else 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A1A1A),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isTamil) 
                                "தேர்ந்தெடுக்கப்பட்ட ${selectedIds.size} பதிவுகள் நிரந்தரமாக நீக்கப்படும். இதை மாற்ற முடியாது." 
                                else "Are you sure? ${selectedIds.size} selected records will be permanently removed.",
                            fontSize = 15.sp,
                            color = Color.DarkGray.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(36.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onConfirmDelete,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                                shape = RoundedCornerShape(16.dp),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                            ) {
                                Text(
                                    text = if (isTamil) "ஆம், அழி" else "Yes, Delete",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                            
                            TextButton(
                                onClick = onCancelDelete,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Text(
                                    text = if (isTamil) "வேண்டாம், ரத்து செய்" else "No, Keep Them",
                                    color = Color.Gray,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    val headerGradient = Brush.verticalGradient(
        colors = 
            listOf(AppColors.DeepGreen, BrandDeepGreen.copy(alpha = 0.85f))
    )

    val pageBackground = Color(0xFFF8F5F4)

    PearlMilletScaffold(
        containerColor = pageBackground,
        enableScrolling = true
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
            // 1. HEADER SECTION (Responsive Curved Box + Rounded Corner layout in both orientations)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 0.dp, shape = RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                    .clip(RoundedCornerShape(bottomStart = 65.dp, bottomEnd = 0.dp))
                    .background(headerGradient)
            ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = AppLayout.DefaultHorizontalPadding)
                            .padding(top = 16.dp, bottom = 32.dp)
                    ) {
                        // Top Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Back / Close Button
                            IconButton(
                                onClick = if (isSelectionMode) onToggleSelectionMode else onBackClick,
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
                                    .size(40.dp)
                            ) {
                                Icon(
                                    imageVector = if (isSelectionMode) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }

                            // Menu / Select Actions
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isSelectionMode) {
                                    IconButton(
                                        onClick = onSelectAll,
                                        modifier = Modifier.background(Color.White.copy(alpha = 0.25f), CircleShape).size(40.dp)
                                    ) {
                                        Icon(Icons.Default.SelectAll, null, tint = Color.White)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    if (selectedIds.isNotEmpty()) {
                                        IconButton(
                                            onClick = onDeleteRequest,
                                            modifier = Modifier.background(Color.Red.copy(alpha = 0.4f), CircleShape).size(40.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, null, tint = Color.White)
                                        }
                                    }
                                } else {
                                    IconButton(
                                        onClick = onMenuClick,
                                        modifier = Modifier.background(Color.White.copy(alpha = 0.25f), CircleShape).size(40.dp)
                                    ) {
                                        Icon(Icons.Default.Menu, null, tint = Color.White)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Header Texts
                        Column(
                            modifier = Modifier.padding(start = 48.dp)
                        ) {
                            Text(
                                text = if (isTamil) "கடந்த கால தேடல்கள்" else "DETECTION LOGS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelectionMode) Color.LightGray else Color(0xFFA5D6A7),
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isSelectionMode) {
                                    if (isTamil) "${selectedIds.size} தேர்வு" else "${selectedIds.size} Selected"
                                } else {
                                    if (isTamil) "வரலாறு" else "History"
                                },
                                fontSize = if (isTamil) 24.sp else 28.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                                lineHeight = 34.sp
                            )
                        }
                    }
            }

            Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))

            // 2. LIST CONTENT (Using LazyColumn for high performance)
            if (isLoading) {
                Box(Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AppColors.VibrantGreen)
                }
            } else if (allScanResults.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(300.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.HistoryToggleOff, null, modifier = Modifier.size(80.dp), tint = Color.Gray.copy(alpha = 0.3f))
                        Spacer(Modifier.height(16.dp))
                        Text(text = strings.noHistory, fontSize = 16.sp, color = Color.Gray)
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                ) {
                    allScanResults.forEach { item ->
                        Box(modifier = Modifier.padding(horizontal = AppLayout.DefaultHorizontalPadding, vertical = 6.dp)) {
                            HistoryItem(
                                item = item,
                                isTamil = isTamil,
                                isSelectionMode = isSelectionMode,
                                isSelected = selectedIds.contains(item.id),
                                onLongClick = { onItemLongClick(item) },
                                onClick = { onItemClick(item) }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryItem(
    item: ScanResultListItem,
    isTamil: Boolean,
    isSelectionMode: Boolean,
    isSelected: Boolean,
    onLongClick: () -> Unit,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFFE8F5E9) else Color.White,
        animationSpec = tween(durationMillis = 200),
        label = "BgColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, if (isSelected) AppColors.DeepGreen else Color.Transparent)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelectionMode) {
                RadioButton(
                    selected = isSelected,
                    onClick = null,
                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.DeepGreen)
                )
                Spacer(Modifier.width(8.dp))
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp),
                color = Color.LightGray.copy(alpha = 0.2f)
            ) {
                HistoryImage(
                    scanId = item.id,
                    result = item.result,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Get translated info
                val diseaseInfo = getDiseaseInfo(item.result, if (isTamil) "ta" else "en")
                
                Text(
                    text = diseaseInfo.name, // Display translated name
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isTamil) 15.sp else 17.sp,
                    color = AppColors.TextDark
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(item.timestamp)),
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                }
            }

            if (!isSelectionMode) {
                Surface(
                    shape = CircleShape,
                    color = Color.LightGray.copy(alpha = 0.1f),
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color.LightGray
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "English - Phone")
@Composable
fun HistoryScreenContentEnglishPreview() {
    MaterialTheme {
        HistoryScreenContent(
            language = "en",
            allScanResults = listOf(
                com.pearlmillet.app.data.database.ScanResultListItem(1L, "Blast", 0.95f, System.currentTimeMillis()),
                com.pearlmillet.app.data.database.ScanResultListItem(2L, "Healthy", 0.99f, System.currentTimeMillis() - 86400000)
            ),
            isLoading = false,
            isSelectionMode = false,
            selectedIds = emptySet(),
            showDeleteConfirmation = false,
            onBackClick = {},
            onMenuClick = {},
            onToggleSelectionMode = {},
            onSelectAll = {},
            onDeleteRequest = {},
            onConfirmDelete = {},
            onCancelDelete = {},
            onItemClick = {},
            onItemLongClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Tamil - Phone")
@Composable
fun HistoryScreenContentTamilPreview() {
    MaterialTheme {
        HistoryScreenContent(
            language = "ta",
            allScanResults = listOf(
                com.pearlmillet.app.data.database.ScanResultListItem(1L, "Blast", 0.95f, System.currentTimeMillis()),
                com.pearlmillet.app.data.database.ScanResultListItem(2L, "Healthy", 0.99f, System.currentTimeMillis() - 86400000)
            ),
            isLoading = false,
            isSelectionMode = false,
            selectedIds = emptySet(),
            showDeleteConfirmation = false,
            onBackClick = {},
            onMenuClick = {},
            onToggleSelectionMode = {},
            onSelectAll = {},
            onDeleteRequest = {},
            onConfirmDelete = {},
            onCancelDelete = {},
            onItemClick = {},
            onItemLongClick = {}
        )
    }
}