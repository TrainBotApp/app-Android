package com.example.trainbot

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.app.AlertDialog
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import java.io.FileOutputStream

class TrainActivity : AppCompatActivity() {
    private val PICK_IMAGE = 1
    private var lastBitmap: Bitmap? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
            lastBitmap = bitmap
            findViewById<TextView>(R.id.text_training_status).text = "Image loaded!"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_train)

        val addDataButton = findViewById<Button>(R.id.button_add_data)
        val startTrainingButton = findViewById<Button>(R.id.button_start_training)
        val statusText = findViewById<TextView>(R.id.text_training_status)

        addDataButton.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        startTrainingButton.setOnClickListener {
            if (lastBitmap != null) {
                val input = EditText(this)
                AlertDialog.Builder(this)
                    .setTitle("Enter Label")
                    .setView(input)
                    .setPositiveButton("OK") { _, _ ->
                        val label = input.text.toString().ifBlank { "Unlabeled" }
                        val features = ImageFeatureExtractor.extractAllFeatures(lastBitmap!!)
                        val imageFile = File(filesDir, "img_${System.currentTimeMillis()}.png")
                        FileOutputStream(imageFile).use { out ->
                            lastBitmap!!.compress(Bitmap.CompressFormat.PNG, 100, out)
                        }
                        FeatureDatabase.addSample(features, label, imageFile.absolutePath)
                        FeatureDatabase.save(this)
                        statusText.text = "Sample added with label: $label"
                        AchievementManager.checkTraining(this)
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                statusText.text = "No image loaded!"
            }
        }
    }
}
