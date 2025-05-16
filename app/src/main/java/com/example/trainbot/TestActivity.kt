package com.example.trainbot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts

class TestActivity : AppCompatActivity() {
    private val PICK_IMAGE = 2
    private var lastBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            lastBitmap = bitmap
            findViewById<TextView>(R.id.text_inference_result).text = "Image loaded!"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val selectTestDataButton = findViewById<Button>(R.id.button_select_test_data)
        val runInferenceButton = findViewById<Button>(R.id.button_run_inference)
        val resultText = findViewById<TextView>(R.id.text_inference_result)

        FeatureDatabase.load(this)

        selectTestDataButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        runInferenceButton.setOnClickListener {
            if (lastBitmap != null) {
                // Try ML model first
                val classifier = ImageClassifier(this)
                val mlLoaded = classifier.loadModel()
                val mlResult = if (mlLoaded) classifier.classify(lastBitmap!!) else null
                classifier.close()
                if (mlResult != null && mlResult.second > 0.5f) {
                    resultText.text = "ML: ${mlResult.first} (${(mlResult.second * 100).toInt()}%)"
                } else {
                    // Fallback: feature extraction
                    val features = ImageFeatureExtractor.extractAllFeatures(lastBitmap!!)
                    val fallback = FeatureDatabase.classify(features)
                    if (fallback != null) {
                        val (label, score) = fallback
                        resultText.text = "Fallback: $label (${(score * 100).toInt()}%)"
                    } else {
                        resultText.text = "No match found."
                    }
                }
            } else {
                resultText.text = "No image loaded!"
            }
        }
    }
}
