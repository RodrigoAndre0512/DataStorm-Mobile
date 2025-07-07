package com.example.myapplication.model

import java.io.Serializable


data class Noticia(
    val idnoticias: Int,
    val titulo: String,
    val contenido: String,
    val categoria: String,
    val autor: String,
    val fecha_publicacion: String,
    val LIKES: Int,
    val tags: String
) : Serializable



data class NoticiaNueva(
    val titulo: String,
    val contenido: String,
    val categoria: String,
    val autor: String,
    val fecha_publicacion: String,
    val LIKES: Int = 0
)

data class LikeRequest(
    val idusuarioLI: Int,
    val idnoticiaLI: Int
)




