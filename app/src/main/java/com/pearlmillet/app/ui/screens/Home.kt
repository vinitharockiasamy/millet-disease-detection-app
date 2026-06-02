package com.pearlmillet.app.ui.screens

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.R
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.data.database.ScanResultListItem
import com.pearlmillet.app.ui.components.AppFooter
import com.pearlmillet.app.ui.components.PearlMilletScaffold
import com.pearlmillet.app.ui.components.HistoryImage
import com.pearlmillet.app.ui.theme.AppColors
import com.pearlmillet.app.ui.theme.AppLayout
import com.pearlmillet.app.ui.theme.BrandDeepGreen
import com.pearlmillet.app.utils.getDiseaseInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL



data class HomeStrings(
    val scanTitle: String,
    val scanSubtitle: String,
    val didYouKnow: String,
    val readMore: String,
    val library: String,
    val diseaseLib: String,
    val fertilizerGuide: String,
    val history: String,
    val welcome: String,
    val fallbackFarmer: String
)

fun getLocalHomeStrings(language: String): HomeStrings {
    return if (language == "ta") {
        HomeStrings(
            welcome = "வணக்கம்",
            history = "வரலாறு",
            scanTitle = "பயிரை ஸ்கேன் செய்யவும்",
            scanSubtitle = "நோய் கண்டறிதல் & தீர்வு",
            didYouKnow = "உங்களுக்குத் தெரியுமா?",
            readMore = "மேலும் படிக்க",
            library = "அறிவு மையம்",
            diseaseLib = "நோய் களஞ்சியம்",
            fertilizerGuide ="நோய் மேலாண்மை",
            fallbackFarmer = "விவசாயி"
        )
    } else {
        HomeStrings(
            welcome = "Welcome",
            history = "Recent History",
            scanTitle = "Scan Your Crop",
            scanSubtitle = "Detect Diseases & Get Solutions",
            didYouKnow = "Did You Know?",
            readMore = "Read More",
            library = "Knowledge Hub",
            diseaseLib = "Disease Library",
            fertilizerGuide = "Disease Management",
            fallbackFarmer = "Farmer")
    }
}

data class WeatherDataState(
    val temperature: String,
    val status: String,
    val icon: ImageVector
)



@Composable
fun HomeScreen(
    userName: String,
    userRole: String,
    language: String,
    navController: NavController,
    onScanClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val context = LocalContext.current
    var recentScans by remember { mutableStateOf<List<ScanResultListItem>>(emptyList()) }
    var weatherData by remember { mutableStateOf(WeatherDataState("--", "Loading", Icons.Default.Cloud)) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                AppDatabase.getDatabase(context).scanResultDao().getAllScanResults().collect { list ->
                    recentScans = list.take(5)
                }
            } catch (e: Exception) {
                recentScans = emptyList()
            }
        }
    }

    // Prevent redundant weather fetches on every navigation
    var lastFetchTime by remember { mutableStateOf(0L) }
    LaunchedEffect(Unit) {
        val currentTime = System.currentTimeMillis()
        if (weatherData.temperature == "--" || (currentTime - lastFetchTime > 15 * 60 * 1000)) {
            if (isNetworkAvailable(context)) {
                try {
                    weatherData = fetchWeatherData()
                    lastFetchTime = currentTime
                } catch (e: Exception) {
                    weatherData = WeatherDataState("--", "Error", Icons.Default.CloudOff)
                }
            }
        }
    }

    HomeScreenContent(
        userName = userName,
        language = language,
        weatherData = weatherData,
        recentScans = recentScans,
        navController = navController,
        onScanClick = onScanClick,
        onHistoryClick = onHistoryClick,
        onMenuClick = onMenuClick
    )
}



@Composable
fun HomeScreenContent(
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
                                text = if (isTamil) "மேலும்" else "View More",
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
                            HistoryMediumCard(scan, language, onHistoryClick)
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

                // Note: Footer is now automatically handled by PearlMilletScaffold at the end of scroll
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}



@Composable
fun HeaderSection(
    userName: String,
    weatherData: WeatherDataState,
    welcomeText: String,
    fallbackName: String,
    language: String,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(top = 16.dp, start = AppLayout.DefaultHorizontalPadding, end = AppLayout.DefaultHorizontalPadding, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "$welcomeText,",
                fontSize = if (language == "ta") 13.sp else 14.sp,
                color = AppColors.TextDark.copy(alpha = 0.6f)
            )
            Text(
                text = if (userName.isEmpty()) fallbackName else userName,
                fontSize = if (language == "ta") 21.sp else 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = AppColors.TextDark,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Spacer(Modifier.width(16.dp))

        Surface(
            shape = RoundedCornerShape(50),
            color = Color(0xFFF5F5F5),
            border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = weatherData.icon,
                    contentDescription = null,
                    tint = AppColors.GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = weatherData.temperature,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextDark
                )
            }
        }

        Spacer(Modifier.width(12.dp))

        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 4.dp,
            modifier = Modifier.size(42.dp).clickable(onClick = onMenuClick)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Menu, "Menu", tint = AppColors.DeepGreen)
            }
        }
    }
}

@Composable
fun HeroScanSection(strings: HomeStrings, isTamil: Boolean, onScanClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppLayout.DefaultHorizontalPadding)
            .height(200.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.verticalGradient(colors = listOf(AppColors.DeepGreen, BrandDeepGreen)))
            .clickable(onClick = onScanClick)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val verticalShift = -65f
            val horizontalShift = 25f
            val centerX = (size.width * 0.5f) + horizontalShift
            val bottomY = (size.height * 1f) + verticalShift
            val topY = (size.height * 0.1f) + verticalShift
            val pivotY = (size.height * 0.45f) + verticalShift

            rotate(degrees = 50f, pivot = Offset(centerX, pivotY)) {
                val leafPath = Path().apply {
                    moveTo(centerX, bottomY)
                    quadraticTo(centerX - size.height * 0.65f, (size.height * 0.6f) + verticalShift, centerX, topY)
                    quadraticTo(centerX + size.height * 0.65f, (size.height * 0.6f) + verticalShift, centerX, bottomY)
                    close()
                }

                drawPath(
                    path = leafPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            AppColors.VividGreen.copy(alpha = 0.2f),
                            Color(0xFF81C784).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
                drawLine(
                    color = AppColors.VividGreen.copy(alpha = 0.1f),
                    start = Offset(centerX, bottomY),
                    end = Offset(centerX, topY),
                    strokeWidth = 2f
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val isNarrow =  LocalConfiguration.current.screenWidthDp < 360
            Surface(
                shape = CircleShape,
                color = AppColors.VividGreen,
                modifier = Modifier.size(72.dp),
                shadowElevation = 8.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.CameraAlt, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = strings.scanTitle,
                fontSize = if (isNarrow) {
                    if (isTamil) 16.sp else 19.sp
                } else {
                    if (isTamil) 18.sp else 22.sp
                },
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = strings.scanSubtitle,
                fontSize = if (isNarrow) 12.sp else 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color((0xFFA5D6A7)
                ))
        }
    }
}



@Composable
fun KnowledgeHubCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    isTamil: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier.height(120.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 3.dp,
        border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha=0.4f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(shape = CircleShape, color = iconColor.copy(alpha = 0.1f), modifier = Modifier.size(42.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = title,
                fontSize = if (isTamil) 12.sp else 13.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextDark,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun HistoryMediumCard(scan: ScanResultListItem, language: String, onClick: () -> Unit) {
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
                HistoryImage(scanId = scan.id, result = scan.result, modifier = Modifier.fillMaxSize())
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




private fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
    return false
}

private suspend fun fetchWeatherData(): WeatherDataState = withContext(Dispatchers.IO) {
    try {
        val lat = 11.0168; val long = 76.9558
        val response = URL("https://api.open-meteo.com/v1/forecast?latitude=$lat&longitude=$long&current_weather=true").readText()
        val json = JSONObject(response).getJSONObject("current_weather")
        WeatherDataState("${json.getDouble("temperature")}°C", "Clear", Icons.Default.WbSunny)
    } catch (e: Exception) {
        WeatherDataState("--", "Error", Icons.Default.CloudOff)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 750)
@Composable
fun HomeScreenPreviewWithHistory() {
    val mockRecentScans = listOf(
        ScanResultListItem(id = 1, result = "Downy Mildew", confidence = 0.92f, timestamp = System.currentTimeMillis()),
        ScanResultListItem(id = 2, result = "Smut", confidence = 0.98f, timestamp = System.currentTimeMillis()),
        ScanResultListItem(id = 3, result = "Ergot", confidence = 0.85f, timestamp = System.currentTimeMillis())
    )

    HomeScreenContent(
        userName = "Vinith",
        language = "ta",
        weatherData = WeatherDataState("28°C", "Sunny", Icons.Default.WbSunny),
        recentScans = mockRecentScans,
        navController = rememberNavController(),
        onScanClick = {},
        onHistoryClick = {},
        onMenuClick = {}
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 750)
@Composable
fun HomeScreenPreviewWithenHistory() {
    val mockRecentScans = listOf(
        ScanResultListItem(id = 1, result = "Downy Mildew", confidence = 0.92f, timestamp = System.currentTimeMillis()),
        ScanResultListItem(id = 2, result = "Smut", confidence = 0.98f, timestamp = System.currentTimeMillis()),
        ScanResultListItem(id = 3, result = "Ergot", confidence = 0.85f, timestamp = System.currentTimeMillis())
    )

    HomeScreenContent(
        userName = "Vinith",
        language = "en",
        weatherData = WeatherDataState("28°C", "Sunny", Icons.Default.WbSunny),
        recentScans = mockRecentScans,
        navController = rememberNavController(),
        onScanClick = {},
        onHistoryClick = {},
        onMenuClick = {}
    )
}