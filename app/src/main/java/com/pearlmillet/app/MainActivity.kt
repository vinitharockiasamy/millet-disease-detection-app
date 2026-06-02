package com.pearlmillet.app

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.pearlmillet.app.data.AppPreferences
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.ui.screens.*
import com.pearlmillet.app.ui.theme.PearlMilletCareTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.pearlmillet.app.tflite.InferenceProvider
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var appPreferences: AppPreferences
    private lateinit var database: AppDatabase
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        appPreferences = AppPreferences(this)
        database = AppDatabase.getDatabase(this)

        // Keep Splash Screen on until data is ready
        lifecycleScope.launch {
            combine(
                appPreferences.isFirstLaunch,
                appPreferences.language,
                appPreferences.userName,
                appPreferences.userRole,
                appPreferences.isSetupComplete
            ) { _, _, _, _, _ ->
                true
            }.collect { ready ->
                isReady = ready
            }
        }
        
        splashScreen.setKeepOnScreenCondition { !isReady }

        setContent {
            PearlMilletCareTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(appPreferences = appPreferences, database = database)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        InferenceProvider.closeAll()
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation(appPreferences: AppPreferences, database: AppDatabase) {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    val isFirstLaunch by appPreferences.isFirstLaunch.collectAsState(initial = null)
    val userName by appPreferences.userName.collectAsState(initial = null)
    val language by appPreferences.language.collectAsState(initial = null)
    val userRole by appPreferences.userRole.collectAsState(initial = null)
    val setupComplete by appPreferences.isSetupComplete.collectAsState(initial = null)

    val isFirstLaunchLoaded = isFirstLaunch != null
    val languageLoaded = language != null
    val userNameLoaded = userName != null
    val userRoleLoaded = userRole != null
    val setupCompleteLoaded = setupComplete != null

    // Calculate start destination based on prefs
    val startDestination = if (isFirstLaunch == true || language?.isEmpty() == true || userName?.isEmpty() == true) {
        "welcome"
    } else {
        "home"
    }

    if (!isFirstLaunchLoaded || !languageLoaded || !userNameLoaded || !userRoleLoaded || !setupCompleteLoaded) {
        // "Invisible" Loading State - Covered by System Splash
        // We render an empty background to ensure the window is layout-ready
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background))
    } else {
        NavHost(navController = navController, startDestination = startDestination) {
            // Splash route removed - logic handled above

            composable(
                route = "welcome",
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None }
            ) {
            WelcomeScreen(
                onStartClick = {
                    navController.navigate("language") {
                        popUpTo("welcome") { inclusive = true }
                    }
                }
            )
        }

        composable("language") {
            LanguageSelectScreen { selectedLang ->
                scope.launch {
                    appPreferences.saveLanguage(selectedLang)
                    // We DO NOT set FirstLaunchComplete here anymore, 
                    // or maybe we do? Usually 'FirstLaunch' is done after they leave welcome?
                    // Let's keep existing logic: FirstLaunch set after language.
                    appPreferences.setFirstLaunchComplete()
                    navController.navigate("setup")
                }
            }
        }

        composable("setup") {
            FirstTimeSetupScreen(
                currentLanguage = language ?: "ta",
                onSetupComplete = { name, role ->
                    scope.launch {
                        appPreferences.saveUserName(name)
                        appPreferences.saveUserRole(role)
                        appPreferences.markSetupComplete()
                        navController.navigate("home") {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("home") {
            HomeScreen(
                userName = userName ?: "",
                userRole = userRole ?: "",
                language = language ?: "ta",
                navController = navController,
                onScanClick = { 
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.navigate("scan") 
                },
                onHistoryClick = { navController.navigate("history") },
                onMenuClick = { navController.navigate("menu") }
            )
        }

        composable("menu") {
            MenuScreen(
                userName = userName ?: "",
                userRole = userRole ?: "",
                language = language ?: "ta",
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate("edit_profile") },
                onChangeLanguage = {
                    val currentLang = language ?: "ta"
                    val newLang = if (currentLang == "ta") "en" else "ta"
                    scope.launch { appPreferences.saveLanguage(newLang) }
                },
                onAboutUs = { navController.navigate("about") }
            )
        }

        // Plain scan entry — always starts fresh
        composable("scan") {
            CameraScanScreen(
                language = language ?: "ta",
                continuationScanCount = 0,
                onImageCaptured = { imageUri ->
                    val encodedUri = Uri.encode(imageUri)
                    navController.navigate("scanning_analysis/$encodedUri") {
                        popUpTo("scan") { inclusive = true }
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Continuation scan — session is kept alive, count shown as banner
        composable(
            "scan_continue/{count}",
            arguments = listOf(navArgument("count") { type = NavType.IntType })
        ) { back ->
            val count = back.arguments?.getInt("count") ?: 1
            CameraScanScreen(
                language = language ?: "ta",
                continuationScanCount = count,
                onImageCaptured = { imageUri ->
                    val encodedUri = Uri.encode(imageUri)
                    navController.navigate("scanning_analysis/$encodedUri") {
                        popUpTo("scan_continue/$count") { inclusive = true }
                    }
                },
                onBackClick = {
                    // Back during continuation = user gave up → clear session
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.navigate("home") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable(
            "scanning_analysis/{imageUri}",
            arguments = listOf(navArgument("imageUri") { type = NavType.StringType })
        ) { backStackEntry ->
            val imageUri = backStackEntry.arguments?.getString("imageUri") ?: ""
            ScanningAnalysisScreen(
                imageUri = imageUri,
                language = language ?: "ta",
                database = database,
                onDetectionComplete = { result, confidence, imageCount, isSoft ->
                    val encodedUri = Uri.encode(imageUri)
                    navController.navigate("result/$result/$confidence/$imageCount/$encodedUri?isSoft=$isSoft") {
                        popUpTo("home")
                    }
                },
                onBackClick = { 
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.popBackStack() 
                },
                onNewScan = { imageCount ->
                    if (imageCount == 0) {
                        // Fresh scan after failure — session already cleared by caller
                        navController.navigate("scan") { popUpTo("home") }
                    } else {
                        // Continuation — session KEPT, show banner with count
                        navController.navigate("scan_continue/$imageCount") { popUpTo("home") }
                    }
                }
            )
        }

        composable(
            "result/{result}/{confidence}/{imageCount}/{imageUri}?isSoft={isSoft}",
            arguments = listOf(
                navArgument("result") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.StringType },
                navArgument("imageCount") { type = NavType.IntType },
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("isSoft") { type = NavType.BoolType; defaultValue = false }
            )
        ) { entry ->
            val result = entry.arguments?.getString("result") ?: ""
            val confidenceStr = entry.arguments?.getString("confidence") ?: "0"
            val imageCount = entry.arguments?.getInt("imageCount") ?: 1
            val imageUri = entry.arguments?.getString("imageUri") ?: ""
            val isSoft = entry.arguments?.getBoolean("isSoft") ?: false
            val confidence = confidenceStr.toFloatOrNull() ?: 0f
            ResultScreen(
                result = result,
                confidence = confidence,
                imageCount = imageCount,
                isSoft = isSoft,
                imageUri = imageUri,
                language = language ?: "ta",
                navController = navController,
                onNewScan = {
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.navigate("scan") {
                        popUpTo("home")
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(
            "history_result/{result}/{confidence}/{imageUri}?timestamp={timestamp}",
            arguments = listOf(
                navArgument("result") { type = NavType.StringType },
                navArgument("confidence") { type = NavType.StringType },
                navArgument("imageUri") { type = NavType.StringType },
                navArgument("timestamp") { type = NavType.LongType; defaultValue = 0L }
            )
        ) { entry ->
            val result = entry.arguments?.getString("result") ?: ""
            val confidenceStr = entry.arguments?.getString("confidence") ?: "0"
            val imageUri = entry.arguments?.getString("imageUri") ?: ""
            val timestamp = entry.arguments?.getLong("timestamp") ?: 0L
            val confidence = confidenceStr.toFloatOrNull() ?: 0f
            HistoryResultScreen(
                result = result,
                confidence = confidence,
                imageUri = imageUri,
                timestamp = timestamp,
                language = language ?: "ta",
                navController = navController,
                onNewScan = {
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.navigate("scan") {
                        popUpTo("home")
                    }
                },
                onBackClick = { navController.popBackStack() },
                onMenuClick = { navController.navigate("menu") }
            )
        }

        composable(
            "history_result_by_id/{scanId}",
            arguments = listOf(navArgument("scanId") { type = NavType.LongType })
        ) { entry ->
            val scanId = entry.arguments?.getLong("scanId") ?: 0L
            HistoryResultScreen(
                scanId = scanId,
                database = database,
                language = language ?: "ta",
                navController = navController,
                onNewScan = {
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                    navController.navigate("scan") {
                        popUpTo("home")
                    }
                },
                onBackClick = { navController.popBackStack() },
                onMenuClick = { navController.navigate("menu") }
            )
        }

        composable("history") {
            HistoryScreen(
                language = language ?: "ta",
                database = database,
                navController = navController,
                onBackClick = { navController.popBackStack() },
                onMenuClick = { navController.navigate("menu") }
            )
        }



        composable("about") {
            AboutUsScreen(
                language = language ?: "ta",
                onBack = { navController.popBackStack() }
            )
        }

        composable("edit_profile") {
            EditProfileScreen(
                currentLanguage = language ?: "ta",
                currentName = userName ?: "",
                currentRole = userRole ?: "",
                onBack = { navController.popBackStack() },
                onSave = { newName, newRole ->
                    scope.launch {
                        appPreferences.saveUserName(newName)
                        appPreferences.saveUserRole(newRole)
                        navController.popBackStack()
                    }
                }
            )
        }

        composable("fertilizer_guide") {
            FertilizerGuideScreen(
                language = language ?: "ta",
                onBack = { navController.popBackStack() },
                onMenuClick = { navController.navigate("menu") }
            )
        }

        composable("disease_library") {
            DiseaseLibraryScreen(
                language = language ?: "ta",
                navController = navController,
                onBack = { navController.popBackStack() },
                onMenuClick = { navController.navigate("menu") }
            )
        }

        composable("disease_detail/{diseaseId}") { backStackEntry ->
            val diseaseId = backStackEntry.arguments?.getString("diseaseId") ?: ""
            DiseaseDetailScreen(
                diseaseId = diseaseId,
                language = language ?: "ta",
                onBack = { navController.popBackStack() }
            )
        }

        }
    }
}