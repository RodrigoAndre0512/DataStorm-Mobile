package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.Comentario
import com.example.myapplication.model.ComentarioRequest
import com.example.myapplication.model.LikeRequest
import com.example.myapplication.model.Noticia
import com.example.myapplication.modelo.HistorialComentarioRequest
import com.example.myapplication.modelo.HistorialNoticiaRequest
import com.example.myapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatearFechaLarga(fechaISO: String): String {
    return try {
        val input = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("es", "ES"))
        val output = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale("es", "ES"))
        val date = input.parse(fechaISO)
        output.format(date!!)
    } catch (e: Exception) {
        fechaISO
    }
}

class NewsActivity : ComponentActivity() {
    // Mostar Noticia
    private lateinit var tvTitulo: TextView
    private lateinit var tvAutor: TextView
    private lateinit var tvFecha: TextView
    private lateinit var tvContenido: TextView
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button

    private var listaNoticias: List<Noticia> = emptyList()
    private var indiceActual = 0

    // Likes
    private var idUsuario: Int = -1
    private lateinit var tvLikes: TextView
    private var haDadoLike = false
    private lateinit var btnLike: Button

    // Comentarios
    private lateinit var rvComentarios: RecyclerView
    private lateinit var comentarioAdapter: ComentarioAdapter
    private lateinit var etComentario: EditText
    private lateinit var btnComentar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_news)

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
        val search = findViewById<ImageButton>(R.id.searchButton)
        search.setOnClickListener() {
            val intent = Intent(this, PostSearchActivity::class.java)
            startActivity(intent)
        }

        // Mostar Noticia
        tvTitulo = findViewById(R.id.title)
        tvAutor = findViewById(R.id.author)
        tvFecha = findViewById(R.id.date)
        tvContenido = findViewById(R.id.content)
        btnBack = findViewById(R.id.backButton)
        btnNext = findViewById(R.id.nextButton)

        btnBack.setOnClickListener {
            if (indiceActual > 0) {
                indiceActual--
                mostrarNoticiaActual()
            }
        }

        btnNext.setOnClickListener {
            if (indiceActual < listaNoticias.size - 1) {
                indiceActual++
                mostrarNoticiaActual()
            }
        }

        cargarNoticias()

        // Likes
        tvLikes = findViewById(R.id.likes)
        btnLike = findViewById(R.id.likeButton)

        btnLike.setOnClickListener {
            if (idUsuario == -1) {
                Toast.makeText(this, "Debes iniciar sesión para dar like", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val noticia = listaNoticias[indiceActual]
            val like = LikeRequest(idusuarioLI = idUsuario, idnoticiaLI = noticia.idnoticias)

            if (haDadoLike) {
                RetrofitClient.instance.quitarLike(like).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            haDadoLike = false
                            val actual = listaNoticias[indiceActual]
                            val actualizado = actual.copy(LIKES = actual.LIKES - 1)
                            listaNoticias = listaNoticias.mapIndexed { i, n ->
                                if (i == indiceActual) actualizado else n
                            }
                            actualizarBotonLike()
                            mostrarNoticiaActual()
                            Toast.makeText(this@NewsActivity, "Like quitado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@NewsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                RetrofitClient.instance.darLike(like).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            haDadoLike = true
                            val actual = listaNoticias[indiceActual]
                            val actualizado = actual.copy(LIKES = actual.LIKES + 1)
                            listaNoticias = listaNoticias.mapIndexed { i, n ->
                                if (i == indiceActual) actualizado else n
                            }
                                actualizarBotonLike()
                            mostrarNoticiaActual()
                            Toast.makeText(this@NewsActivity, "Like agregado", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@NewsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }
        val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        idUsuario = prefs.getInt("idUsuario", -1)

        // Comentarios
        rvComentarios = findViewById(R.id.rvComentarios)
        etComentario = findViewById(R.id.commentInput)
        btnComentar = findViewById(R.id.commentButton)

        comentarioAdapter = ComentarioAdapter()
        rvComentarios.layoutManager = LinearLayoutManager(this)
        rvComentarios.adapter = comentarioAdapter

        btnComentar.setOnClickListener {
            val contenido = etComentario.text.toString().trim()
            if (idUsuario == -1) {
                Toast.makeText(this, "Inicia sesión para comentar", Toast.LENGTH_SHORT).show()
            } else if (contenido.isNotEmpty()) {
                val comentario = ComentarioRequest(
                    contenido = etComentario.text.toString().trim(),
                    fechacoment = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(System.currentTimeMillis()),
                    idusuario = idUsuario,
                    idnoticias = listaNoticias[indiceActual].idnoticias
                )

                RetrofitClient.instance.publicarComentario(comentario)
                    .enqueue(object : Callback<Void> {
                        override fun onResponse(call: Call<Void>, response: Response<Void>) {
                            if (response.isSuccessful) {
                                etComentario.text.clear()
                                cargarComentarios(listaNoticias[indiceActual].idnoticias)
                                Toast.makeText(this@NewsActivity, "Comentario publicado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@NewsActivity, "Error al comentar", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<Void>, t: Throwable) {
                            Toast.makeText(this@NewsActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

    }

    private fun cargarNoticias() {
        RetrofitClient.instance.obtenerNoticias()
            .enqueue(object : Callback<List<Noticia>> {
                override fun onResponse(call: Call<List<Noticia>>, response: Response<List<Noticia>>) {
                    if (response.isSuccessful && response.body() != null) {
                        listaNoticias = response.body()!!
                        if (listaNoticias.isNotEmpty()) {
                            indiceActual = 0
                            mostrarNoticiaActual()
                        } else {
                            Toast.makeText(this@NewsActivity, "No hay noticias", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@NewsActivity, "Error al cargar noticias", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Noticia>>, t: Throwable) {
                    Toast.makeText(this@NewsActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun mostrarNoticiaActual() {
        val noticia = listaNoticias[indiceActual]
        tvTitulo.text = noticia.titulo
        tvAutor.text = "by ${noticia.autor}"
        tvFecha.text = formatearFechaLarga(noticia.fecha_publicacion)
        tvContenido.text = noticia.contenido
        tvLikes.text = "Likes: ${noticia.LIKES}"
        if (idUsuario != -1) {
            verificarLike(noticia.idnoticias)
            agregarNoticiaAHistorial(noticia.idnoticias)
        } else {
            btnLike.isEnabled = false
            btnLike.text = "Log-In"
        }
        cargarComentarios(listaNoticias[indiceActual].idnoticias)
    }

    private fun verificarLike(idNoticia: Int) {
        // esta sería una llamada GET que podrías tener en el backend, o puedes traer todos los likes y filtrar
        RetrofitClient.instance.obtenerLikesUsuario(idUsuario).enqueue(object : Callback<List<LikeRequest>> {
            override fun onResponse(call: Call<List<LikeRequest>>, response: Response<List<LikeRequest>>) {
                if (response.isSuccessful && response.body() != null) {
                    val likes = response.body()!!
                    haDadoLike = likes.any { it.idnoticiaLI == idNoticia }
                    actualizarBotonLike()
                }
            }

            override fun onFailure(call: Call<List<LikeRequest>>, t: Throwable) {
                Toast.makeText(this@NewsActivity, "Error al verificar like", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun actualizarBotonLike() {
        if (haDadoLike) {
            btnLike.text = "Quitar Like"
        } else {
            btnLike.text = "Dar Like"
        }
    }

    private fun agregarNoticiaAHistorial(idNoticia: Int) {
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(System.currentTimeMillis())

        val historial = HistorialNoticiaRequest(
            fecha_vistah = fechaActual,
            idnoticiaHN = idNoticia,
            idusuarioHN = idUsuario
        )

        RetrofitClient.instance.agregarAHistorial(historial).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // Puedes dejarlo vacío si no necesitas mostrar nada
                if (!response.isSuccessful) {
                    println("No se pudo agregar a historial: código ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                println("Error al registrar en historial: ${t.message}")
            }
        })
    }

    private fun cargarComentarios(idNoticia: Int) {
        RetrofitClient.instance.obtenerComentarios(idNoticia)
            .enqueue(object : Callback<List<Comentario>> {
                override fun onResponse(call: Call<List<Comentario>>, response: Response<List<Comentario>>) {
                    if (response.isSuccessful && response.body() != null) {
                        val comentarios = response.body()!!
                        comentarioAdapter.actualizar(comentarios)

                        registrarComentariosVistos(comentarios)
                    }
                }

                override fun onFailure(call: Call<List<Comentario>>, t: Throwable) {
                    Toast.makeText(this@NewsActivity, "Error al cargar comentarios", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun registrarComentariosVistos(comentarios: List<Comentario>) {
        val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val idUsuario = prefs.getInt("idUsuario", -1)
        val idNoticia = listaNoticias[indiceActual].idnoticias
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        if (idUsuario == -1) return // si no ha iniciado sesión, no registra

        comentarios.forEach { comentario ->
            val historial = HistorialComentarioRequest(
                fecha_vista = fechaActual,
                idnoticiaHC = idNoticia,
                idusuarioHC = idUsuario,
                idcomentariosHC = comentario.idcomentarios
            )

            RetrofitClient.instance.agregarHistorialComentario(historial)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Log.d("HistorialComentario", "Registrado comentario ${comentario.idcomentarios}")
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("HistorialComentario", "Error: ${t.message}")
                    }
                })
        }
    }
}

