package com.example.myapplication
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import com.example.myapplication.modelo.HistorialNoticia
import com.example.myapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun naturalizarFecha(fechaIso: String): String {
    val fecha = ZonedDateTime.parse(fechaIso)
    val formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
    return fecha.format(formatter)
}

class NewsHistoryActivity : ComponentActivity() {

    private lateinit var historyList: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news_history)

        val home = findViewById<ImageButton>(R.id.homeButton)
        home.setOnClickListener() {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        val news = findViewById<ImageButton>(R.id.newsButton)
        news.setOnClickListener() {
            val intent = Intent(this, NewsActivity::class.java)
            startActivity(intent)
        }
        val search = findViewById<ImageButton>(R.id.searchButton)
        search.setOnClickListener() {
            val intent = Intent(this, PostSearchActivity::class.java)
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

        historyList = findViewById(R.id.historyList)

        val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)

        if (idUsuario != -1) {
            cargarHistorial(idUsuario)
        } else {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
        }

    }

    private fun cargarHistorial(idUsuario: Int) {
        RetrofitClient.instance.obtenerHistorialNoticias(idUsuario)
            .enqueue(object : Callback<List<HistorialNoticia>> {
                override fun onResponse(
                    call: Call<List<HistorialNoticia>>,
                    response: Response<List<HistorialNoticia>>
                ) {
                    if (response.isSuccessful) {
                        val historial = response.body() ?: emptyList()
                        historyList.removeAllViews()

                        for (item in historial) {
                            val fechaNatural= naturalizarFecha(item.fecha_vistah)
                            val fechaFinal = fechaNatural.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            val texto = TextView(this@NewsHistoryActivity)
                            texto.text = "📌 ${item.titulo}\n📅 ${fechaFinal}\n📂 ${item.categoria}"
                            texto.textSize = 16f
                            texto.setTextColor(ContextCompat.getColor(this@NewsHistoryActivity, R.color.black))
                            texto.setPadding(10, 10, 10, 20)
                            historyList.addView(texto)
                        }
                    } else {
                        Toast.makeText(this@NewsHistoryActivity, "Error al cargar historial", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<HistorialNoticia>>, t: Throwable) {
                    Toast.makeText(this@NewsHistoryActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

}