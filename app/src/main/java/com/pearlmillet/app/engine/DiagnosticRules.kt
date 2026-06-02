package com.pearlmillet.app.engine

object DiagnosticRules {
    const val MILLET_THRESHOLD = 0.35f
    const val HIGH_CONFIDENCE_THRESHOLD = 0.90f
    const val MEDIUM_CONFIDENCE_THRESHOLD = 0.65f
    const val BLUR_THRESHOLD = 5.0f
    const val DARK_THRESHOLD = 40.0f
    const val BRIGHT_THRESHOLD = 230.0f
    const val MIN_GREEN_RATIO = 0.005f
    const val HEALTHY_LABEL = "Healthy"
    const val NOT_MILLET_MESSAGE = "Not Millet Leaf"
}
