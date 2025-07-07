package com.example.myapplication.model

data class Usuario(
    val UserName: String,
    val Correo: String,
    val Contrasena: String,
    val Dia: Int,
    val Mes: Int,
    val Anio: Int
)
data class UsuarioRegistrado(
    val Correo: String,
    val Contrasena: String,
)


data class UsuarioConId(
    val id: Int,
    val UserName: String,
    val Correo: String,
    val Contrasena: String,
    val Dia: Int,
    val Mes: Int,
    val Anio: Int
)
data class LoginResponse(
    val usuario: UsuarioConId,
)


