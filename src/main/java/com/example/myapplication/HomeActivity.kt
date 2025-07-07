package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : ComponentActivity() {
    private lateinit var homeText: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val updateClock = object : Runnable {
        override fun run() {
            val today = Date()
            val dateFormat = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es", "ES"))
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale("es", "ES"))
            val fecha = dateFormat.format(today)
            val hora = timeFormat.format(today)

            homeText.text = "$fecha\n$hora"
            handler.postDelayed(this, 1000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        homeText = findViewById(R.id.homeText)
        handler.post(updateClock)

        val search = findViewById<ImageButton>(R.id.searchButton)
        search.setOnClickListener() {
            val intent = Intent(this, PostSearchActivity::class.java)
            startActivity(intent)
        }

        val news = findViewById<ImageButton>(R.id.newsButton)
        news.setOnClickListener() {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }

        val login = findViewById<ImageButton>(R.id.loginButton)
        login.setOnClickListener {
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

            val intent = if (isLoggedIn) {
                Intent(this, UserInformationActivity::class.java)
            } else {
                Intent(this, LogInActivity::class.java)
            }
            startActivity(intent)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateClock)
    }


}


