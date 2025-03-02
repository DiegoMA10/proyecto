package com.example.proyecto.Entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

import java.io.Serializable

@Entity(
    tableName = "components",
    foreignKeys = [ForeignKey(
        entity = ConfigurationEntity::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("configurationId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class ComponentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val price: Double,
    val tipo: String,
    val configurationId: Int // clave foránea para relacionar con ConfigurationEntity
) : Serializable
{
    override fun toString(): String {
        return name
    }
}


