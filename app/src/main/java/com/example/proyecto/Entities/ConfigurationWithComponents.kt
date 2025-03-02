package com.example.proyecto.Entities

import androidx.room.Embedded
import androidx.room.Relation
import com.example.proyecto.Entities.ComponentEntity
import java.io.Serializable


data class ConfigurationWithComponents(
    @Embedded val configuration: ConfigurationEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "configurationId"

    )
    val components: List<ComponentEntity>
):Serializable

