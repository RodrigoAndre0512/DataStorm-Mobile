package com.example.myapplication.network

import com.example.myapplication.model.Noticia
import java.sql.DriverManager
import java.sql.Connection

object DatabaseHelper {
    private const val URL = "jdbc:mysql://btuu8k041acvbxporpsx-mysql.services.clever-cloud.com:3306/btuu8k041acvbxporpsx?useSSL=false&allowPublicKeyRetrieval=true"
    private const val USER = "uu2z9bfklpnksgne"
    private const val PASS = "zdl6pa09XUKjNn5AIT55"

    init {
        Class.forName("com.mysql.jdbc.Driver")
    }

    fun getConnection(): Connection =
        DriverManager.getConnection(URL, USER, PASS)

    fun searchNoticias(query: String): List<Noticia> {
        val conn = getConnection()
        val stmt = conn.prepareStatement(
            "SELECT idnoticias, titulo, contenido, categoria, autor FROM noticias " +
                    "WHERE autor LIKE ? OR categoria LIKE ? OR contenido LIKE ?"
        )
        val param = "%$query%"
        stmt.setString(1, param)
        stmt.setString(2, param)
        stmt.setString(3, param)

        val rs = stmt.executeQuery()
        val lista = mutableListOf<Noticia>()
        while (rs.next()) {
            lista.add(Noticia(
                rs.getInt("idnoticias"),
                rs.getString("titulo"),
                rs.getString("contenido"),
                rs.getString("categoria"),
                rs.getString("autor"),
                rs.getString("fecha_publicacion"),
                rs.getInt("LIKES"),
                rs.getString("tags"),
            ))
        }

        rs.close()
        stmt.close()
        conn.close()
        return lista
    }
}
