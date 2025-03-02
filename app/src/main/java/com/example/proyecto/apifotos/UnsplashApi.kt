package com.example.proyecto.apifotos

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UnsplashApi {
    // Petición para obtener una imagen aleatoria
    @GET("photos/random")
    suspend fun getRandomImage(
        @Header("Authorization") authorization: String
    ): Response<List<ImageResponse>>
}

// Clase para manejar la respuesta de la API
data class ImageResponse(
    val urls: Urls
)

// Clase para manejar las URLs de la imagen
data class Urls(
    val full: String
)
