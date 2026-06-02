package com.pearlmillet.app.utils

import android.graphics.Bitmap
import android.util.Log

/**
 * Singleton manager to hold bitmaps in memory during a scan session.
 * This prevents large Navigation argument passes and eliminates redundant disk I/O.
 */
object ScanSessionManager {
    private const val TAG = "ScanSessionManager"

    var currentSession: com.pearlmillet.app.engine.DiagnosticSession? = null

    @Synchronized
    fun createSession() {
        Log.d(TAG, "Creating new DiagnosticSession")
        clearSession()
        currentSession = com.pearlmillet.app.engine.DiagnosticSession(
            sessionId = java.util.UUID.randomUUID().toString(),
            createdAt = System.currentTimeMillis(),
            originalBitmap = null,
            mlBitmap = null,
            imageResults = mutableListOf()
        )
    }

    @Synchronized
    fun clearSession() {
        Log.d(TAG, "Clearing DiagnosticSession")
        currentSession?.originalBitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
        currentSession?.mlBitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
        currentSession = null
    }

    @Synchronized
    fun clearOriginalOnly() {
        Log.d(TAG, "Clearing original high-res bitmap only to free memory")
        currentSession?.originalBitmap?.let {
            if (!it.isRecycled) {
                it.recycle()
            }
        }
        currentSession?.originalBitmap = null
    }

    @Synchronized
    fun getCurrentImage(): Bitmap? {
        return currentSession?.originalBitmap
    }

    @Synchronized
    fun addImageResult(result: com.pearlmillet.app.engine.ImageDiagnosticResult) {
        currentSession?.imageResults?.add(result)
    }

    // Alias for backward compatibility during refactor if needed
    @Synchronized
    fun clear() {
        clearSession()
    }
}
