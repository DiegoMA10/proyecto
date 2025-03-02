package com.example.proyecto.dao
import com.example.proyecto.Entities.ComponentEntity
import androidx.room.*


@Dao
interface ComponentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponent(component: ComponentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComponents(components: List<ComponentEntity>)

    @Update
    suspend fun updateComponent(component: ComponentEntity)

    @Update
    suspend fun updateComponents(components: List<ComponentEntity>)

    @Delete
    suspend fun deleteComponent(component: ComponentEntity)

    // Obtener los componentes asociados a una configuración
    @Query("SELECT * FROM Components WHERE configurationId = :configId")
    suspend fun getComponentsForConfiguration(configId: Int): List<ComponentEntity>
}
