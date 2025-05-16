package com.example.trainbot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class OnboardingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        val botNameInput = findViewById<EditText>(R.id.edit_bot_name)
        val avatarImage = findViewById<ImageView>(R.id.image_avatar)
        val continueButton = findViewById<Button>(R.id.button_continue)
        val welcomeText = findViewById<TextView>(R.id.text_welcome)

        // Set the default avatar image to the app icon
        avatarImage.setImageResource(R.mipmap.icon)

        // Optionally: let user pick avatar (not implemented here)
        welcomeText.text = "Welcome! Name your bot to get started."

        continueButton.setOnClickListener {
            val botName = botNameInput.text.toString().ifBlank { "TrainBot" }
            val prefs = getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
            prefs.edit().putString("bot_name", botName).apply()
            // Optionally: save avatar selection
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}
