package com.pearlmillet.app.tflite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import com.pearlmillet.app.utils.toSoftware
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.max

private const val TAG = "KAMBU_INFER"

class MilletClassifier(
    context: Context,
    modelName: String = "millet_disease_classifier.tflite"
) {
    private var interpreter: Interpreter? = null
    private val inputW = 224
    private val inputH = 224
    private val labels = listOf("Blast", "Downy Mildew", "Ergot", "Healthy", "Rust", "Smut")

    private var inputDataType: org.tensorflow.lite.DataType = org.tensorflow.lite.DataType.UINT8
    private var outputDataType: org.tensorflow.lite.DataType = org.tensorflow.lite.DataType.UINT8
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
            inputDataType = inputT?.dataType() ?: org.tensorflow.lite.DataType.UINT8
            
            val outT = interpreter?.getOutputTensor(0)
            outputDataType = outT?.dataType() ?: org.tensorflow.lite.DataType.UINT8
            
            val q = outT?.quantizationParams()
            outScale = q?.scale ?: 1.0f
            outZeroPoint = q?.zeroPoint ?: 0

            // Faster buffer initialization
            val bytesPerChannel = if (inputDataType == org.tensorflow.lite.DataType.FLOAT32) 4 else 1
            inputBuffer = ByteBuffer.allocateDirect(1 * inputH * inputW * 3 * bytesPerChannel)
                .order(ByteOrder.nativeOrder())
            
            val numLabels = labels.size
            val outputBytes = if (outputDataType == org.tensorflow.lite.DataType.FLOAT32) 4 else 1
            outputBuffer = ByteBuffer.allocateDirect(1 * numLabels * outputBytes).order(ByteOrder.nativeOrder())

            Log.d(TAG, "MilletClassifier loaded: inputType=$inputDataType, outputType=$outputDataType, scale=$outScale, zero=$outZeroPoint")
            Log.d(TAG, "Input Shape: ${inputT?.shape()?.contentToString()}")
            Log.d(TAG, "Output Shape: ${outT?.shape()?.contentToString()}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MilletClassifier", e)
        }
    }

    @Synchronized
    fun classifyDisease(bitmap: Bitmap): com.pearlmillet.app.engine.ClassificationResult {
        val interp = interpreter ?: return com.pearlmillet.app.engine.ClassificationResult("Error", 0f, emptyMap())
        val inBuf = inputBuffer ?: return com.pearlmillet.app.engine.ClassificationResult("Error", 0f, emptyMap())
        val outBuf = outputBuffer ?: return com.pearlmillet.app.engine.ClassificationResult("Error", 0f, emptyMap())

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

            var maxConf = -1f
            var maxIdx = 0
            val numLabels = labels.size
            val probabilities = mutableMapOf<String, Float>()
            
            for (i in 0 until numLabels) {
                val confidence = if (outputDataType == org.tensorflow.lite.DataType.FLOAT32) {
                    outBuf.float
                } else {
                    val quantized = outBuf.get().toInt() and 0xFF
                    (quantized - outZeroPoint) * outScale
                }
                
                probabilities[labels[i]] = confidence.coerceIn(0f, 1f)

                if (confidence > maxConf) {
                    maxConf = confidence
                    maxIdx = i
                }
            }

            // Cleanup if toSoftware created a new bitmap
            if (softwareBitmap != bitmap) softwareBitmap.recycle()

            val label = labels.getOrElse(maxIdx) { "Unknown" }
            return com.pearlmillet.app.engine.ClassificationResult(label, maxConf.coerceIn(0f, 1f), probabilities)

        } catch (e: Exception) {
            Log.e(TAG, "classifyDisease failed", e)
            return com.pearlmillet.app.engine.ClassificationResult("Error", 0f, emptyMap())
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