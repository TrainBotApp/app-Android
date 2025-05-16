package com.example.trainbot

import android.content.Context
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class KnowledgeBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_knowledge_base)

        val listView = findViewById<ListView>(R.id.list_knowledge)
        val db = FeatureDatabase.loadAll(this)
        val adapter = KnowledgeAdapter(this, db)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val entry = db[position]
            val options = arrayOf("Edit Label", "Delete Entry")
            AlertDialog.Builder(this)
                .setTitle("Manage Entry")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> editLabel(entry, adapter)
                        1 -> {
                            FeatureDatabase.deleteSample(entry)
                            adapter.remove(entry)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
                .show()
        }
    }

    private fun editLabel(entry: FeatureDatabase.Entry, adapter: KnowledgeAdapter) {
        val input = EditText(this)
        input.setText(entry.label)
        AlertDialog.Builder(this)
            .setTitle("Edit Label")
            .setView(input)
            .setPositiveButton("OK") { _, _ ->
                FeatureDatabase.updateLabel(entry, input.text.toString())
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

class KnowledgeAdapter(context: Context, val data: MutableList<FeatureDatabase.Entry>) : ArrayAdapter<FeatureDatabase.Entry>(context, 0, data) {
    override fun getView(position: Int, convertView: android.view.View?, parent: android.view.ViewGroup): android.view.View {
        val view = convertView ?: android.view.LayoutInflater.from(context).inflate(R.layout.item_knowledge, parent, false)
        val entry = data[position]
        val imageView = view.findViewById<ImageView>(R.id.image_entry)
        val labelView = view.findViewById<TextView>(R.id.text_label)
        val file = File(entry.imagePath)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            imageView.setImageBitmap(bitmap)
        }
        labelView.text = entry.label
        return view
    }
}
