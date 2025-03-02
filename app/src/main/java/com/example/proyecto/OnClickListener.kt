package com.example.proyecto

import com.example.proyecto.Entities.ConfigurationEntity

interface OnClickListener {
    fun onClick(configuratonEntity: ConfigurationEntity)

    fun onFavorite(configuratonEntity: ConfigurationEntity)

    fun onDelete(configuratonEntity: ConfigurationEntity)
}