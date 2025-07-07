package com.example.myapplication.modelo

data class HistorialComentario(
    val idhistorialHC: Int,
    val fecha_vista: String,
    val idnoticiaHC: Int,
    val idusuarioHC: Int,
    val idcomentariosHC: Int,
    val contenido: String,
    val titulo: String
)

data class HistorialComentarioRequest(
    val fecha_vista: String,
    val idnoticiaHC: Int,
    val idusuarioHC: Int,
    val idcomentariosHC: Int
)



