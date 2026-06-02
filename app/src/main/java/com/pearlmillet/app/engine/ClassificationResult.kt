package com.pearlmillet.app.engine

data class ClassificationResult(
    val predictedLabel: String,
    val confidence: Float,
    val probabilities: Map<String, Float>
)
