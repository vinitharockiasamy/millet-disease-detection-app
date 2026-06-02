package com.pearlmillet.app.ui.preview

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.navigation.compose.rememberNavController
import com.pearlmillet.app.ui.components.DiagnosticProgressCard
import com.pearlmillet.app.ui.screens.*
import com.pearlmillet.app.ui.theme.PearlMilletCareTheme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

// ─────────────────────────────────────────────────────────────────────────────
//  Preview Utilities
// ─────────────────────────────────────────────────────────────────────────────

private class LangProvider : PreviewParameterProvider<String> {
    override val values = sequenceOf("en", "ta")
}

// ─────────────────────────────────────────────────────────────────────────────
//  WELCOME SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Welcome — English", showBackground = true, showSystemUi = true)
@Composable
private fun WelcomePreviewEn() {
    PearlMilletCareTheme {
        WelcomeScreen(onStartClick = {})
    }
}

@Preview(name = "Welcome — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun WelcomePreviewTa() {
    PearlMilletCareTheme {
        WelcomeScreen(onStartClick = {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  LANGUAGE SELECT SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Language Select", showBackground = true, showSystemUi = true)
@Composable
private fun LanguageSelectPreview() {
    PearlMilletCareTheme {
        LanguageSelectScreen(onLanguageSelected = {})
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  FIRST TIME SETUP SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "First Setup — English", showBackground = true, showSystemUi = true)
@Composable
private fun FirstSetupPreviewEn() {
    PearlMilletCareTheme {
        FirstTimeSetupScreen(
            currentLanguage = "en",
            onSetupComplete = { _, _ -> },
            onBackClick = {}
        )
    }
}

@Preview(name = "First Setup — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun FirstSetupPreviewTa() {
    PearlMilletCareTheme {
        FirstTimeSetupScreen(
            currentLanguage = "ta",
            onSetupComplete = { _, _ -> },
            onBackClick = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  HOME SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Home — English", showBackground = true, showSystemUi = true)
@Composable
private fun HomePreviewEn() {
    PearlMilletCareTheme {
        HomeScreenContent(
            userName = "Ravi Kumar",
            language = "en",
            weatherData = WeatherDataState("--", "Loading...", Icons.Default.Cloud),
            recentScans = emptyList(),
            navController = rememberNavController(),
            onScanClick = {},
            onHistoryClick = {},
            onMenuClick = {}
        )
    }
}

@Preview(name = "Home — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun HomePreviewTa() {
    PearlMilletCareTheme {
        HomeScreenContent(
            userName = "ரவி குமார்",
            language = "ta",
            weatherData = WeatherDataState("--", "Loading...", Icons.Default.Cloud),
            recentScans = emptyList(),
            navController = rememberNavController(),
            onScanClick = {},
            onHistoryClick = {},
            onMenuClick = {}
        )
    }
}



// ─────────────────────────────────────────────────────────────────────────────
//  RESULT SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Result — Blast Disease EN", showBackground = true, showSystemUi = true)
@Composable
private fun ResultBlastEn() {
    PearlMilletCareTheme {
        ResultScreen(
            result = "Blast",
            confidence = 0.92f,
            imageCount = 2,
            isSoft = false,
            imageUri = "",
            language = "en",
            navController = rememberNavController(),
            onNewScan = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Result — Blast Disease TA", showBackground = true, showSystemUi = true)
@Composable
private fun ResultBlastTa() {
    PearlMilletCareTheme {
        ResultScreen(
            result = "Blast",
            confidence = 0.92f,
            imageCount = 2,
            isSoft = false,
            imageUri = "",
            language = "ta",
            navController = rememberNavController(),
            onNewScan = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Result — Downy Mildew Soft EN", showBackground = true, showSystemUi = true)
@Composable
private fun ResultDownySoftEn() {
    PearlMilletCareTheme {
        ResultScreen(
            result = "Downy Mildew",
            confidence = 0.81f,
            imageCount = 3,
            isSoft = true,
            imageUri = "",
            language = "en",
            navController = rememberNavController(),
            onNewScan = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Result — Healthy EN", showBackground = true, showSystemUi = true)
@Composable
private fun ResultHealthyEn() {
    PearlMilletCareTheme {
        ResultScreen(
            result = "Healthy",
            confidence = 0.97f,
            imageCount = 1,
            isSoft = false,
            imageUri = "",
            language = "en",
            navController = rememberNavController(),
            onNewScan = {},
            onBackClick = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  MENU SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Menu — English", showBackground = true, showSystemUi = true)
@Composable
private fun MenuPreviewEn() {
    PearlMilletCareTheme {
        MenuScreen(
            userName = "Dr.Vinith",
            userRole = "Professional",
            language = "en",
            onBack = {},
            onEditProfile = {},
            onChangeLanguage = {},
            onAboutUs = {}
        )
    }
}

@Preview(name = "Menu — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun MenuPreviewTa() {
    PearlMilletCareTheme {
        MenuScreen(
            userName = "ரவி குமார்",
            userRole = "விவசாயி",
            language = "ta",
            onBack = {},
            onEditProfile = {},
            onChangeLanguage = {},
            onAboutUs = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  EDIT PROFILE SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Edit Profile — English", showBackground = true, showSystemUi = true)
@Composable
private fun EditProfilePreviewEn() {
    PearlMilletCareTheme {
        EditProfileScreen(
            currentLanguage = "en",
            currentName = "Dr.Vinith",
            currentRole = "Professional",
            onBack = {},
            onSave = { _, _ -> }
        )
    }
}

@Preview(name = "Edit Profile — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun EditProfilePreviewTa() {
    PearlMilletCareTheme {
        EditProfileScreen(
            currentLanguage = "ta",
            currentName = "ரவி குமார்",
            currentRole = "விவசாயி",
            onBack = {},
            onSave = { _, _ -> }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DISEASE LIBRARY SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Disease Library — English", showBackground = true, showSystemUi = true)
@Composable
private fun DiseaseLibraryPreviewEn() {
    PearlMilletCareTheme {
        DiseaseLibraryScreen(
            language = "en",
            navController = rememberNavController(),
            onBack = {},
            onMenuClick = {}
        )
    }
}

@Preview(name = "Disease Library — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun DiseaseLibraryPreviewTa() {
    PearlMilletCareTheme {
        DiseaseLibraryScreen(
            language = "ta",
            navController = rememberNavController(),
            onBack = {},
            onMenuClick = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  DISEASE DETAIL SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Disease Detail — Blast EN", showBackground = true, showSystemUi = true)
@Composable
private fun DiseaseDetailBlastEn() {
    PearlMilletCareTheme {
        DiseaseDetailScreen(
            diseaseId = "blast",
            language = "en",
            onBack = {}
        )
    }
}

@Preview(name = "Disease Detail — Blast TA", showBackground = true, showSystemUi = true)
@Composable
private fun DiseaseDetailBlastTa() {
    PearlMilletCareTheme {
        DiseaseDetailScreen(
            diseaseId = "blast",
            language = "ta",
            onBack = {}
        )
    }
}

@Preview(name = "Disease Detail — Downy Mildew EN", showBackground = true, showSystemUi = true)
@Composable
private fun DiseaseDetailDownyEn() {
    PearlMilletCareTheme {
        DiseaseDetailScreen(
            diseaseId = "downy_mildew",
            language = "en",
            onBack = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  FERTILIZER GUIDE SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "Fertilizer Guide — English", showBackground = true, showSystemUi = true)
@Composable
private fun FertilizerPreviewEn() {
    PearlMilletCareTheme {
        FertilizerGuideScreen(
            language = "en",
            onBack = {},
            onMenuClick = {}
        )
    }
}

@Preview(name = "Fertilizer Guide — Tamil", showBackground = true, showSystemUi = true)
@Composable
private fun FertilizerPreviewTa() {
    PearlMilletCareTheme {
        FertilizerGuideScreen(
            language = "ta",
            onBack = {},
            onMenuClick = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  ABOUT US SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "About Us — English", showBackground = true, showSystemUi = true)
@Composable
private fun AboutUsPreviewEn() {
    PearlMilletCareTheme {
        AboutUsScreen(
            language = "en",
            onBack = {}
        )
    }
}

@Preview(name = "About Us — Tamil", showBackground = true, showSystemUi = true,widthDp = 360, heightDp = 1800)
@Composable
private fun AboutUsPreviewTa() {
    PearlMilletCareTheme {
        AboutUsScreen(
            language = "ta",
            onBack = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  HISTORY SCREEN
// ─────────────────────────────────────────────────────────────────────────────

@Preview(name = "History — Empty EN", showBackground = true, showSystemUi = true)
@Composable
private fun HistoryPreviewEmptyEn() {
    PearlMilletCareTheme {
        HistoryScreenContent(
            language = "en",
            allScanResults = emptyList(),
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

@Preview(name = "History — Empty TA", showBackground = true, showSystemUi = true)
@Composable
private fun HistoryPreviewEmptyTa() {
    PearlMilletCareTheme {
        HistoryScreenContent(
            language = "ta",
            allScanResults = emptyList(),
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

@Preview(name = "History — Delete Confirm Dialog EN", showBackground = true, showSystemUi = true)
@Composable
private fun HistoryDeleteDialogEn() {
    PearlMilletCareTheme {
        HistoryScreenContent(
            language = "en",
            allScanResults = emptyList(),
            isLoading = false,
            isSelectionMode = true,
            selectedIds = setOf(1L),
            showDeleteConfirmation = true,   // shows the delete confirm dialog
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

@Preview(name = "History — Delete Confirm Dialog TA", showBackground = true, showSystemUi = true)
@Composable
private fun HistoryDeleteDialogTa() {
    PearlMilletCareTheme {
        HistoryScreenContent(
            language = "ta",
            allScanResults = emptyList(),
            isLoading = false,
            isSelectionMode = true,
            selectedIds = setOf(1L),
            showDeleteConfirmation = true,   // shows the delete confirm dialog
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

