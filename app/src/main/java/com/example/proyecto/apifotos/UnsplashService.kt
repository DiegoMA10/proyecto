package com.example.proyecto.apifotos

import android.util.Log
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object UnsplashService {
    private const val API_URL = "https://api.unsplash.com/photos/random"
    private const val CLIENT_ID = "P3p16ktdCcfxpkQDESzh8HGlK2wOF0QyK1uk9QQSFfw"

    private const val FIXED_IMAGE_URL = "https://images.unsplash.com/photo-1593642532973-d31b6557fa68"

    suspend fun getImage(): String {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.unsplash.com/photos/random?query=gaming+setup&orientation=landscape&client_id=$CLIENT_ID")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()

                if (!response.isSuccessful) {
                    Log.e("UnsplashService", "Error en la solicitud. Código de estado: ${response.code()}")
                    return@withContext FIXED_IMAGE_URL
                }

                response.use { res ->
                    res.body()?.let { body ->
                        val json = body.string()
                        Log.d("UnsplashService", "Respuesta JSON completa: $json")

                        try {
                            // Parseamos la respuesta como un JSONObject porque la API devuelve un objeto, no un array
                            val jsonObject = JSONObject(json)

                            // Accedemos al objeto 'urls' dentro del JSON y obtenemos la URL de la imagen
                            val imageUrl = jsonObject.getJSONObject("urls")
                                .getString("regular")

                            Log.d("UnsplashService", "Imagen URL: $imageUrl")
                            return@withContext imageUrl
                        } catch (e: Exception) {
                            Log.e("UnsplashService", "Error al procesar la respuesta JSON", e)
                            return@withContext FIXED_IMAGE_URL
                        }
                    } ?: run {
                        Log.e("UnsplashService", "El cuerpo de la respuesta está vacío")
                        return@withContext FIXED_IMAGE_URL
                    }
                }
            } catch (e: IOException) {
                Log.e("UnsplashService", "Error en la solicitud", e)
                return@withContext FIXED_IMAGE_URL
            }
        }
    }
}
