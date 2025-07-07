package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.model.Usuario
import com.example.myapplication.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val username = findViewById<EditText>(R.id.nameInput)
        val correo = findViewById<EditText>(R.id.accountInput)
        val contrasena = findViewById<EditText>(R.id.passwordInput)
        val confirmPassword = findViewById<EditText>(R.id.validatePasswordInput)
        val dia = findViewById<EditText>(R.id.dayInput)
        val mes = findViewById<EditText>(R.id.monthInput)
        val anio = findViewById<EditText>(R.id.yearInput)
        val btnRegistrar = findViewById<Button>(R.id.btnsignin)
        btnRegistrar.setOnClickListener {
            val nombre= username.text.toString().trim()
            val email= correo.text.toString().trim()
            val pass= contrasena.text.toString().trim()
            val confirmPass= confirmPassword.text.toString().trim()
            val diaStr= dia.text.toString().trim()
            val mesStr= mes.text.toString().trim()
            val anioStr= anio.text.toString().trim()
            if (nombre.isEmpty()|| email.isEmpty()||pass.isEmpty() ||
                diaStr.isEmpty() || mesStr.isEmpty() || anioStr.isEmpty())
            {
                Toast.makeText(this, "Porfavor completa todos los campos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            if (pass!=confirmPass)
            {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val diaInt= diaStr.toIntOrNull()
            val mesInt= diaStr.toIntOrNull()
            val anioInt= diaStr.toIntOrNull()
            if(diaInt == null || mesInt == null || anioInt == null){
                Toast.makeText(this, "Dia, mesi y año deben ser numeros validos", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }
            val usuario = Usuario(
                UserName = username.text.toString(),
                Correo = correo.text.toString(),
                Contrasena = contrasena.text.toString(),
                Dia = dia.text.toString().toIntOrNull() ?: 0,
                Mes = mes.text.toString().toIntOrNull() ?: 0,
                Anio = anio.text.toString().toIntOrNull() ?: 0
            )
            RetrofitClient.instance.registrarUsuario(usuario)
                .enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@SignUpActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            // Ir a Home si quieres
                            val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(this@SignUpActivity, "Error en el registro: ${response.code()}", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@SignUpActivity, "Fallo de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
