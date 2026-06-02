package com.pearlmillet.app.data

data class Strings(
    val appName: String,
    val scanLeaf: String,
    val camera: String,
    val gallery: String,
    val detectDisease: String,
    val healthy: String,
    val blast: String,
    val downyMildew: String,
    val ergot: String,
    val rust: String,
    val smut: String,
    val howToSpray: String,
    val confidence: String,
    val history: String,
    val noHistory: String,
    val back: String,
    val newScan: String,
    val healthyCropTitle: String,
    val healthyCropMessage: String,
    val notMilletLeafTitle: String,
    val notMilletLeafMessage: String,
    val enterName: String,
    val chooseLanguage: String,
    val english: String,
    val tamil: String,
    val continueText: String,
    val welcome: String,
    val qualityBlurryTitle: String,
    val qualityBlurryMessage: String,
    val qualityDarkTitle: String,
    val qualityDarkMessage: String,
    val qualityBrightTitle: String,
    val qualityBrightMessage: String,
    val qualityNoLeafTitle: String,
    val qualityNoLeafMessage: String,
    val unableToAnalyzeTitle: String,
    val unableToAnalyzeMessage: String,
    val deleteConfirmTitle: String,
    val deleteConfirmMessage: String,
    val cancel: String,
    val delete: String,
    val recommendation: String,
    val chemicalControl: String,
    val applicationMethod: String,
    val note: String,
    val symptoms: String,
    val favourableConditions: String,
    val viewDetails: String,
    val captureAnother: String,
    val rescanLeaf: String,
    val confirmScan: String,
    val retakePhoto: String,
    val cameraGuidance: String,
    val done: String,
    val scanAnother: String,
    val sprayRecommendations: Map<String, SprayRecommendation>
)

data class SprayRecommendation(
    val title: String,
    val english: String,
    val tamil: String
)

object StringsRepository {

    val english = Strings(
        appName = "Pearl Millet Care",
        scanLeaf = "Scan Leaf",
        camera = "Camera",
        gallery = "Gallery",
        detectDisease = "Detect Disease",
        healthy = "Healthy",
        blast = "Blast",
        downyMildew = "Downy Mildew",
        ergot = "Ergot",
        rust = "Rust",
        smut = "Smut",
        howToSpray = "How to Spray?",
        confidence = "Confidence",
        history = "History",
        noHistory = "No scan history available",
        back = "Back",
        newScan = "New Scan",
        healthyCropTitle = "Healthy Crop",
        healthyCropMessage = "Your crop appears healthy. No disease symptoms detected",
        notMilletLeafTitle = "Unrecognised Image",
        notMilletLeafMessage = "This does not appear to be a pearl millet leaf. Please position the leaf clearly within the frame and scan again",
        enterName = "Enter your name",
        chooseLanguage = "Choose Language",
        english = "English",
        tamil = "தமிழ்",
        continueText = "Continue",
        welcome = "Welcome",
        qualityBlurryTitle = "Image Too Blurry",
        qualityBlurryMessage = "Hold the camera steady and ensure the leaf is in sharp focus before scanning",
        qualityDarkTitle = "Insufficient Lighting",
        qualityDarkMessage = "Move to a well-lit area or use the flash to improve image clarity",
        qualityBrightTitle = "Overexposed Image",
        qualityBrightMessage = "Reduce glare by moving away from direct sunlight or diffusing the light source",
        qualityNoLeafTitle = "Leaf Not Clearly Visible",
        qualityNoLeafMessage = "Move closer to the leaf and ensure it fills most of the frame before scanning",
        unableToAnalyzeTitle = "Image Quality Issue",
        unableToAnalyzeMessage = "• Move closer to the leaf\n• Ensure adequate lighting\n• Hold the camera steady",
        deleteConfirmTitle = "Delete Selected Records?",
        deleteConfirmMessage = "This action is permanent and cannot be undone",
        cancel = "Cancel",
        delete = "Delete",
        recommendation = "Recommendation",
        chemicalControl = "Chemical Control",
        applicationMethod = "Application Method",
        note = "Note",
        symptoms = "Symptoms",
        favourableConditions = "Favourable Conditions",
        viewDetails = "View Details",
        captureAnother = "Capture Another Photo",
        rescanLeaf = "Rescan Leaf",
        confirmScan = "Confirm Scan",
        retakePhoto = "Retake Photo",
        cameraGuidance = "Place the leaf inside the frame",
        done = "Done",
        scanAnother = "Scan Another Leaf",
        sprayRecommendations = mapOf(
            "Blast" to SprayRecommendation(
                title = "Blast Disease Treatment",
                english = "Spray with Tricyclazole 75% WP at 2g per liter of water. Repeat after 15 days if needed",
                tamil = "டிரைசைக்ளோஸோல் 75% WP ஒரு லிட்டர் தண்ணீருக்கு 2 கிராம் தெளிக்கவும். தேவைப்பட்டால் 15 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்"
            ),
            "Downy Mildew" to SprayRecommendation(
                title = "Downy Mildew Treatment",
                english = "Spray with Metalaxyl 8% + Mancozeb 64% WP at 2.5g per liter of water",
                tamil = "மெட்டாலாக்ஸில் 8% + மான்கோஜெப் 64% WP ஒரு லிட்டர் தண்ணீருக்கு 2.5 கிராம் தெளிக்கவும்"
            ),
            "Ergot" to SprayRecommendation(
                title = "Ergot Disease Treatment",
                english = "Remove infected heads and spray with Carbendazim 50% WP at 1g per liter of water",
                tamil = "பாதிக்கப்பட்ட தலைகளை அகற்றி, கார்பெண்டாஜிம் 50% WP ஒரு லிட்டர் தண்ணீருக்கு 1 கிராம் தெளிக்கவும்"
            ),
            "Rust" to SprayRecommendation(
                title = "Rust Disease Treatment",
                english = "Spray with Propiconazole 25% EC at 1ml per liter of water. Repeat after 10 days",
                tamil = "ப்ரோபிகோனாஸோல் 25% EC ஒரு லிட்டர் தண்ணீருக்கு 1 மிலி தெளிக்கவும். 10 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்"
            ),
            "Smut" to SprayRecommendation(
                title = "Smut Disease Treatment",
                english = "Treat seeds with Carbendazim 50% WP at 2g per kg seed before sowing",
                tamil = "விதைப்பதற்கு முன்பு விதைகளை கார்பெண்டாஜிம் 50% WP ஒரு கிலோ விதைக்கு 2 கிராம் சிகிச்சை செய்யவும்"
            )
        )
    )

    val tamil = Strings(
        appName = "Pearl Millet Care",
        scanLeaf = "இலையை ஸ்கேன் செய்",
        camera = "கேமரா",
        gallery = "கேலரி",
        detectDisease = "நோயை கண்டறியுங்கள்",
        healthy = "ஆரோக்கியமானது",
        blast = "பிளாஸ்ட்",
        downyMildew = "டவுனி மில்டியூ",
        ergot = "எர்காட்",
        rust = "ரஸ்ட்",
        smut = "ஸ்மட்",
        howToSpray = "மருந்து தெளிப்பது எப்படி?",
        confidence = "நம்பகத்தன்மை",
        history = "வரலாறு",
        noHistory = "ஸ்கேன் வரலாறு இல்லை",
        back = "பின்",
        newScan = "புதிய ஸ்கேன்",
        healthyCropTitle = "ஆரோக்கியமான பயிர்",
        healthyCropMessage = "உங்கள் பயிர் ஆரோக்கியமாக உள்ளது. நோய் அறிகுறிகள் எதுவும் இல்லை",
        notMilletLeafTitle = "அடையாளம் தெரியவில்லை",
        notMilletLeafMessage = "இது கம்பு இலையாகத் தெரியவில்லை. இலையை சட்டத்திற்குள் தெளிவாக வைத்து மீண்டும் முயற்சிக்கவும்",
        enterName = "உங்கள் பெயரை உள்ளிடவும்",
        chooseLanguage = "மொழியைத் தேர்ந்தெடுக்கவும்",
        english = "English",
        tamil = "தமிழ்",
        continueText = "தொடரவும்",
        welcome = "வரவேற்பு",
        qualityBlurryTitle = "படம் தெளிவற்றதாக உள்ளது",
        qualityBlurryMessage = "கேமராவை அசையாமல் பிடித்து, இலை தெளிவாக தெரியும் வரை காத்திருந்து ஸ்கேன் செய்யவும்",
        qualityDarkTitle = "வெளிச்சம் போதுமானதில்லை",
        qualityDarkMessage = "நல்ல வெளிச்சமான இடத்திற்குச் சென்று அல்லது ஃபிளாஷ் பயன்படுத்தி மீண்டும் முயற்சிக்கவும்",
        qualityBrightTitle = "அதிகமான வெளிச்சம்",
        qualityBrightMessage = "நேரடி சூரிய வெளிச்சத்தை தவிர்த்து, நிழலான இடத்தில் ஸ்கேன் செய்யவும்",
        qualityNoLeafTitle = "இலை சரியாக தெரியவில்லை",
        qualityNoLeafMessage = "கேமராவை இலையை நோக்கி கொண்டு சென்று, சட்டத்தில் நிரம்பும்படி வைத்து ஸ்கேன் செய்யவும்",
        unableToAnalyzeTitle = "படத் தரம் போதுமானதில்லை",
        unableToAnalyzeMessage = "• இலையை அருகில் கொண்டு வாருங்கள்\n• சரியான வெளிச்சம் இருக்க வேண்டும்\n• கேமராவை நிலையாக பிடிக்கவும்",
        deleteConfirmTitle = "தேர்ந்தெடுத்த பதிவுகளை நீக்கவா?",
        deleteConfirmMessage = "இந்த செயல் நிரந்தரமானது. மீட்டெடுக்க முடியாது",
        cancel = "ரத்து",
        delete = "நீக்கு",
        recommendation = "பரிந்துரை",
        chemicalControl = "இரசாயன கட்டுப்பாடு",
        applicationMethod = "பயன்படுத்தும் முறை",
        note = "குறிப்பு",
        symptoms = "அறிகுறிகள்",
        favourableConditions = "சாதகமான சூழ்நிலைகள்",
        viewDetails = "விவரங்களை காண்க",
        captureAnother = "மற்றொரு புகைப்படம் எடுக்கவும்",
        rescanLeaf = "மீண்டும் ஸ்கேன் செய்யவும்",
        confirmScan = "உறுதிப்படுத்தவும்",
        retakePhoto = "மீண்டும் எடுக்கவும்",
        cameraGuidance = "இலையை சட்டத்திற்குள் வைக்கவும்",
        done = "முடிந்தது",
        scanAnother = "மற்றொரு இலையை ஸ்கேன் செய்",
        sprayRecommendations = mapOf(
            "Blast" to SprayRecommendation(
                title = "பிளாஸ்ட் நோய் சிகிச்சை",
                english = "Spray with Tricyclazole 75% WP at 2g per liter of water. Repeat after 15 days if needed",
                tamil = "டிரைசைக்ளோஸோல் 75% WP ஒரு லிட்டர் தண்ணீருக்கு 2 கிராம் தெளிக்கவும். தேவைப்பட்டால் 15 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்"
            ),
            "Downy Mildew" to SprayRecommendation(
                title = "டவுனி மில்டியூ சிகிச்சை",
                english = "Spray with Metalaxyl 8% + Mancozeb 64% WP at 2.5g per liter of water",
                tamil = "மெட்டாலாக்ஸில் 8% + மான்கோஜெப் 64% WP ஒரு லிட்டர் தண்ணீருக்கு 2.5 கிராம் தெளிக்கவும்"
            ),
            "Ergot" to SprayRecommendation(
                title = "எர்காட் நோய் சிகிச்சை",
                english = "Remove infected heads and spray with Carbendazim 50% WP at 1g per liter of water",
                tamil = "பாதிக்கப்பட்ட தலைகளை அகற்றி, கார்பெண்டாஜிம் 50% WP ஒரு லிட்டர் தண்ணீருக்கு 1 கிராம் தெளிக்கவும்"
            ),
            "Rust" to SprayRecommendation(
                title = "ரஸ்ட் நோய் சிகிச்சை",
                english = "Spray with Propiconazole 25% EC at 1ml per liter of water. Repeat after 10 days",
                tamil = "ப்ரோபிகோனாஸோல் 25% EC ஒரு லிட்டர் தண்ணீருக்கு 1 மிலி தெளிக்கவும். 10 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்"
            ),
            "Smut" to SprayRecommendation(
                title = "ஸ்மட் நோய் சிகிச்சை",
                english = "Treat seeds with Carbendazim 50% WP at 2g per kg seed before sowing",
                tamil = "விதைப்பதற்கு முன்பு விதைகளை கார்பெண்டாஜிம் 50% WP ஒரு கிலோ விதைக்கு 2 கிராம் சிகிச்சை செய்யவும்"
            )
        )
    )

    fun getStrings(language: String): Strings {
        return when (language) {
            "ta" -> tamil
            else -> english
        }
    }
}
