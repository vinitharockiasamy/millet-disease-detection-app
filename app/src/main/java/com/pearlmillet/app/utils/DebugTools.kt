package com.pearlmillet.app.utils

import android.util.Log
import com.pearlmillet.app.BuildConfig
import com.pearlmillet.app.engine.DiagnosticSession

object DebugTools {
    private const val TAG = "MilletDebug"

    fun logSessionState(session: DiagnosticSession?) {
        if (!BuildConfig.DEBUG) return
        
        session?.let {
            Log.d(TAG, "=== SESSION DEBUG ===")
            Log.d(TAG, "Image Count: ${it.imageResults.size}")
            
            it.imageResults.lastOrNull()?.let { last ->
                Log.d(TAG, "PREDICTION: ${last.predictedLabel} (${String.format("%.4f", last.confidence)})")
                Log.d(TAG, "VALIDATION: ${last.validationState}")
                Log.d(TAG, "ALL PROBS: ${last.probabilities}")
            }
            Log.d(TAG, "======================")
        }
    }

    fun logInferenceTime(label: String, timeMs: Long) {
        if (!BuildConfig.DEBUG) return
        Log.d(TAG, "Inference for $label took ${timeMs}ms")
    }

    // Local failure logging for dataset improvement
    fun logFailureLocally(reason: String, metadata: Map<String, Any>) {
        // In a real app, this might write to a local file or a hidden DB table
        // For now, we log it with a specific prefix for easy extraction
        Log.e("FIELD_TEST_FAILURE", "Reason: $reason | Data: $metadata")
    }
}
