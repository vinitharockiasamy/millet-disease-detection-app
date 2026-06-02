package com.pearlmillet.app.engine

import android.graphics.Bitmap

data class DiagnosticSession(
    val sessionId: String,
    val createdAt: Long,
    var originalBitmap: Bitmap?,
    var mlBitmap: Bitmap?,
    var imageResults: MutableList<ImageDiagnosticResult> = mutableListOf()
)

data class ImageDiagnosticResult(
    val imageIndex: Int,
    val probabilities: Map<String, Float>,
    val predictedLabel: String,
    val confidence: Float,
    val detectorConfidence: Float,
    val validationState: String // e.g., "Passed", "QualityFailure", "MilletFailure"
)
