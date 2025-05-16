package com.example.trainbot

import android.content.Context

object AchievementManager {
    private val achievements = listOf(
        Achievement("First Training", "Add your first training image!", "first_training"),
        Achievement("First Challenge", "Complete your first daily challenge!", "first_challenge"),
        Achievement("Streak 3", "Complete 3 daily challenges in a row!", "streak_3"),
        Achievement("Streak 7", "Complete 7 daily challenges in a row!", "streak_7"),
        Achievement("Knowledge Collector", "Add 10 images to your knowledge base!", "knowledge_10")
    )

    data class Achievement(val title: String, val description: String, val key: String)

    fun unlock(context: Context, key: String) {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("ach_$key", true).apply()
    }

    fun isUnlocked(context: Context, key: String): Boolean {
        val prefs = context.getSharedPreferences("trainbot_prefs", Context.MODE_PRIVATE)
        return prefs.getBoolean("ach_$key", false)
    }

    fun getAll(context: Context): List<Pair<Achievement, Boolean>> =
        achievements.map { it to isUnlocked(context, it.key) }

    // Call these from relevant places in the app:
    fun checkTraining(context: Context) {
        if (!isUnlocked(context, "first_training")) unlock(context, "first_training")
        val count = FeatureDatabase.loadAll(context).size
        if (count >= 10 && !isUnlocked(context, "knowledge_10")) unlock(context, "knowledge_10")
    }
    fun checkChallenge(context: Context) {
        if (!isUnlocked(context, "first_challenge")) unlock(context, "first_challenge")
        val streak = ChallengeManager.getStreak(context)
        if (streak >= 3 && !isUnlocked(context, "streak_3")) unlock(context, "streak_3")
        if (streak >= 7 && !isUnlocked(context, "streak_7")) unlock(context, "streak_7")
    }
}
