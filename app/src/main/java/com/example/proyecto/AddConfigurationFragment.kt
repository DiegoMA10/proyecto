package com.example.proyecto

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.Entities.ConfigurationEntity
import com.example.proyecto.Entities.UserEntity
import com.example.proyecto.apifotos.UnsplashService
import com.example.proyecto.data.AppApplication
import com.example.proyecto.databinding.FragmentAddConfigurationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddConfigurationFragment : Fragment() {

    private lateinit var binding: FragmentAddConfigurationBinding
    private val categories = listOf("CPU", "GPU", "Motherboard", "RAM", "Case", "PSU", "Storage")
    private lateinit var allComponents: List<ComponentEntity>
    private lateinit var adapter: AddConfigurationAdapter
    private lateinit var user: UserEntity
    private val args: AddConfigurationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val selectedComponents = mutableMapOf<String, ComponentEntity>()
        binding.recyclerViewConfiguration.layoutManager = LinearLayoutManager(requireContext())
        user = args.Usuario

        allComponents = loadComponentsFromJson(requireContext())

        adapter = AddConfigurationAdapter(requireContext(), categories, allComponents) { category, selectedComponent ->
            selectedComponents[category] = selectedComponent
            Toast.makeText(requireContext(), "Seleccionado: ${selectedComponent.name} en $category", Toast.LENGTH_SHORT).show()

            val totalPrice = selectedComponents.values.sumOf { it.price }
            binding.totalPriceText.text = "Total: $%.2f".format(totalPrice)
        }
        binding.recyclerViewConfiguration.adapter = adapter

        binding.acceptButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val imageUrl = UnsplashService.getImage()  // Obtén la URL de la imagen de manera asíncrona
                    Log.i("AddConfigurationFragment", "Imagen URL: $imageUrl")

                    // Guarda la configuración con la URL de la imagen
                    saveConfiguration(
                        binding.componentNameInput.text.toString(),
                        binding.componentDescriptionInput.text.toString(),
                        selectedComponents.values.sumOf { it.price },
                        selectedComponents.values.toList(),
                        imageUrl
                    )
                } catch (e: Exception) {
                    Log.e("AddConfigurationFragment", "Error al obtener la imagen de Unsplash", e)
                    Toast.makeText(requireContext(), "Error al obtener la imagen.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.randomButton.setOnClickListener {
            adapter.selectRandomComponents()
        }
    }

    private fun loadComponentsFromJson(context: Context): List<ComponentEntity> {
        val allComponents = mutableListOf<ComponentEntity>()
        for (category in categories) {
            val jsonString = context.assets.open("$category.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<ComponentEntity>>() {}.type
            val components: List<ComponentEntity> = Gson().fromJson(jsonString, listType)
            val updatedComponents = components.map { it.copy(tipo = category) }
            allComponents += updatedComponents
        }

        return allComponents
    }

    private suspend fun saveConfiguration(
        name: String, description: String, price: Double,
        components: List<ComponentEntity>, imageUrl: String
    ) {
        val configuration = ConfigurationEntity(
            nombre = name,
            descripcion = description,
            precio = price,
            imageUrl = imageUrl
        )
        lifecycleScope.launch(Dispatchers.IO) {
            val configurationId = AppApplication.database.configurationDao().insertConfiguration(configuration)
            val componentsEntities = components.map { it.copy(configurationId = configurationId.toInt()) }
            AppApplication.database.componentDao().insertComponents(componentsEntities)

            withContext(Dispatchers.Main) {
                // Navegar después de guardar la configuración
                val action = AddConfigurationFragmentDirections.actionAddConfigurationFragmentToHomeFragment(user)
                findNavController().navigate(action)
            }
        }
    }
}
