package com.example.proyecto

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.Entities.ConfigurationEntity
import com.example.proyecto.Entities.ConfigurationWithComponents
import com.example.proyecto.Entities.UserEntity
import com.example.proyecto.data.AppApplication

import com.example.proyecto.databinding.FragmentEditConfigurationBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditConfigurationFragment : Fragment() {

    private lateinit var binding: FragmentEditConfigurationBinding
    private val categories = listOf("CPU", "GPU", "Motherboard", "RAM", "Case", "PSU", "Storage")
    private lateinit var allComponents: List<ComponentEntity>
    private lateinit var adapter: EditConfigurationAdapter
    private lateinit var user: UserEntity
    private lateinit var configurationWithComponents: ConfigurationWithComponents
    private val args: EditConfigurationFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditConfigurationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Recuperar la configuración desde los argumentos
        user = args.Usuario
        configurationWithComponents = args.ConfigurationWithComponets
        allComponents = loadComponentsFromJson(requireContext())

        // Llenar la información inicial de la configuración
        binding.componentNameInput.setText(configurationWithComponents.configuration.nombre)
        binding.componentDescriptionInput.setText(configurationWithComponents.configuration.descripcion)
        val totalPrice = configurationWithComponents.components.sumOf { it.price }
        binding.totalPriceText.setText("Total: $%.2f".format(totalPrice))

        // Crear el mapa de componentes precargados
        val preSelectedComponents = configurationWithComponents.components
            .associateBy { it.tipo }.toMutableMap() // Crear un Map<String, ComponentEntity> con la categoría como clave


        binding.recyclerViewConfiguration.layoutManager = LinearLayoutManager(requireContext())
        adapter = EditConfigurationAdapter(requireContext(), categories, allComponents, preSelectedComponents) { category, selectedComponent ->
            preSelectedComponents[category] = selectedComponent
            val totalPrice = preSelectedComponents.values.sumOf { it.price }
            binding.totalPriceText.text = "Total: $%.2f".format(totalPrice)
        }
        binding.recyclerViewConfiguration.adapter = adapter


        binding.acceptButton.setOnClickListener {
            saveConfiguration(
                binding.componentNameInput.text.toString(),
                binding.componentDescriptionInput.text.toString(),
                preSelectedComponents.values.sumOf { it.price },
                preSelectedComponents.values.toList(),
                configurationWithComponents.configuration.imageUrl
            )
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

    private fun saveConfiguration(
        name: String, description: String, price: Double,
        components: List<ComponentEntity>, imageUrl: String
    ) {
        val updatedConfiguration = ConfigurationEntity(
            id = configurationWithComponents.configuration.id,
            nombre = name,
            descripcion = description,
            precio = price,
            imageUrl = imageUrl
        )

        lifecycleScope.launch(Dispatchers.IO) {
            // Actualizar la configuración en la base de datos
            AppApplication.database.configurationDao().updateConfiguration(updatedConfiguration)

            // Obtener los componentes actuales en la BD para esta configuración
            val existingComponents = AppApplication.database.componentDao()
                .getComponentsForConfiguration(updatedConfiguration.id)
                .associateBy { it.tipo } // Mapa: tipo -> ComponentEntity

            // Preparar la lista de componentes para guardar
            val componentsToUpdate = mutableListOf<ComponentEntity>()
            val componentsToInsert = mutableListOf<ComponentEntity>()

            for (component in components) {
                val existingComponent = existingComponents[component.tipo]
                if (existingComponent != null) {
                    // Si el componente ya existe, mantener su ID y actualizarlo
                    componentsToUpdate.add(component.copy(id = existingComponent.id, configurationId = updatedConfiguration.id))
                } else {
                    // Si el componente es nuevo, asignarle el configurationId y agregarlo a la lista de inserción
                    componentsToInsert.add(component.copy(configurationId = updatedConfiguration.id))
                }
            }

            // Actualizar e insertar los componentes en la base de datos
            if (componentsToUpdate.isNotEmpty()) {
                AppApplication.database.componentDao().updateComponents(componentsToUpdate)
            }
            if (componentsToInsert.isNotEmpty()) {
                AppApplication.database.componentDao().insertComponents(componentsToInsert)
            }

            // Verificar si los datos se han guardado correctamente
            val updatedComponents = AppApplication.database.componentDao()
                .getComponentsForConfiguration(updatedConfiguration.id)
            Log.d("EditConfig", "Componentes guardados en la BD: $updatedComponents")

            withContext(Dispatchers.Main) {
                val configurationWithComponents = AppApplication.database.configurationDao()
                    .getConfigurationWithComponents(updatedConfiguration.id)
                val action = EditConfigurationFragmentDirections
                    .actionEditConfigurationFragmentToConfigurationDetailFragment(user, configurationWithComponents)

                findNavController().navigate(action)
            }
        }
    }

}
