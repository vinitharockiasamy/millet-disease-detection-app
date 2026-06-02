package com.pearlmillet.app.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.with
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pearlmillet.app.data.database.AppDatabase
import com.pearlmillet.app.utils.getDiseaseTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Ultra-smooth image loader with Crossfade transition to prevent "hanging" or "jumping" UI.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun HistoryImage(
    scanId: Long,
    result: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoaded by remember { mutableStateOf(false) }
    
    // Theme is cached to prevent redundant lookups
    val theme = remember(result) { getDiseaseTheme(result, false) }

    LaunchedEffect(scanId) {
        withContext(Dispatchers.IO) {
            try {
                val scanResult = AppDatabase.getDatabase(context).scanResultDao().getScanResultById(scanId)
                if (scanResult != null && scanResult.imagePath.isNotEmpty()) {
                    val file = File(scanResult.imagePath)
                    if (file.exists()) {
                        val options = BitmapFactory.Options().apply {
                            inJustDecodeBounds = true
                        }
                        BitmapFactory.decodeFile(file.absolutePath, options)
                        
                        // Optimized sample size for smooth scrolling
                        options.inSampleSize = calculateInSampleSize(options, 120, 120)
                        options.inJustDecodeBounds = false
                        options.inPreferredConfig = Bitmap.Config.RGB_565 // Less memory, faster decoding
                        
                        val decoded = BitmapFactory.decodeFile(file.absolutePath, options)
                        
                        withContext(Dispatchers.Main) {
                            bitmap = decoded
                            isLoaded = true
                        }
                    } else {
                        withContext(Dispatchers.Main) { isLoaded = true }
                    }
                } else {
                    withContext(Dispatchers.Main) { isLoaded = true }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) { isLoaded = true }
            }
        }
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(theme.cardBackgroundColor),
        contentAlignment = Alignment.Center
    ) {
        // Directly show image when loaded, no fade effect
        val currentBitmap = bitmap
        if (currentBitmap != null) {
            Image(
                bitmap = currentBitmap.asImageBitmap(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Persistent placeholder icon until bitmap is ready
            Icon(
                imageVector = theme.icon,
                contentDescription = null,
                tint = theme.primaryColor,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.outHeight to options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}
