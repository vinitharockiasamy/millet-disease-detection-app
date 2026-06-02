package com.pearlmillet.app.engine

object ProbabilityAverager {

    fun averageProbabilities(results: List<ImageDiagnosticResult>): AggregatedDiagnosis {
        if (results.isEmpty()) {
            return AggregatedDiagnosis("Unknown", 0f, emptyMap(), 0, false)
        }
        
        if (results.size == 1) {
            val first = results.first()
            return AggregatedDiagnosis(
                finalLabel = first.predictedLabel,
                finalConfidence = first.confidence,
                averagedProbabilities = first.probabilities,
                imageCount = 1,
                confidenceImproved = false
            )
        }

        // 1. Collect all probability maps and sum them up
        val sumMap = mutableMapOf<String, Float>()
        for (result in results) {
            for ((label, conf) in result.probabilities) {
                sumMap[label] = sumMap.getOrDefault(label, 0f) + conf
            }
        }

        // 2. Divide by total image count
        val count = results.size.toFloat()
        val averagedMap = sumMap.mapValues { it.value / count }

        // 3. Find the maximum averaged confidence
        var finalLabel = "Unknown"
        var finalConfidence = -1f

        for ((label, conf) in averagedMap) {
            if (conf > finalConfidence) {
                finalConfidence = conf
                finalLabel = label
            }
        }

        // 4. Calculate if confidence improved compared to previous best
        val previousBestConfidence = results.dropLast(1).maxOfOrNull { it.confidence } ?: 0f
        val confidenceImproved = finalConfidence > previousBestConfidence

        return AggregatedDiagnosis(
            finalLabel = finalLabel,
            finalConfidence = finalConfidence,
            averagedProbabilities = averagedMap,
            imageCount = results.size,
            confidenceImproved = confidenceImproved
        )
    }
}
