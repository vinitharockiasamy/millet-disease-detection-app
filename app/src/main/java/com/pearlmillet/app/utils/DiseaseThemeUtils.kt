package com.pearlmillet.app.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.pearlmillet.app.ui.theme.AppColors
import java.util.Locale

data class DiseaseTheme(
    val primaryColor: Color,
    val secondaryColor: Color,
    val cardBackgroundColor: Color,
    val icon: ImageVector
)

fun getDiseaseTheme(result: String, isTamil: Boolean): DiseaseTheme {
    val lowerResult = result.lowercase(Locale.ROOT)
    return when {
        lowerResult.contains("blast") -> DiseaseTheme(
            primaryColor = Color(0xFFFF1744),
            secondaryColor = Color(0xFFB71C1C),
            cardBackgroundColor = Color(0xFFFFEBEE),
            icon = Icons.Default.Warning
        )
        lowerResult.contains("rust") -> DiseaseTheme(
            primaryColor = Color(0xFFFF3D00),
            secondaryColor = Color(0xFFBF360C),
            cardBackgroundColor = Color(0xFFFFF3E0),
            icon = Icons.Default.Warning
        )
        lowerResult.contains("downy") || lowerResult.contains("mildew") -> DiseaseTheme(
            primaryColor = Color(0xFF76FF03),
            secondaryColor = Color(0xFF558B2F),
            cardBackgroundColor = Color(0xFFF1F8E9),
            icon = Icons.Default.Warning
        )
        lowerResult.contains("ergot") -> DiseaseTheme(
            primaryColor = Color(0xFFD500F9),
            secondaryColor = Color(0xFF4A148C),
            cardBackgroundColor = Color(0xFFF3E5F5),
            icon = Icons.Default.Warning
        )
        lowerResult.contains("smut") -> DiseaseTheme(
            primaryColor = Color(0xFF2979FF),
            secondaryColor = Color(0xFF263238),
            cardBackgroundColor = Color(0xFFE3F2FD),
            icon = Icons.Default.Warning
        )
        lowerResult.contains("healthy") -> DiseaseTheme(
            primaryColor = Color(0xFF00E676),
            secondaryColor = Color(0xFF1B5E20),
            cardBackgroundColor = Color(0xFFE8F5E9),
            icon = Icons.Default.CheckCircle
        )
        lowerResult.contains("not millet") || lowerResult.contains("invalid") -> DiseaseTheme(
            primaryColor = Color(0xFFB0BEC5),
            secondaryColor = Color(0xFF263238),
            cardBackgroundColor = Color(0xFFECEFF1),
            icon = Icons.Default.BrokenImage
        )
        else -> DiseaseTheme(
            primaryColor = AppColors.DeepGreen,
            secondaryColor = AppColors.DeepGreen,
            cardBackgroundColor = Color(0xFFFAFAFA),
            icon = Icons.Default.Info
        )
    }
}

data class DiseaseInfo(
    val name: String,
    val botanicalName: String,
    val description: String
)

fun getDiseaseId(result: String): String? {
    val lowerResult = result.lowercase(Locale.ROOT)
    return when {
        lowerResult.contains("blast") -> "blast"
        lowerResult.contains("rust") -> "rust"
        lowerResult.contains("downy") || lowerResult.contains("mildew") -> "downy_mildew"
        lowerResult.contains("ergot") -> "ergot"
        lowerResult.contains("smut") -> "smut"
        else -> null
    }
}

fun getDiseaseInfo(result: String, language: String): DiseaseInfo {
    val isTamil = language == "ta"
    val lowerResult = result.lowercase(Locale.ROOT)
    return when {
        lowerResult.contains("blast") -> DiseaseInfo(
            name = if (isTamil) "குலை நோய்" else "Blast Disease",
            botanicalName = if (isTamil) "மேக்னபோர்தா கிரைசியா" else "Magnaporthe grisea",
            description = if (isTamil) 
                "இலைகளில் கண் அல்லது வைர வடிவ புள்ளிகள் தோன்றும். கதிர்களில் மணி பிடிப்பு பாதிக்கப்படும்."
                else "Fungal disease causing diamond-shaped lesions on leaves and severe damage to the earhead."
        )
        lowerResult.contains("rust") -> DiseaseInfo(
            name = if (isTamil) "துரு நோய்" else "Rust Disease",
            botanicalName = if (isTamil) "பச்சினியா சப்ஸ்ட்ரியாட்டா" else "Puccinia substriata",
            description = if (isTamil) 
                "இலைகளில் செம்பழுப்பு நிறப் புள்ளிகள் தோன்றும். இலைகள் காய்ந்துவிடும்." 
                else "Reddish-brown pustules on leaves, causing premature drying and yield loss."
        )
        lowerResult.contains("downy") || lowerResult.contains("mildew") -> DiseaseInfo(
            name = if (isTamil) "அடிச்சாம்பல் நோய்" else "Downy Mildew",
            botanicalName = if (isTamil) "ஸ்க்ளிரோஸ்போரா கிராமினிகோலா" else "Sclerospora graminicola",
            description = if (isTamil) 
                "இலைகள் வெளுத்து காணப்படும். கதிர்கள் பச்சை இலை போன்ற அமைப்பாக மாறும் (Green Ear)."
                else "Causes chlorotic streaks on leaves and transformation of floral parts into leafy structures (Green Ear)."
        )
        lowerResult.contains("ergot") -> DiseaseInfo(
            name = if (isTamil) "தேன் ஒழுகல் நோய்" else "Ergot Disease",
            botanicalName = if (isTamil) "கிளாவிசெப்ஸ் ஃபுசிபார்மிஸ்" else "Claviceps fusiformis",
            description = if (isTamil) 
                "கதிர்களிலிருந்து தேன் போன்ற திரவம் வடியும். மணிகள் கருப்பாக மாறும்." 
                else "Pinkish sticky liquid (honeydew) exudes from florets, later turning into hard black sclerotia."
        )
        lowerResult.contains("smut") -> DiseaseInfo(
            name = if (isTamil) "கரிப்பூட்டை நோய்" else "Smut Disease",
            botanicalName = if (isTamil) "டோலிபோஸ்போரியம் பெனிசில்லேரியே" else "Tolyposporium penicillariae",
            description = if (isTamil) 
                "கதிர்களில் உள்ள மணிகள் உப்பி, கருப்பு நிறப் பொடியாக மாறும்." 
                else "Grains are transformed into sori containing black powdery spores."
        )
        lowerResult.contains("healthy") -> DiseaseInfo(
            name = if (isTamil) "ஆரோக்கியமான பயிர்" else "Healthy Crop",
            botanicalName = "Pennisetum glaucum",
            description = if (isTamil) 
                "உங்கள் பயிர் ஆரோக்கியமாக உள்ளது. நோய் அறிகுறிகள் இல்லை." 
                else "Your crop looks healthy! No disease symptoms detected."
        )
        lowerResult.contains("not millet") || lowerResult.contains("invalid") -> DiseaseInfo(
            name = if (isTamil) "கம்பு இலை அல்ல" else "Not Millet Leaf",
            botanicalName = "",
            description = if (isTamil) 
                "ஸ்கேன் செய்யப்பட்ட படம் கம்பு இலையாகத் தெரியவில்லை. தயவுசெய்து மீண்டும் முயற்சிக்கவும்." 
                else "The scanned image does not appear to be a Pearl Millet leaf. Please try again."
        )
        else -> DiseaseInfo(
            name = result,
            botanicalName = "",
            description = if (isTamil) "விவரங்கள் கிடைக்கவில்லை." else "No details available."
        )
    }
}
