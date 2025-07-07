package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.myapplication.modelo.NasaNoticia
import com.example.myapplication.network.NasaApiService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class NasaApodActivity : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var tvExplicacion: TextView
    private lateinit var imgNasa: ImageView
    private lateinit var tvAutor: TextView
    private lateinit var tvFecha: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nasa_apod)

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


        tvTitulo = findViewById(R.id.tvTitulo)
        tvExplicacion = findViewById(R.id.tvExplicacion)
        imgNasa = findViewById(R.id.imgNasa)
        tvAutor = findViewById(R.id.tvAutor)
        tvFecha = findViewById(R.id.tvFecha)

        cargarNoticiaDelDia()
    }

    private fun cargarNoticiaDelDia() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(NasaApiService::class.java)

        service.getNasaNoticia().enqueue(object : Callback<NasaNoticia> {
            override fun onResponse(call: Call<NasaNoticia>, response: Response<NasaNoticia>) {
                if (response.isSuccessful && response.body() != null) {
                    val nasa = response.body()!!
                    tvTitulo.text = nasa.title
                    tvAutor.text = "NASA"
                    tvFecha.text = "${nasa.date}"
                    tvExplicacion.text = nasa.explanation

                    if (nasa.media_type == "image") {
                        imgNasa.load(nasa.url)
                        imgNasa.visibility = View.VISIBLE
                    } else {
                        imgNasa.visibility = View.GONE
                        tvExplicacion.text = "(No es imagen)\n${nasa.explanation}"
                    }
                } else {
                    Toast.makeText(this@NasaApodActivity, "Error al cargar la noticia", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NasaNoticia>, t: Throwable) {
                Toast.makeText(this@NasaApodActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}