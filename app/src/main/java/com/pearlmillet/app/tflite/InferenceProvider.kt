package com.pearlmillet.app.tflite

import android.content.Context
import android.util.Log

/**
 * Singleton provider for ML models to prevent expensive re-initialization.
 */
object InferenceProvider {
    private const val TAG = "InferenceProvider"
    
    private var detector: MilletDetector? = null
    private var classifier: MilletClassifier? = null

    @Synchronized
    fun getDetector(context: Context): MilletDetector {
        if (detector == null) {
            Log.d(TAG, "Initializing singleton MilletDetector")
            detector = MilletDetector(context.applicationContext)
        }
        return detector!!
    }

    @Synchronized
    fun getClassifier(context: Context): MilletClassifier {
        if (classifier == null) {
            Log.d(TAG, "Initializing singleton MilletClassifier")
            classifier = MilletClassifier(context.applicationContext)
        }
        return classifier!!
    }

    /**
     * Call this when the app is being destroyed or under extreme memory pressure.
     */
    @Synchronized
    fun closeAll() {
        Log.d(TAG, "Closing all ML models")
        detector?.close()
        classifier?.close()
        detector = null
        classifier = null
    }
}
