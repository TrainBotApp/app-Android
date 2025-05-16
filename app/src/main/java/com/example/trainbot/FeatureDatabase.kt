package com.example.trainbot

import android.graphics.Bitmap
import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

object FeatureDatabase {
    // For knowledge base: store image path and label
    data class Entry(val imagePath: String, val label: String, val features: FloatArray) : java.io.Serializable
    private val db = mutableListOf<Entry>()

    fun addSample(features: FloatArray, label: String, imagePath: String) {
        db.add(Entry(imagePath, label, features))
    }

    fun save(context: Context) {
        val file = File(context.filesDir, "feature_db.bin")
        ObjectOutputStream(FileOutputStream(file)).use { it.writeObject(db) }
    }

    fun load(context: Context) {
        val file = File(context.filesDir, "feature_db.bin")
        if (!file.exists()) return
        ObjectInputStream(file.inputStream()).use {
            @Suppress("UNCHECKED_CAST")
            val loaded = it.readObject() as? MutableList<Entry>
            if (loaded != null) {
                db.clear()
                db.addAll(loaded)
            }
        }
    }

    fun loadAll(context: Context): MutableList<Entry> {
        val file = File(context.filesDir, "feature_db.bin")
        if (!file.exists()) return db
        ObjectInputStream(file.inputStream()).use {
            @Suppress("UNCHECKED_CAST")
            val loaded = it.readObject() as? MutableList<Entry>
            if (loaded != null) {
                db.clear()
                db.addAll(loaded)
            }
        }
        return db
    }

    fun deleteSample(entry: Entry) {
        db.remove(entry)
    }

    fun updateLabel(entry: Entry, newLabel: String) {
        val idx = db.indexOf(entry)
        if (idx != -1) db[idx] = entry.copy(label = newLabel)
    }

    fun classify(features: FloatArray): Pair<String, Float>? {
        if (db.isEmpty()) return null
        var bestLabel = "Unknown"
        var bestScore = -1.0f
        for (entry in db) {
            val score = cosineSimilarity(features, entry.features)
            if (score > bestScore) {
                bestScore = score
                bestLabel = entry.label
            }
        }
        return bestLabel to bestScore
    }

    private fun cosineSimilarity(a: FloatArray, b: FloatArray): Float {
        var dot = 0f
        var normA = 0f
        var normB = 0f
        for (i in a.indices) {
            dot += a[i] * b[i]
            normA += a[i] * a[i]
            normB += b[i] * b[i]
        }
        return if (normA == 0f || normB == 0f) 0f else (dot / (kotlin.math.sqrt(normA) * kotlin.math.sqrt(normB)))
    }
}
