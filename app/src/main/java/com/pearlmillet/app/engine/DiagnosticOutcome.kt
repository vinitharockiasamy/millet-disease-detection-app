package com.pearlmillet.app.engine

import com.pearlmillet.app.quality.QualityIssue

sealed class DiagnosticOutcome {
    data class Success(
        val label: String, 
        val confidence: Float,
        val imageCount: Int = 1
    ) : DiagnosticOutcome()

    data class SoftSuccess(
        val label: String,
        val confidence: Float,
        val imageCount: Int
    ) : DiagnosticOutcome()
    
    data class NeedsMoreImages(
        val currentLabel: String,
        val confidence: Float,
        val probabilities: Map<String, Float>,
        val imageCount: Int
    ) : DiagnosticOutcome()

    data class UncertainDiagnosis(val message: String) : DiagnosticOutcome()
    
    data class QualityFailure(val issue: QualityIssue) : DiagnosticOutcome()
    data class MilletFailure(val message: String) : DiagnosticOutcome()
    data class HealthyCrop(
        val label: String,
        val confidence: Float,
        val imageCount: Int = 1
    ) : DiagnosticOutcome()
    data class SystemFailure(val error: String) : DiagnosticOutcome()
}
