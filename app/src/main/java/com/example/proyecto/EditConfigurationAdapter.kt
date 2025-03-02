package com.example.proyecto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.databinding.ItemConfigurationBinding
import com.example.proyecto.databinding.ItemConfigurationCategoryBinding

class EditConfigurationAdapter(
private val context: Context,
private val categories: List<String>,
private val components: List<ComponentEntity>,
private val preSelectedComponents: Map<String, ComponentEntity>,
private val onComponentSelected: (String, ComponentEntity) -> Unit
) : RecyclerView.Adapter<EditConfigurationAdapter.ViewHolder>() {

    private val selectedComponents = mutableMapOf<String, ComponentEntity?>()

    init {

        selectedComponents.putAll(preSelectedComponents)
    }

    inner class ViewHolder(val binding: ItemConfigurationCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConfigurationCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.categoryTextView.text = category

        // Filtrar los componentes por categoría
        val filteredComponents = components.filter { it.tipo.equals(category, ignoreCase = true) }

        // Crear un adaptador para AutoCompleteTextView
        val adapter = ComponentAdapter(context, filteredComponents)
        holder.binding.componentAutoComplete.setAdapter(adapter)

        holder.binding.componentAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.binding.componentAutoComplete.showDropDown()
            }
        }

        // Configurar el listener para capturar la selección
        holder.binding.componentAutoComplete.setOnItemClickListener { _, _, pos, _ ->

            val selectedComponent = adapter.getItem(pos)
            selectedComponent?.let {
                selectedComponents[category] = it
                holder.binding.componentPriceTextView.text = "Precio: $${it.price}"
                onComponentSelected(category, it)
            }
        }

        // Mostrar el nombre del componente previamente seleccionado en AutoCompleteTextView
        selectedComponents[category]?.let {
            holder.binding.componentAutoComplete.setText(it.name, false)
            holder.binding.componentPriceTextView.text = "Precio: $${it.price}"
        }
    }

    override fun getItemCount(): Int = categories.size

    private class ComponentAdapter(
        context: Context,
        private val components: List<ComponentEntity>

    ) : ArrayAdapter<ComponentEntity>(context, 0, components) {

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.dropdown_item_component, parent, false)
            val nameText = view.findViewById<TextView>(R.id.dropdown_component_name)
            val priceText = view.findViewById<TextView>(R.id.dropdown_component_price)
            val item = getItem(position)
            nameText.text = item?.name
            priceText.text = "USD ${item?.price}"
            return view
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            return getDropDownView(position, convertView, parent)
        }
    }
}
