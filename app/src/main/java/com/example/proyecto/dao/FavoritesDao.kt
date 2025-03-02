package com.example.proyecto.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto.Entities.FavoritesEntity

@Dao
interface FavoritesDao {
    @Insert
    suspend fun insertFavorite(favorite: FavoritesEntity)

    @Delete
    suspend fun deleteFavorite(favorite: FavoritesEntity)

    @Query("SELECT * FROM favorites WHERE userId = :userId")
    suspend fun getFavoritesByUser(userId: Int): List<FavoritesEntity>

    @Query("DELETE FROM favorites WHERE configurationId = :configurationId")
    suspend fun deleteFavoritesByConfigurationId(configurationId: Int)
}