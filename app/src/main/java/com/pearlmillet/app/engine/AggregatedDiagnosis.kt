package com.pearlmillet.app.engine

data class AggregatedDiagnosis(
    val finalLabel: String,
    val finalConfidence: Float,
    val averagedProbabilities: Map<String, Float>,
    val imageCount: Int,
    val confidenceImproved: Boolean
)
