package com.example.proyecto

import com.example.proyecto.Entities.ConfigurationEntity



import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.proyecto.Entities.FavoritesEntity
import com.example.proyecto.databinding.ItemConfigurationBinding


class HomeAdapter(
    private var configuration: MutableList<ConfigurationEntity>,
    private var favorites: MutableList<FavoritesEntity>,
    private val listener: OnClickListener
) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemConfigurationBinding.bind(view)

        fun setListener(configurationEntity: ConfigurationEntity) {
            binding.root.setOnClickListener {
                listener.onClick(configurationEntity)
            }
            binding.tbFavorite.setOnClickListener {
                listener.onFavorite(configurationEntity)
            }
            binding.root.setOnLongClickListener {
                listener.onDelete(configurationEntity)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_configuration, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = configuration.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val configurationEntity = configuration[position]
        with(holder) {
            setListener(configurationEntity)
            binding.tvTitle.text = configurationEntity.nombre
            binding.content.text = configurationEntity.descripcion
            binding.tvPrice.text = "Precio: $%.2f".format(configurationEntity.precio)



            Glide.with(itemView.context)
                .load(configurationEntity.imageUrl)
                .error(R.drawable.ic_launcher_background)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .centerCrop()
                .into(binding.ivNews)
            val isFavorite = favorites.any { it.configurationId == configurationEntity.id }
            binding.tbFavorite.isChecked = isFavorite
        }
    }

    fun setConfiguration(configurations: List<ConfigurationEntity>) {
        this.configuration = configurations.toMutableList()
        notifyDataSetChanged()
    }

    fun setFavorites(favorites: List<FavoritesEntity>) {
        this.favorites = favorites.toMutableList()
        notifyDataSetChanged()
    }

    fun updateConfiguracion(configurationEntity: ConfigurationEntity) {
        val index = configuration.indexOf(configurationEntity)
        if (index != -1) {
            notifyItemChanged(index)
        }
    }

    fun deleteConfiguration(configurationEntity: ConfigurationEntity) {
        val index = configuration.indexOf(configurationEntity)
        if (index != -1) {
            configuration.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}
