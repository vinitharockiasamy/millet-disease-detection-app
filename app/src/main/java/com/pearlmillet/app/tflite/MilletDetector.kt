package com.pearlmillet.app.tflite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.pearlmillet.app.utils.toSoftware
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max

private const val TAG = "KAMBU_INFER"

class MilletDetector(
    context: Context,
    modelName: String = "millet_detector.tflite"
) {
    private var interpreter: Interpreter? = null
    private val inputW = 224
    private val inputH = 224
    
    private var inputDataType: DataType = DataType.UINT8
    private var outputDataType: DataType = DataType.UINT8
    
    private var outScale: Float = 1.0f
    private var outZeroPoint: Int = 0

    // Reuse buffers to reduce GC pressure
    private var inputBuffer: ByteBuffer? = null
    private var outputBuffer: ByteBuffer? = null
    private val pixelArray = IntArray(inputW * inputH)

    init {
        try {
            val modelBuf = loadModel(context, modelName)
            val opt = Interpreter.Options().apply {
                setNumThreads(4)
                useXNNPACK = true
            }
            interpreter = Interpreter(modelBuf, opt)

            val inputT = interpreter?.getInputTensor(0)
            inputDataType = inputT?.dataType() ?: DataType.UINT8
            
            val outT = interpreter?.getOutputTensor(0)
            outputDataType = outT?.dataType() ?: DataType.UINT8
            
            val q = outT?.quantizationParams()
            outScale = q?.scale ?: 1.0f
            outZeroPoint = q?.zeroPoint ?: 0

            // Faster buffer initialization
            val bytesPerChannel = if (inputDataType == DataType.FLOAT32) 4 else 1
            inputBuffer = ByteBuffer.allocateDirect(1 * inputH * inputW * 3 * bytesPerChannel)
                .order(ByteOrder.nativeOrder())
            
            val outputBytes = if (outputDataType == DataType.FLOAT32) 4 else 1
            outputBuffer = ByteBuffer.allocateDirect(1 * outputBytes).order(ByteOrder.nativeOrder())

            Log.d(TAG, "MilletDetector loaded → input=$inputDataType, output=$outputDataType scale=$outScale zp=$outZeroPoint")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MilletDetector", e)
        }
    }

    @Synchronized
    fun detectMillet(bitmap: Bitmap): Float {
        val interp = interpreter ?: return 0f
        val inBuf = inputBuffer ?: return 0f
        val outBuf = outputBuffer ?: return 0f

        try {
            val softwareBitmap = bitmap.toSoftware()
            
            inBuf.rewind()
            com.pearlmillet.app.utils.putPixelsToBuffer(
                bitmap = softwareBitmap,
                buffer = inBuf,
                pixelArray = pixelArray,
                inputW = inputW,
                inputH = inputH,
                inputDataType = inputDataType
            )
            inBuf.rewind()

            outBuf.rewind()
            interp.run(inBuf, outBuf)
            outBuf.rewind()

            val confidence = if (outputDataType == DataType.FLOAT32) {
                outBuf.float
            } else {
                val quantized = outBuf.get().toInt() and 0xFF
                (quantized - outZeroPoint) * outScale
            }

            // Cleanup if toSoftware created a new bitmap
            if (softwareBitmap != bitmap) softwareBitmap.recycle()

            return confidence.coerceIn(0f, 1f)

        } catch (e: Exception) {
            Log.e(TAG, "detectMillet failed", e)
            return 0f
        }
    }



    private fun loadModel(context: Context, name: String): MappedByteBuffer {
        val fd = context.assets.openFd(name)
        return FileInputStream(fd.fileDescriptor).use { fis ->
            fis.channel.map(FileChannel.MapMode.READ_ONLY, fd.startOffset, fd.declaredLength)
        }
    }

    fun close() {
        try { 
            interpreter?.close() 
            inputBuffer = null
            outputBuffer = null
        } catch (t: Throwable) {}
    }
}
