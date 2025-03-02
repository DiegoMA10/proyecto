package com.example.proyecto.Entities



import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configurations")
data class ConfigurationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val precio: Double,
    val imageUrl: String
)

