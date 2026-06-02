package com.pearlmillet.app.quality

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.abs

/**
 * Lightweight, offline image quality validation designed for Android.
 * Analyzes blur, lighting, and leaf visibility without relying on OpenCV.
 */
object ImageQualityAnalyzer {

    // Thresholds now centralized in DiagnosticRules
    private val BLUR_THRESHOLD = com.pearlmillet.app.engine.DiagnosticRules.BLUR_THRESHOLD
    private val DARK_THRESHOLD = com.pearlmillet.app.engine.DiagnosticRules.DARK_THRESHOLD
    private val BRIGHT_THRESHOLD = com.pearlmillet.app.engine.DiagnosticRules.BRIGHT_THRESHOLD
    private val MIN_GREEN_RATIO = com.pearlmillet.app.engine.DiagnosticRules.MIN_GREEN_RATIO

    suspend fun analyze(bitmap: Bitmap): QualityResult = withContext(Dispatchers.Default) {
        val width = bitmap.width
        val height = bitmap.height
        val totalPixels = width * height
        
        // Single array allocation for speed
        val pixels = IntArray(totalPixels)
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        var totalLuminance = 0L
        var leafPixelCount = 0
        var totalGradient = 0L

        // Single pass over pixels O(N)
        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val pixel = pixels[index]
                
                val r = (pixel shr 16) and 0xFF
                val g = (pixel shr 8) and 0xFF
                val b = pixel and 0xFF

                // 1. Luminance Calculation (Fast Approximation)
                val lum = (r * 299 + g * 587 + b * 114) / 1000
                totalLuminance += lum

                // 2. Leaf Visibility Heuristic (Green/Yellow/Brown)
                // In plant leaves, Green is typically distinctly higher than Blue.
                // We keep it extremely forgiving to avoid rejecting diseased leaves (which may be brown/yellow).
                if (g > b + 5 && g > r * 0.5f) {
                    leafPixelCount++
                }

                // 3. Blur Detection (Horizontal Gradient)
                if (x < width - 1) {
                    val rightPixel = pixels[index + 1]
                    val rr = (rightPixel shr 16) and 0xFF
                    val rg = (rightPixel shr 8) and 0xFF
                    val rb = rightPixel and 0xFF
                    val rightLum = (rr * 299 + rg * 587 + rb * 114) / 1000
                    
                    totalGradient += abs(lum - rightLum)
                }
            }
        }

        val avgLuminance = totalLuminance.toFloat() / totalPixels
        val avgGradient = totalGradient.toFloat() / (totalPixels - height) // horizontal gradients
        val leafRatio = leafPixelCount.toFloat() / totalPixels

        val issues = mutableListOf<QualityIssue>()

        // Check against thresholds
        if (avgGradient < BLUR_THRESHOLD) {
            issues.add(QualityIssue.BLURRY)
        }
        
        if (avgLuminance < DARK_THRESHOLD) {
            issues.add(QualityIssue.TOO_DARK)
        } else if (avgLuminance > BRIGHT_THRESHOLD) {
            issues.add(QualityIssue.TOO_BRIGHT)
        }

        if (leafRatio < MIN_GREEN_RATIO) {
            issues.add(QualityIssue.LOW_LEAF_VISIBILITY)
        }

        QualityResult(
            passed = issues.isEmpty(),
            issues = issues
        )
    }
}
