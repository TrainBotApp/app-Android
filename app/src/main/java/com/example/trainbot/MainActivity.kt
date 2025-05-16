package com.example.trainbot

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("trainbot_prefs", MODE_PRIVATE)
        if (prefs.getString("bot_name", null) == null) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }
        setContentView(R.layout.activity_main)

        val trainButton = findViewById<MaterialButton>(R.id.button_train)
        val testButton = findViewById<MaterialButton>(R.id.button_test)

        val challengeText = findViewById<TextView?>(R.id.text_challenge)
        val streakText = findViewById<TextView?>(R.id.text_streak)
        val pointsText = findViewById<TextView?>(R.id.text_points)
        val completeChallengeButton = findViewById<Button?>(R.id.button_complete_challenge)
        challengeText?.text = ChallengeManager.getTodayChallenge(this)
        streakText?.text = "Streak: ${ChallengeManager.getStreak(this)}"
        pointsText?.text = "Points: ${ChallengeManager.getPoints(this)}"
        completeChallengeButton?.setOnClickListener {
            ChallengeManager.completeChallenge(this)
            streakText?.text = "Streak: ${ChallengeManager.getStreak(this)}"
            pointsText?.text = "Points: ${ChallengeManager.getPoints(this)}"
            completeChallengeButton.isEnabled = false
            AchievementManager.checkChallenge(this)
        }

        val knowledgeBaseButton = findViewById<Button?>(R.id.button_knowledge_base)
        knowledgeBaseButton?.setOnClickListener {
            startActivity(Intent(this, KnowledgeBaseActivity::class.java))
        }

        val achievementsButton = findViewById<Button?>(R.id.button_achievements)
        achievementsButton?.setOnClickListener {
            startActivity(Intent(this, AchievementsActivity::class.java))
        }

        trainButton.setOnClickListener {
            startActivity(Intent(this, TrainActivity::class.java))
        }
        testButton.setOnClickListener {
            startActivity(Intent(this, TestActivity::class.java))
        }
    }
}
