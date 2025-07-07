package com.example.myapplication


import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult.InformationElement
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.model.NoticiaNueva
import com.example.myapplication.network.RetrofitClient
import java.time.LocalDate
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublicationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_publication)

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

        val etTitulo = findViewById<EditText>(R.id.titleInput)
        val etContenido = findViewById<EditText>(R.id.contentInput)
        val etCategoria = findViewById<EditText>(R.id.categoryInput)
        val etAutor = findViewById<EditText>(R.id.nameInput)
        val btnPublicar = findViewById<Button>(R.id.postButton)

        btnPublicar.setOnClickListener {
            val titulo = etTitulo.text.toString().trim()
            val contenido = etContenido.text.toString().trim()
            val categoria = etCategoria.text.toString().trim()
            val autor = etAutor.text.toString().trim()
            val fechaHoy = LocalDate.now().toString()

            if (titulo.isEmpty() || contenido.isEmpty() || categoria.isEmpty() || autor.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val noticia = NoticiaNueva(
                titulo = titulo,
                contenido = contenido,
                categoria = categoria,
                autor = autor,
                fecha_publicacion = fechaHoy,
                LIKES = 0
            )

            RetrofitClient.instance.publicarNoticia(noticia)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@PublicationActivity, "Noticia publicada", Toast.LENGTH_SHORT).show()
                            finish() // o limpiar campos
                        } else {
                            Toast.makeText(this@PublicationActivity, "Error al publicar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@PublicationActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

    }
}