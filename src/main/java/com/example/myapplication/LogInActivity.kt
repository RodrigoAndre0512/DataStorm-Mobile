package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.UsuarioRegistrado
import com.example.myapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LogInActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_log_in)

        val correoInput = findViewById<EditText>(R.id.NameAccount)
        val contrasenaInput = findViewById<EditText>(R.id.Password)
        val btnLogin = findViewById<Button>(R.id.btnlogin)
        val btnPass = findViewById<Button>(R.id.btnsignin)
        btnLogin.setOnClickListener {
            val correo =correoInput.text.toString().trim()
            val contrasena = contrasenaInput.text.toString().trim()
            if(correo.isEmpty() || contrasena.isEmpty())
            {
                Toast.makeText(this, "Completa todos loc campos", Toast.LENGTH_SHORT ).show()
                return@setOnClickListener
            }
            val usuario = UsuarioRegistrado(
                Correo = correo,
                Contrasena = contrasena
            )
            RetrofitClient.instance.iniciarSesion(usuario)
                .enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.isSuccessful) {
                            val usuarioResponse = response.body()?.usuario
                            if (usuarioResponse != null) {
                                Toast.makeText(this@LogInActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()

                                val prefs = getSharedPreferences("MisPreferencias", MODE_PRIVATE)
                                prefs.edit()
                                    .putString("correoUsuario", usuarioResponse.Correo)
                                    .putInt("idUsuario", usuarioResponse.id) // <--- Aquí guardas el ID
                                    .apply()

                                val intent = Intent(this@LogInActivity, UserInformationActivity::class.java)
                                startActivity(intent)

                                val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
                                with (sharedPref.edit()) {
                                    putBoolean("isLoggedIn", true)
                                    apply()
                                }
                            }
                        } else {
                            Toast.makeText(this@LogInActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(this@LogInActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }

        btnPass.setOnClickListener {
            val intent2 = Intent(this, SignUpActivity::class.java)
            startActivity(intent2)

        }
    }
}