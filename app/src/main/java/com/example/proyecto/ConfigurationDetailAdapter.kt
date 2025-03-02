package com.example.proyecto

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.databinding.ItemDetailBinding

class ConfigurationDetailAdapter(private val components: List<ComponentEntity>) :
    RecyclerView.Adapter<ConfigurationDetailAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDetailBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDetailBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val component = components[position]
        holder.binding.tvCategoria.text = component.tipo
        holder.binding.tvNombre.text = component.name
        holder.binding.tvPrecio.text = "Precio: $${component.price}"
    }

    override fun getItemCount(): Int = components.size
}
