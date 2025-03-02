package com.example.proyecto.Entities


import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "favorites",
    primaryKeys = ["userId", "configurationId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"]
        ),
        ForeignKey(
            entity = ConfigurationEntity::class,
            parentColumns = ["id"],
            childColumns = ["configurationId"]
        )
    ]
)
data class FavoritesEntity(
    val userId: Int,
    val configurationId: Int
)
