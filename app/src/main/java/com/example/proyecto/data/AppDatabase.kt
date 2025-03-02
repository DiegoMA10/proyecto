package com.example.proyecto.data


import androidx.room.Database
import androidx.room.RoomDatabase

import com.example.proyecto.dao.ComponentDao
import com.example.proyecto.dao.ConfigurationDao
import com.example.proyecto.dao.FavoritesDao
import com.example.proyecto.dao.UserDao
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.Entities.ConfigurationEntity
import com.example.proyecto.Entities.FavoritesEntity
import com.example.proyecto.Entities.UserEntity

@Database(entities = [UserEntity::class, ConfigurationEntity::class, ComponentEntity::class, FavoritesEntity::class ], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun configurationDao(): ConfigurationDao
    abstract fun componentDao(): ComponentDao
    abstract fun favoritesDao(): FavoritesDao



}
