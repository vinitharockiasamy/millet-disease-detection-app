package com.pearlmillet.app.data

data class DiseaseData(
    val id: String,
    val name: String,
    val botanicalName: String,
    val symptoms: List<String>,
    val favourableConditions: String,
    val treatment: List<String>,
    val application: String,
    val notes: String
)

object DiseaseMetadata {

    fun getDiseaseData(diseaseId: String, language: String): DiseaseData {

        val isTamil = language == "ta"

        return when (diseaseId) {

            "blast" -> DiseaseData(

                id = "blast",

                name = if (isTamil)
                    "குலை நோய்"
                else
                    "Blast Disease",

                botanicalName = if (isTamil)
                    "மேக்னபோர்தா கிரைசியா"
                else
                    "Magnaporthe grisea",

                symptoms = if (isTamil) listOf(

                    "நீள்வட்ட அல்லது வைர வடிவிலான சாம்பல் நிறப் புள்ளிகள் பெரும்பாலும் மஞ்சள் ஒளிவட்டத்தால் சூழப்பட்டிருக்கும்.",

                    "புள்ளிகள் ஒன்றோடொன்று இணைந்து இலையின் திசுக்கள் கருகி காணப்படும்."

                ) else listOf(

                    "Elliptical or diamond-shaped greyish lesions on leaves, often surrounded by a chlorotic halo.",

                    "Lesions may coalesce and cause necrosis of leaf tissues."

                ),

                favourableConditions = if (isTamil)

                    "அதிக ஈரப்பதம் (90% க்கும் மேல்), மிதமான வெப்பநிலை (25–30°C) மற்றும் மேகமூட்டமான வானிலை."

                else

                    "High relative humidity (>90%), moderate temperature (25–30°C), and cloudy weather.",

                treatment = if (isTamil) listOf(

                    "நோயின் அறிகுறி தென்பட்டவுடன் கார்பண்டாசிம் 50% WP @ 250 கிராம்/எக்டர் அளவில் தெளிக்கவும்.",

                    "தேவைப்பட்டால் 10 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்."

                ) else listOf(

                    "Spray Carbendazim 50% WP @ 250 g/ha at the appearance of disease symptoms.",

                    "Repeat after 10 days if necessary."

                ),

                application = if (isTamil)

                    "கைத்தெளிப்பான் மூலம் இலைகள் நன்கு நனையும்படி தெளிக்க வேண்டும்."

                else

                    "Apply as a foliar spray using a knapsack sprayer, ensuring complete coverage of the foliage.",

                notes = if (isTamil)

                    "அதிக அளவு தழைச்சத்து (நைட்ரஜன்) உரங்களை பயன்படுத்துவதைத் தவிர்க்கவும்."

                else

                    "Avoid excessive application of nitrogenous fertilizers."

            )


            "downy_mildew" -> DiseaseData(

                id = "downy_mildew",

                name = if (isTamil)
                    "அடிச்சாம்பல் நோய்"
                else
                    "Downy Mildew",

                botanicalName = if (isTamil)
                    "ஸ்க்ளிரோஸ்போரா கிராமினிகோலா"
                else
                    "Sclerospora graminicola",

                symptoms = if (isTamil) listOf(

                    "இலையின் அடிப்பகுதியில் வெண்மை நிற பூஞ்சை வளர்ச்சி மற்றும் இலையின் மேற்பரப்பில் வெளிறிய கோடுகள் அல்லது வெளிர் பச்சை நிற மாற்றத் திட்டுகள் காணப்படும்.",

                    "கதிரின் பூக்கள் பகுதியாகவோ அல்லது முழுமையாகவோ பச்சை இலை போன்ற அமைப்புகளாக மாறும் (பசுங்கதிர்)."

                ) else listOf(

                    "White downy fungal growth on the lower surface of leaves and chlorotic streaks or light green discoloured patches on the upper surface.",

                    "Florets of the inflorescence become partially or completely transformed into green leafy structures (green ear)."

                ),

                favourableConditions = if (isTamil)

                    "அதிக ஈரப்பதம் (90% க்கும் மேல்), மழை தூறும் வானிலை மற்றும் 20–23°C வெப்பநிலை."

                else

                    "High relative humidity (>90%), drizzling weather, and temperatures between 20–23°C.",

                treatment = if (isTamil) listOf(

                    "விதைப்பதற்கு முன் மெட்டாலாக்சில் 35% WS @ 6 கிராம்/கிலோ விதை என்ற அளவில் விதை நேர்த்தி செய்யவும்.",

                    "மெட்டாலாக்சில் 18% + மேன்கோசெப் 64% WP @ 500 கிராம்/எக்டர் அல்லது மேன்கோசெப் 75% WP @ 1000 கிராம்/எக்டர் என்ற அளவில் தெளிக்கவும்."

                ) else listOf(

                    "Treat seeds with Metalaxyl 35% WS @ 6 g/kg of seed before sowing.",

                    "Spray Metalaxyl 18% + Mancozeb 64% WP @ 500 g/ha or Mancozeb 75% WP @ 1000 g/ha."

                ),

                application = if (isTamil)

                    "நோய் அறிகுறிகள் தென்பட்டவுடன் உடனடியாக தெளிக்க வேண்டும்."

                else

                    "Spray immediately after noticing the initial disease symptoms.",

                notes = if (isTamil)

                    "நோய் எதிர்ப்பு சக்தி கொண்ட ரகங்களைப் பயிரிட பரிந்துரைக்கப்படுகிறது."

                else

                    "Cultivate disease-resistant varieties."

            )


            "ergot" -> DiseaseData(
                id = "ergot",
                name = if (isTamil) "தேன் ஒழுகல் நோய்" else "Ergot Disease",
                botanicalName = if (isTamil) "கிளாவிசெப்ஸ் ஃபுசிபார்மிஸ்" else "Claviceps fusiformis",
                symptoms = if (isTamil) listOf(
                    "கதிரில் பாதிக்கப்பட்ட பூக்களில் இருந்து சிறிய தேன் போன்ற துளிகள் ஒழுகும்.",
                    "பாதிக்கப்பட்ட பூவின் கருவானது அடர் பழுப்பு நிறத்தில் இருந்து கருப்பு நிற ஸ்கிளிரோசியாவாக வெளிநோக்கி வளரும்."
                ) else listOf(
                    "Exudation of small honeydew-like mucilaginous droplets from infected spikelets.",
                    "The infected ovary develops into a dark brown to black sclerotium projecting out of the spikelet."
                ),
                favourableConditions = if (isTamil)

                    "பூக்கும் பருவத்தில் அதிக ஈரப்பதம் (80% க்கும் மேல்) மற்றும் தொடர்ச்சியான மழை (20-30°C) நோய் பரவ உதவும்."

                else

                    "High relative humidity (>80%) and continuous rainfall (20–30°C) during the flowering stage favour disease development.",

                treatment = if (isTamil) listOf(
                    "ஒரு எக்டருக்கு கார்பண்டாசிம் 50% WP @ 500 கிராம் அல்லது மான்கோசெப் 75% WP @ 1000 கிராம் என்ற அளவில் 5–10% பூக்கும் நிலையிலும் மீண்டும் 50% பூக்கும் நிலையிலும் தெளிக்கவும்.",
                    "பாதிக்கப்பட்ட கதிர்களை அகற்றி அழிக்கவும்."
                ) else listOf(
                    "Spray Carbendazim 50% WP @ 500 g/ha or Mancozeb 75% WP @ 1000 g/ha at 5–10% flowering and again at 50% flowering stage.",
                    "Remove and destroy infected heads."
                ),
                application = if (isTamil) "பூக்கும் தருணத்தில் முதல் தெளிப்பும், 10 நாட்கள் கழித்து இரண்டாவது தெளிப்பும் கொடுக்க வேண்டும்." else "Apply the first spray at the onset of flowering, followed by a second spray after 10 days.",
                notes = if (isTamil) "பாதிக்கப்பட்ட தானியங்களை கால்நடைகளுக்கு உணவாகக் கொடுக்கக் கூடாது." else "Ergot-infected grains are toxic and should not be fed to cattle."
            )


            "rust" -> DiseaseData(

                id = "rust",

                name = if (isTamil)
                    "துரு நோய்"
                else
                    "Rust Disease",

                botanicalName = if (isTamil)
                    "பக்சினியா சப்ஸ்ட்ரையாட்டா"
                else
                    "Puccinia substriata",

                symptoms = if (isTamil) listOf(

                    "இலைகளின் இருபுறங்களிலும் செம்பழுப்பு நிற பூஞ்சை வித்துக்கள் சற்றே உயர்ந்த புள்ளிகளாகத் தோன்றும்.",

                    "நோய் தீவிரமானால் இலைகள் முன்கூட்டியே காய்ந்து வாடிவிடும்.",

                    "இலை உறை, தண்டு மற்றும் மஞ்சரி தண்டுகளிலும் கொப்புளங்கள் உருவாகலாம்."

                ) else listOf(

                    "Reddish-brown pustules appear predominantly on both surfaces of leaves.",

                    "Severe infection leads to premature drying and withering of leaves.",

                    "Pustules may also develop on leaf sheaths, stems, and peduncles."

                ),

                favourableConditions = if (isTamil)

                    "குளிர்ச்சியான இரவு வெப்பநிலை (20–23°C), அதிக பனித்துளி உருவாக்கம் மற்றும் ஈரப்பதமான மழைக்கால வானிலை நோய் பரவ உதவும்."

                else

                    "Cool night temperatures (20–23°C), heavy dew formation, and humid rainy weather favour disease development.",

                treatment = if (isTamil) listOf(

                    "நோயின் அறிகுறிகள் தென்பட்டவுடன் நனைந்த கந்தகம் 80% WP @ 2500 கிராம்/எக்டர் அல்லது மான்கோசெப் 75% WP @ 1000 கிராம்/எக்டர் என்ற அளவில் தெளிக்கவும்.",

                    "தேவைப்பட்டால் 10–15 நாட்களுக்குப் பிறகு மீண்டும் தெளிக்கவும்."

                ) else listOf(

                    "Spray Wettable Sulphur 80% WP @ 2500 g/ha or Mancozeb 75% WP @ 1000 g/ha at the appearance of disease symptoms.",

                    "Repeat after 10–15 days if necessary."

                ),

                application = if (isTamil)

                    "இலைகள் முழுவதும் நனையும் வகையில் கைத்தெளிப்பான் மூலம் தெளிக்க வேண்டும்."

                else

                    "Apply as a foliar spray, ensuring thorough coverage of the foliage.",

                notes = if (isTamil)

                    "களைகளை கட்டுப்படுத்துவதன் மூலம் நோய் பரவலைக் குறைக்கலாம்."

                else

                    "Controlling weed hosts can help reduce disease spread."

            )



            "smut" -> DiseaseData(

                id = "smut",

                name = if (isTamil)
                    "கரிப்பூட்டை நோய்"
                else
                    "Smut Disease",

                botanicalName = if (isTamil)
                    "டோலிபோஸ்போரியம் பெனிசில்லேரியே"
                else
                    "Tolyposporium penicillariae",

                symptoms = if (isTamil) listOf(

                    "கதிரிலுள்ள சில மணிகள் கருப்பு பூஞ்சை வித்துக்கள் கொண்ட கூடுகளாக மாறும்.",

                    "பாதிக்கப்பட்ட மணிகள் முதலில் பச்சை நிறத்திலும் அளவில் பெரிதாகவும் இருந்து பின்னர் கருப்பு நிறமாக மாறும்."

                ) else listOf(

                    "A few florets in the earhead transform into enlarged sori containing smut spores.",

                    "Infected grains are initially enlarged and green in colour, later turning black."

                ),

                favourableConditions = if (isTamil)

                    "பூக்கும் பருவத்தில் அதிக ஈரப்பதம் (80–95%) மற்றும் தொடர்ச்சியான மழை (25-35°C) நோய் பரவ உதவும்."

                else

                    "High relative humidity (80–95%) and continuous rainfall (25–35°C) during the flowering stage favour disease development.",

                treatment = if (isTamil) listOf(

                    "கதிர் இலை பருவத்தில் சினெப் 75% WP @ 1000 கிராம்/எக்டர் என்ற அளவில் தெளிக்கவும்.",

                    "பாதிக்கப்பட்ட கதிர்களை சேகரித்து அழிக்கவும்."

                ) else listOf(

                    "Spray Zineb 75% WP @ 1000 g/ha at the boot leaf stage.",

                    "Collect and destroy infected earheads."

                ),

                application = if (isTamil)

                    "இது பாதிக்கப்பட்ட விதைகள் மற்றும் வயல்வெளி மூலமாக பரவக்கூடிய நோய் என்பதால் விதை நேர்த்தி மிகவும் முக்கியமானது."

                else

                    "Seed treatment is important as the disease can spread through infected seeds and field inoculum.",

                notes = if (isTamil)

                    "பயிர் சுழற்சி முறையைப் பின்பற்ற பரிந்துரைக்கப்படுகிறது."

                else

                    "Follow proper crop rotation practices."

            )


            else -> DiseaseData(
                id = "unknown",
                name = if (isTamil) "தகவல் இல்லை" else "Unknown",
                botanicalName = "",
                symptoms = emptyList(),
                favourableConditions = "",
                treatment = emptyList(),
                application = "",
                notes = ""
            )
        }
    }
}
