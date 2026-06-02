package com.pearlmillet.app.engine

import android.content.Context
import com.pearlmillet.app.quality.ImageQualityAnalyzer
import com.pearlmillet.app.tflite.InferenceProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Test version of the DiagnosticEngine to easily test multi-image flows.
 * Switch to this in ScanningAnalysisScreen.kt to test 3-image confirmation.
 */
object DiagnosticTest {

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
                return@withContext DiagnosticOutcome.QualityFailure(issue)
            }

            // 2. Load Models
            val detector = InferenceProvider.getDetector(context)
            val classifier = InferenceProvider.getClassifier(context)

            // 3. Millet Presence Check
            val milletConf = detector.detectMillet(bitmap)

            // 4. Disease Classification
            val classification = classifier.classifyDisease(bitmap)

            // Add real result to session so we have data
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

            // ─── TEMPORARY TEST MOCK FOR 3-IMAGE FLOW ───
            val outcome = when (imageCount) {
                1 -> {
                    // Force the app to ask for a second image
                    DiagnosticOutcome.NeedsMoreImages(
                        currentLabel = aggregated.finalLabel,
                        confidence = 0.75f, // Mocked medium confidence
                        probabilities = aggregated.averagedProbabilities,
                        imageCount = imageCount
                    )
                }
                2 -> {
                    // Follow real flow: stop if confident, ask for 3rd if not
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
                    // On the 3rd image, let it complete normally
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
            // ──────────────────────────────────────────────

            return@withContext outcome

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext DiagnosticOutcome.SystemFailure(e.message ?: "Unknown error")
        }
    }
}
