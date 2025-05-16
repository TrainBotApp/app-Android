package com.example.trainbot

import android.os.Bundle
import android.widget.ListView
import android.widget.TextView
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity

class AchievementsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_achievements)
        val listView = findViewById<ListView>(R.id.list_achievements)
        val achievements = AchievementManager.getAll(this)
        listView.adapter = AchievementAdapter(this, achievements)
    }
}

class AchievementAdapter(context: Context, val data: List<Pair<AchievementManager.Achievement, Boolean>>) : ArrayAdapter<Pair<AchievementManager.Achievement, Boolean>>(context, 0, data) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_achievement, parent, false)
        val (achievement, unlocked) = data[position]
        val title = view.findViewById<TextView>(R.id.text_achievement_title)
        val desc = view.findViewById<TextView>(R.id.text_achievement_desc)
        val icon = view.findViewById<ImageView>(R.id.image_achievement)
        title.text = achievement.title
        desc.text = achievement.description
        icon.setImageResource(if (unlocked) android.R.drawable.star_on else android.R.drawable.star_off)
        return view
    }
}
