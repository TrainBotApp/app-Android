package com.example.trainbot

import android.content.Context
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.random.Random

object ChallengeManager {
    private val challenges = listOf(
        "Train your bot with a new image!",
        "Test your bot with a new photo!",
        "Organize your knowledge base!",
        "Label 3 new images today!",
        "Achieve 2 correct recognitions!",
        "Review your bot's categories!",
        "Share your bot's progress with a friend!",
        "Try training with a different lighting condition!",
        "Add a fun fact to your knowledge base!",
        "Test your bot on an object it hasn't seen before!",
        "Update your bot's avatar!",
        "Check your achievement progress!",
        "Try to beat your challenge streak!",
        "Give your bot a new name for today!",
        "Teach your bot something new!",
        "Take a photo outside for training!",
        "Organize your images into folders!",
        "Try to get 5 correct recognitions in a row!",
        "Add a new category to your bot!",
        "Test your bot with a friend's help!"
    )
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun getTodayChallenge(context: Context): String {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        val today = LocalDate.now().format(formatter)
        val lastDate = prefs.getString("challenge_date", null)
        val lastChallenge = prefs.getString("challenge_text", null)
        val dayOfYear = LocalDate.now().dayOfYear
        val challenge = challenges[dayOfYear % challenges.size]
        return if (lastDate == today && lastChallenge != null) {
            lastChallenge
        } else {
            prefs.edit().putString("challenge_date", today)
                .putString("challenge_text", challenge)
                .apply()
            challenge
        }
    }

    fun completeChallenge(context: Context) {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        val today = LocalDate.now().format(formatter)
        val lastCompleted = prefs.getString("challenge_completed_date", null)
        var streak = prefs.getInt("challenge_streak", 0)
        var points = prefs.getInt("challenge_points", 0)
        if (lastCompleted != today) {
            streak = if (lastCompleted == LocalDate.now().minusDays(1).format(formatter)) streak + 1 else 1
            points += 10 // Award 10 points per challenge
            prefs.edit()
                .putString("challenge_completed_date", today)
                .putInt("challenge_streak", streak)
                .putInt("challenge_points", points)
                .apply()
        }
    }

    fun getStreak(context: Context): Int {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("challenge_streak", 0)
    }

    fun getPoints(context: Context): Int {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        return prefs.getInt("challenge_points", 0)
    }
}
