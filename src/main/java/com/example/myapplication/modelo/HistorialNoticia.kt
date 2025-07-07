package com.example.myapplication.modelo

data class HistorialNoticia(
    val IDHISTONOTI: Int,
    val fecha_vistah: String,
    val idnoticiaHN: Int,
    val idusuarioHN: Int,
    val titulo: String,
    val categoria: String
)

data class HistorialNoticiaRequest(
    val fecha_vistah: String,
    val idnoticiaHN: Int,
    val idusuarioHN: Int
)