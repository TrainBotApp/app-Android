package com.example.trainbot

import android.content.Context
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.sqrt

class ImageClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private val inputImageSize = 224
    private val modelFileName = "mobilenet_v2.tflite"
    private var modelLoaded = false

    fun loadModel(): Boolean {
        return try {
            val model = FileUtil.loadMappedFile(context, modelFileName)
            gpuDelegate = GpuDelegate()
            val options = Interpreter.Options().addDelegate(gpuDelegate)
            interpreter = Interpreter(model, options)
            modelLoaded = true
            true
        } catch (e: Exception) {
            modelLoaded = false
            false
        }
    }

    fun classify(bitmap: Bitmap): Pair<String, Float>? {
        if (!modelLoaded) return null
        val resized = Bitmap.createScaledBitmap(bitmap, inputImageSize, inputImageSize, true)
        val input = preprocessImage(resized)
        val output = Array(1) { FloatArray(1001) }
        interpreter?.run(input, output)
        val maxIdx = output[0].indices.maxByOrNull { output[0][it] } ?: -1
        val confidence = output[0][maxIdx]
        val label = getLabel(maxIdx)
        return label to confidence
    }

    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * inputImageSize * inputImageSize * 3)
        buffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(inputImageSize * inputImageSize)
        bitmap.getPixels(intValues, 0, inputImageSize, 0, 0, inputImageSize, inputImageSize)
        for (pixel in intValues) {
            val r = ((pixel shr 16) and 0xFF) / 255.0f
            val g = ((pixel shr 8) and 0xFF) / 255.0f
            val b = (pixel and 0xFF) / 255.0f
            buffer.putFloat(r)
            buffer.putFloat(g)
            buffer.putFloat(b)
        }
        buffer.rewind()
        return buffer
    }

    private fun getLabel(index: Int): String {
        // TODO: Load labels from file if available
        return "Class $index"
    }

    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
    }
}
