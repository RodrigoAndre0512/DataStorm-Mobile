package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.model.Noticia
import com.example.myapplication.databinding.ItemNoticiaBinding

class NoticiasAdapter(
    val onClick: (Noticia) -> Unit
) : ListAdapter<Noticia, NoticiasAdapter.VH>(DiffCallback()) {

    class VH(val binding: ItemNoticiaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(noticia: Noticia, onClick: (Noticia) -> Unit) {
            binding.title.text = noticia.titulo
            binding.author.text = "Autor: ${noticia.autor}"
            binding.content.text = noticia.contenido // ← NUEVO
            binding.root.setOnClickListener { onClick(noticia) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemNoticiaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class DiffCallback : DiffUtil.ItemCallback<Noticia>() {
        override fun areItemsTheSame(oldItem: Noticia, newItem: Noticia) = oldItem.idnoticias == newItem.idnoticias
        override fun areContentsTheSame(oldItem: Noticia, newItem: Noticia) = oldItem == newItem
    }
}
