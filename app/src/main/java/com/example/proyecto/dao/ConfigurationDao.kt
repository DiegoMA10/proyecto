package com.example.proyecto.dao

import androidx.room.*
import com.example.proyecto.Entities.ConfigurationEntity
import com.example.proyecto.Entities.ConfigurationWithComponents

@Dao
interface ConfigurationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfiguration(configuration: ConfigurationEntity): Long

    @Update
    suspend fun updateConfiguration(configuration: ConfigurationEntity)

    @Delete
    suspend fun deleteConfiguration(configuration: ConfigurationEntity)

    // Obtener todas las configuraciones con sus componentes relacionados
    @Transaction
    @Query("SELECT * FROM configurations")
    suspend fun getConfigurationsWithComponents(): List<ConfigurationWithComponents>

    // Obtener una configuración específica con sus componentes
    @Transaction
    @Query("SELECT * FROM configurations WHERE id = :configurationId")
    suspend fun getConfigurationWithComponents(configurationId: Int): ConfigurationWithComponents

    @Query("DELETE FROM components WHERE configurationId = :configurationId")
    suspend fun deleteComponentsByConfigurationId(configurationId: Long)

    suspend fun deleteConfigurationWithComponents(configuration: ConfigurationEntity) {
        // Eliminar los componentes de la configuración
        deleteComponentsByConfigurationId(configuration.id.toLong())
        // Ahora eliminar la configuración
        deleteConfiguration(configuration)
    }

    @Query("SELECT * FROM configurations")
    suspend fun getConfiguration(): List<ConfigurationEntity>
}
