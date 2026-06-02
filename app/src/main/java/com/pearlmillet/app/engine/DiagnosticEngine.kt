package com.pearlmillet.app.engine

import android.content.Context
import com.pearlmillet.app.quality.ImageQualityAnalyzer
import com.pearlmillet.app.tflite.InferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DiagnosticEngine {

    suspend fun analyzeSession(
        context: Context,
        session: DiagnosticSession
    ): DiagnosticOutcome = withContext(Dispatchers.Default) {
        try {
            val bitmap = session.mlBitmap ?: return@withContext DiagnosticOutcome.SystemFailure("No ML Bitmap found in session")

            // 1. Quality Validation
            val qualityResult = ImageQualityAnalyzer.analyze(bitmap)
            if (!qualityResult.passed) {
                val issue = qualityResult.issues.first()
                session.imageResults.add(
                    ImageDiagnosticResult(
                        imageIndex = session.imageResults.size,
                        probabilities = emptyMap(),
                        predictedLabel = "Unknown",
                        confidence = 0f,
                        detectorConfidence = 0f,
                        validationState = "QualityFailure: $issue"
                    )
                )
                return@withContext DiagnosticOutcome.QualityFailure(issue)
            }

            // 2. Load Models
            val detector = InferenceProvider.getDetector(context)
            val classifier = InferenceProvider.getClassifier(context)

            // 3. Millet Presence Check
            val milletConf = detector.detectMillet(bitmap)

            if (milletConf > DiagnosticRules.MILLET_THRESHOLD) {
                // 4. Disease Classification
                val classification = classifier.classifyDisease(bitmap)

                session.imageResults.add(
                    ImageDiagnosticResult(
                        imageIndex = session.imageResults.size,
                        probabilities = classification.probabilities,
                        predictedLabel = classification.predictedLabel,
                        confidence = classification.confidence,
                        detectorConfidence = milletConf,
                        validationState = "Passed"
                    )
                )

                val validResults = session.imageResults.filter { it.validationState == "Passed" }
                val aggregated = ProbabilityAverager.averageProbabilities(validResults)
                val imageCount = validResults.size

                val outcome = when (imageCount) {
                    1 -> {
                        if (aggregated.finalConfidence >= DiagnosticRules.HIGH_CONFIDENCE_THRESHOLD) {
                            if (aggregated.finalLabel.equals(DiagnosticRules.HEALTHY_LABEL, ignoreCase = true)) {
                                DiagnosticOutcome.HealthyCrop(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            } else {
                                DiagnosticOutcome.Success(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            }
                        } else if (aggregated.finalConfidence >= DiagnosticRules.MEDIUM_CONFIDENCE_THRESHOLD) {
                            DiagnosticOutcome.NeedsMoreImages(
                                currentLabel = aggregated.finalLabel,
                                confidence = aggregated.finalConfidence,
                                probabilities = aggregated.averagedProbabilities,
                                imageCount = imageCount
                            )
                        } else {
                            DiagnosticOutcome.UncertainDiagnosis("Unable to confirm disease clearly.")
                        }
                    }
                    2 -> {
                        if (aggregated.finalConfidence >= 0.85f) {
                            if (aggregated.finalLabel.equals(DiagnosticRules.HEALTHY_LABEL, ignoreCase = true)) {
                                DiagnosticOutcome.HealthyCrop(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            } else {
                                DiagnosticOutcome.Success(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            }
                        } else {
                            DiagnosticOutcome.NeedsMoreImages(
                                currentLabel = aggregated.finalLabel,
                                confidence = aggregated.finalConfidence,
                                probabilities = aggregated.averagedProbabilities,
                                imageCount = imageCount
                            )
                        }
                    }
                    else -> {
                        if (aggregated.finalConfidence >= 0.80f) {
                            if (aggregated.finalLabel.equals(DiagnosticRules.HEALTHY_LABEL, ignoreCase = true)) {
                                DiagnosticOutcome.HealthyCrop(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            } else {
                                DiagnosticOutcome.SoftSuccess(aggregated.finalLabel, aggregated.finalConfidence, imageCount)
                            }
                        } else {
                            DiagnosticOutcome.UncertainDiagnosis("Unable to confidently confirm disease.")
                        }
                    }
                }

                // Session Failure Reset: After 3 failed uncertain attempts, clear session
                if (imageCount >= 3 && outcome is DiagnosticOutcome.UncertainDiagnosis) {
                    com.pearlmillet.app.utils.ScanSessionManager.clearSession()
                }

                return@withContext outcome

            } else {
                session.imageResults.add(
                    ImageDiagnosticResult(
                        imageIndex = session.imageResults.size,
                        probabilities = emptyMap(),
                        predictedLabel = "Unknown",
                        confidence = 0f,
                        detectorConfidence = milletConf,
                        validationState = "MilletFailure"
                    )
                )
                return@withContext DiagnosticOutcome.MilletFailure(DiagnosticRules.NOT_MILLET_MESSAGE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext DiagnosticOutcome.SystemFailure(e.message ?: "Unknown error")
        }
    }
}
