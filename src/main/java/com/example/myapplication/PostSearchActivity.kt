package com.example.myapplication

import android.os.Bundle
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ActivityPostSearchBinding
import com.example.myapplication.model.Noticia
import com.example.myapplication.network.DatabaseHelper
import com.example.myapplication.network.RetrofitClient
import kotlin.concurrent.thread
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PostSearchActivity : ComponentActivity() {

    private lateinit var etBuscar: EditText
    private lateinit var btnBuscar: Button
    private lateinit var rvResultados: RecyclerView
    private lateinit var adapter: NoticiasAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_search)

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

        etBuscar = findViewById(R.id.searchInput)
        btnBuscar = findViewById(R.id.searchButton)
        rvResultados = findViewById(R.id.results)

        adapter = NoticiasAdapter { noticia ->
            val intent = Intent(this, NewsActivity::class.java)
            intent.putExtra("idNoticia", noticia.idnoticias)
            startActivity(intent)
        }

        rvResultados.layoutManager = LinearLayoutManager(this)
        rvResultados.adapter = adapter

        btnBuscar.setOnClickListener {
            val keyword = etBuscar.text.toString().trim()
            if (keyword.isNotEmpty()) {
                buscarNoticiasPorTag(keyword)
            } else {
                Toast.makeText(this, "Ingresa una palabra clave", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun buscarNoticiasPorTag(query: String) {
        RetrofitClient.instance.buscarNoticiasPorTag(query)
            .enqueue(object : Callback<List<Noticia>> {
                override fun onResponse(call: Call<List<Noticia>>, response: Response<List<Noticia>>) {
                    if (response.isSuccessful && response.body() != null) {
                        adapter.submitList(response.body())
                    } else {
                        Toast.makeText(this@PostSearchActivity, "No se encontraron resultados", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Noticia>>, t: Throwable) {
                    Toast.makeText(this@PostSearchActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}