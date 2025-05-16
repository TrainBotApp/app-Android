package com.example.trainbot

import android.graphics.Bitmap
import android.graphics.Color
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sqrt

object ImageFeatureExtractor {
    // a) Color Histogram (RGB)
    fun extractColorHistogram(bitmap: Bitmap): FloatArray {
        val bins = 256
        val histogram = FloatArray(bins * 3)
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val color = bitmap.getPixel(x, y)
                histogram[Color.red(color)] += 1f
                histogram[bins + Color.green(color)] += 1f
                histogram[2 * bins + Color.blue(color)] += 1f
            }
        }
        val total = bitmap.width * bitmap.height
        for (i in histogram.indices) histogram[i] /= total
        return histogram
    }

    // b) Edge Detection (Sobel)
    fun extractEdgeHistogram(bitmap: Bitmap): FloatArray {
        val bins = 32
        val histogram = FloatArray(bins)
        val gx = arrayOf(intArrayOf(-1, 0, 1), intArrayOf(-2, 0, 2), intArrayOf(-1, 0, 1))
        val gy = arrayOf(intArrayOf(-1, -2, -1), intArrayOf(0, 0, 0), intArrayOf(1, 2, 1))
        for (y in 1 until bitmap.height - 1) {
            for (x in 1 until bitmap.width - 1) {
                var sumX = 0f
                var sumY = 0f
                for (ky in -1..1) {
                    for (kx in -1..1) {
                        val pixel = Color.red(bitmap.getPixel(x + kx, y + ky))
                        sumX += gx[ky + 1][kx + 1] * pixel
                        sumY += gy[ky + 1][kx + 1] * pixel
                    }
                }
                val mag = sqrt(sumX * sumX + sumY * sumY)
                val bin = ((mag / 1024f) * (bins - 1)).toInt().coerceIn(0, bins - 1)
                histogram[bin] += 1f
            }
        }
        val total = (bitmap.width - 2) * (bitmap.height - 2)
        for (i in histogram.indices) histogram[i] /= total
        return histogram
    }

    // c) Texture (Local Binary Patterns)
    fun extractLBP(bitmap: Bitmap): FloatArray {
        val bins = 256
        val histogram = FloatArray(bins)
        for (y in 1 until bitmap.height - 1) {
            for (x in 1 until bitmap.width - 1) {
                val center = Color.red(bitmap.getPixel(x, y))
                var lbp = 0
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x - 1, y - 1)) > center) 1 else 0) shl 7)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x, y - 1)) > center) 1 else 0) shl 6)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x + 1, y - 1)) > center) 1 else 0) shl 5)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x + 1, y)) > center) 1 else 0) shl 4)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x + 1, y + 1)) > center) 1 else 0) shl 3)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x, y + 1)) > center) 1 else 0) shl 2)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x - 1, y + 1)) > center) 1 else 0) shl 1)
                lbp = lbp or ((if (Color.red(bitmap.getPixel(x - 1, y)) > center) 1 else 0) shl 0)
                histogram[lbp] += 1f
            }
        }
        val total = (bitmap.width - 2) * (bitmap.height - 2)
        for (i in histogram.indices) histogram[i] /= total
        return histogram
    }

    // Combine all features
    fun extractAllFeatures(bitmap: Bitmap): FloatArray {
        val color = extractColorHistogram(bitmap)
        val edge = extractEdgeHistogram(bitmap)
        val lbp = extractLBP(bitmap)
        return color + edge + lbp
    }
}
