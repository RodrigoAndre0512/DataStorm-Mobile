package com.example.myapplication.network

import com.example.myapplication.model.Comentario
import com.example.myapplication.model.ComentarioRequest
import com.example.myapplication.model.LikeRequest
import com.example.myapplication.model.LoginResponse
import com.example.myapplication.model.Noticia
import com.example.myapplication.model.NoticiaNueva
import com.example.myapplication.model.Usuario
import com.example.myapplication.model.UsuarioConId
import com.example.myapplication.model.UsuarioRegistrado
import com.example.myapplication.modelo.HistorialComentario
import com.example.myapplication.modelo.HistorialComentarioRequest
import com.example.myapplication.modelo.HistorialNoticia
import com.example.myapplication.modelo.HistorialNoticiaRequest
import com.example.myapplication.modelo.NasaNoticia
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("/signup")
    fun registrarUsuario(@Body usuario: Usuario): Call<Void>

    @GET("/usuario")
    fun informacion(@Query("correo") correo: String): Call<Usuario>

    @GET("/usuarioid")
    fun obtenerUsuarioPorId(@Query("id") id: Int): Call<Usuario>

    @POST("/login")
    fun iniciarSesion(@Body usuario: UsuarioRegistrado): Call<LoginResponse>

    @GET("/historialnoticias/{idusuario}")
    fun obtenerHistorialNoticias(@Path("idusuario") idUsuario: Int): Call<List<HistorialNoticia>>

    @GET("/historialcomentarios/{id}")
    fun obtenerHistorialComentarios(@Path("id") idUsuario: Int): Call<List<HistorialComentario>>

    @POST("/noticias")
    fun publicarNoticia(@Body noticia: NoticiaNueva): Call<Void>

    @GET("/noticias")
    fun obtenerNoticias(): Call<List<Noticia>>

    @POST("/likes")
    fun darLike(@Body like: LikeRequest): Call<Void>

    @HTTP(method = "DELETE", path = "/likes", hasBody = true)
    fun quitarLike(@Body like: LikeRequest): Call<Void>

    @GET("/likes/{idusuario}")
    fun obtenerLikesUsuario(@Path("idusuario") idusuario: Int): Call<List<LikeRequest>>

    @POST("/historialnoticias")
    fun agregarAHistorial(@Body historial: HistorialNoticiaRequest): Call<Void>

    @GET("/comentarios/{idnoticia}")
    fun obtenerComentarios(@Path("idnoticia") idnoticia: Int): Call<List<Comentario>>

    @POST("/comentarios")
    fun publicarComentario(@Body comentario: ComentarioRequest): Call<Void>

    @POST("/historialcomentarios")
    fun agregarHistorialComentario(@Body historial: HistorialComentarioRequest): Call<Void>

    @GET("/api/noticias")
    fun buscarNoticiasPorTag(@Query("keyword") keyword: String): Call<List<Noticia>>
}

interface NasaApiService {
    @GET("planetary/apod")
    fun getNasaNoticia(
        @Query("api_key") apiKey: String = "vVfcDS9lemp0J4llnTgf6wTfiUeBETbDuQxMkxtE"
    ): Call<NasaNoticia>
}
