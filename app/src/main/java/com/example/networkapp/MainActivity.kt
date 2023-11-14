package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import android.content.SharedPreferences
import android.widget.CheckBox
import androidx.core.widget.addTextChangedListener
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.lang.StringBuilder
import java.lang.Exception


// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save and load comic info automatically when app starts)


private const val AUTO_SAVE_KEY = "auto_save"


class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView


    private var autoSave = false

    private lateinit var preferences: SharedPreferences

    private val internalFilename = "save_to_file"

    private lateinit var file: File


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        preferences = getPreferences(MODE_PRIVATE)

        // Create file reference for app-specific file
        file = File(filesDir, internalFilename)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        showButton = findViewById<Button>(R.id.showComicButton)

        // Read last saved value from preferences, or false if no value saved
        autoSave = preferences.getBoolean(AUTO_SAVE_KEY, false)


        showButton.setOnClickListener() {
            // Update shared preferences when toggled
            val editor = preferences.edit()
            editor.putBoolean(AUTO_SAVE_KEY, autoSave)
            editor.apply()
        }


        requestQueue = Volley.newRequestQueue(this)
        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)
        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }


    }

    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url, {showComic(it)}, {
            })
        )
    }

    private fun showComic (comicObject: JSONObject) {

        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)

    }

    private fun saveComic () {

        if (autoSave && file.exists()) {
            try {
                val br = BufferedReader(FileReader(file))
                val text = StringBuilder()
                var line: String?
                while (br.readLine().also { line = it } != null) {
                    text.append(line)
                    text.append('\n')
                }
                br.close()
                titleTextView.text = text.toString()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }



    }


}