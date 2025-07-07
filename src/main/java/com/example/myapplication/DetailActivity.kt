package com.example.myapplication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.myapplication.databinding.ActivityDetailBinding
import com.example.myapplication.model.Noticia

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val noticia = intent.getSerializableExtra("noticia") as? Noticia
        noticia?.let {
            binding.tvTitle.text = it.titulo
            binding.tvAuthor.text = it.autor
            binding.tvContent.text = it.contenido
        }
    }
}