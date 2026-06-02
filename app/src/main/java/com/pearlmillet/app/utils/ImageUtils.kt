package com.pearlmillet.app.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import android.graphics.Matrix
import org.tensorflow.lite.DataType

private const val INPUT_SIZE = 224

/**
 * Converts a Hardware Bitmap (from CameraX/ImageDecoder) to a Software Bitmap (ARGB_8888).
 * TFLite requires software bitmaps to access underlying pixel arrays.
 */
fun Bitmap.toSoftware(): Bitmap {
    if (this.config != Bitmap.Config.HARDWARE) return this
    return this.copy(Bitmap.Config.ARGB_8888, false)
}


/**
 * Saves a bitmap to the device's public gallery (Pictures/Pearl Millet Care).
 * Used when the farmer confirms a captured image — ensures the photo is preserved.
 */
fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String, description: String): String? {
    val filename = "$title.jpg"
    var fos: OutputStream? = null
    var imageUri: Uri? = null

    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + File.separator + "Pearl Millet Care"
                )
                put(MediaStore.Images.Media.TITLE, title)
                put(MediaStore.Images.Media.DESCRIPTION, description)
            }
            imageUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues
            )
            imageUri?.let { fos = context.contentResolver.openOutputStream(it) }
        } else {
            val appDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "Pearl Millet Care"
            )
            if (!appDir.exists()) appDir.mkdirs()
            val imageFile = File(appDir, filename)
            fos = FileOutputStream(imageFile)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, title)
                put(MediaStore.Images.Media.DESCRIPTION, description)
                @Suppress("DEPRECATION")
                put(MediaStore.Images.Media.DATA, imageFile.absolutePath)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            }
            imageUri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
            )
        }
        fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        imageUri?.toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Saves a bitmap to the app's private internal storage for use in history.
 * Returns the absolute file path, or null on failure.
 */
fun saveImageToInternalStorage(context: Context, bitmap: Bitmap): String? {
    val filename = "scan_${System.currentTimeMillis()}.jpg"
    return try {
        val file = File(context.filesDir, filename)
        FileOutputStream(file).use { fos ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        }
        file.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * Prepares a bitmap for ML inference by scaling and center-cropping to [inputW] x [inputH].
 * Avoids duplicate allocations where possible.
 */
fun prepareBitmapForInference(bitmap: Bitmap, inputW: Int = 224, inputH: Int = 224): Bitmap {
    if (bitmap.width == inputW && bitmap.height == inputH) return bitmap

    val matrix = Matrix()
    val scale = max(inputW / bitmap.width.toFloat(), inputH / bitmap.height.toFloat())
    matrix.postScale(scale, scale)

    val scaled = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

    return if (scaled.width == inputW && scaled.height == inputH) {
        scaled
    } else {
        val x = (scaled.width - inputW) / 2
        val y = (scaled.height - inputH) / 2
        val cropped = Bitmap.createBitmap(scaled, x, y, inputW, inputH)
        if (scaled != bitmap) scaled.recycle()
        cropped
    }
}

/**
 * Writes bitmap pixels into a pre-allocated TFLite [ByteBuffer].
 * Supports both FLOAT32 (normalized 0–1) and UINT8 (quantized) model inputs.
 *
 * Call [buffer].rewind() before running inference.
 */
fun putPixelsToBuffer(
    bitmap: Bitmap,
    buffer: ByteBuffer,
    pixelArray: IntArray,
    inputW: Int = 224,
    inputH: Int = 224,
    inputDataType: DataType
) {
    bitmap.getPixels(pixelArray, 0, inputW, 0, 0, inputW, inputH)
    for (pixel in pixelArray) {
        val r = (pixel shr 16 and 0xFF)
        val g = (pixel shr 8 and 0xFF)
        val b = (pixel and 0xFF)

        if (inputDataType == DataType.FLOAT32) {
            buffer.putFloat(r / 255f)
            buffer.putFloat(g / 255f)
            buffer.putFloat(b / 255f)
        } else {
            buffer.put(r.toByte())
            buffer.put(g.toByte())
            buffer.put(b.toByte())
        }
    }
}
