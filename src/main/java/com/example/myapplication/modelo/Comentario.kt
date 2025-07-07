package com.example.myapplication.model

data class Comentario(
    val idcomentarios: Int,
    val idnoticias: Int,
    val idusuario: Int,
    val username: String,
    val fechacoment: String,
    val contenido: String
)

data class ComentarioRequest(
    val contenido: String,
    val fechacoment: String,
    val idusuario: Int,
    val idnoticias: Int
)