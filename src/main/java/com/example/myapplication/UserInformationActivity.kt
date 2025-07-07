package com.example.myapplication

import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.model.Usuario
import com.example.myapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
class UserInformationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_information)

        val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
        val correo = prefs.getString("correoUsuario", null)

        val nombreText = findViewById<TextView>(R.id.nameOutput)
        val correoText = findViewById<TextView>(R.id.nameAccountOutput)
        val edadText = findViewById<TextView>(R.id.ageOutput)
        val passwordText = findViewById<TextView>(R.id.passwordOutput)
        val logoutButton = findViewById<Button>(R.id.logOutButton)
        if (correo != null) {
            RetrofitClient.instance.informacion(correo)
                .enqueue(object : Callback<Usuario> {
                    override fun onResponse(call: Call<Usuario>, response: Response<Usuario>) {
                        if (response.isSuccessful && response.body() != null) {
                            val usuario = response.body()!!
                            val edad = calcularEdad(usuario.Anio, usuario.Mes, usuario.Dia)

                            nombreText.text = "${usuario.UserName}"
                            correoText.text = "${usuario.Correo}"
                            edadText.text = "$edad años"
                            passwordText.text="**********"
                        } else {
                            Toast.makeText(this@UserInformationActivity, "No se pudo obtener la información", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Usuario>, t: Throwable) {
                        Toast.makeText(this@UserInformationActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
        logoutButton.setOnClickListener {
            prefs.edit().remove("correoUsuario").putInt("idUsuario", -1).apply()
            val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
            with (sharedPref.edit()) {
                putBoolean("isLoggedIn", false)
                apply()
            }
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish() // Evita volver atrás
        }
        findViewById<ImageButton>(R.id.homeButton).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }
        findViewById<ImageButton>(R.id.newsButton).setOnClickListener {
            startActivity(Intent(this, NewsActivity::class.java))
        }
        findViewById<ImageButton>(R.id.searchButton).setOnClickListener {
            startActivity(Intent(this, PostSearchActivity::class.java))
        }
        findViewById<Button>(R.id.noticiasButton).setOnClickListener {
            startActivity(Intent(this, NewsHistoryActivity::class.java))
        }
        findViewById<Button>(R.id.historialButton).setOnClickListener {
            startActivity(Intent(this, CommentHistoryActivity::class.java))
        }
        findViewById<Button>(R.id.publicarButton).setOnClickListener {
            startActivity(Intent(this, PublicationActivity::class.java))
        }
        findViewById<Button>(R.id.nasaButton).setOnClickListener {
            startActivity(Intent(this, NasaApodActivity::class.java))
        }
    }

    private fun calcularEdad(anio: Int, mes: Int, dia: Int): Int {
        val hoy = Calendar.getInstance()
        val nacimiento = Calendar.getInstance()
        nacimiento.set(anio, mes - 1, dia)

        var edad = hoy.get(Calendar.YEAR) - nacimiento.get(Calendar.YEAR)
        if (hoy.get(Calendar.DAY_OF_YEAR) < nacimiento.get(Calendar.DAY_OF_YEAR)) {
            edad--
        }
        return edad
    }
}