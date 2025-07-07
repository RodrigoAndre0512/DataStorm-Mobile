package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.Comentario

class ComentarioAdapter : RecyclerView.Adapter<ComentarioAdapter.VH>() {

    private val comentarios = mutableListOf<Comentario>()

    class VH(val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(comentario: Comentario) {
            val fecha = formatearFechaLarga(comentario.fechacoment)
            view.findViewById<TextView>(R.id.commentName).text = comentario.username
            view.findViewById<TextView>(R.id.commentDate).text = fecha
            view.findViewById<TextView>(R.id.commentContent).text = comentario.contenido
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comentario, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(comentarios[position])
    }

    override fun getItemCount() = comentarios.size

    fun actualizar(comments: List<Comentario>) {
        comentarios.clear()
        comentarios.addAll(comments)
        notifyDataSetChanged()
    }
}