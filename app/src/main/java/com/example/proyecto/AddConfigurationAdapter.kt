package com.example.proyecto

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto.Entities.ComponentEntity
import com.example.proyecto.databinding.ItemConfigurationCategoryBinding
import kotlin.random.Random

class AddConfigurationAdapter(
    private val context: Context,
    private val categories: List<String>,
    private val components: List<ComponentEntity>,
    private val onComponentSelected: (String, ComponentEntity) -> Unit
) : RecyclerView.Adapter<AddConfigurationAdapter.ViewHolder>() {

    private val selectedComponents = mutableMapOf<String, ComponentEntity?>()

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

        // Filter components only once when the category is set
        val filteredComponents = components.filter { it.tipo.equals(category, ignoreCase = true) }

        // Create an adapter for AutoCompleteTextView
        val adapter = ComponentAdapter(context, filteredComponents)
        holder.binding.componentAutoComplete.setAdapter(adapter)

        holder.binding.componentAutoComplete.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                holder.binding.componentAutoComplete.showDropDown()
            }
        }

        // Configure the listener to capture the selection
        holder.binding.componentAutoComplete.setOnItemClickListener { _, _, pos, _ ->
            // Obtenemos el objeto directamente desde el adaptador filtrado
            val selectedComponent = adapter.getItem(pos)
            selectedComponent?.let {
                selectedComponents[category] = it
                holder.binding.componentPriceTextView.text = "Precio: $${it.price}"
                onComponentSelected(category, it)
            }
        }


        // Display previously selected component name in AutoCompleteTextView if exists
        selectedComponents[category]?.let {
            holder.binding.componentAutoComplete.setText(selectedComponents[category]?.name, false)
            holder.binding.componentPriceTextView.text = "Precio: $${selectedComponents.getValue(category)?.price}"
        }
    }
    fun selectRandomComponents() {
        categories.forEach { category ->
            val filteredComponents = components.filter { it.tipo.equals(category, ignoreCase = true) }
            if (filteredComponents.isNotEmpty()) {
                val randomComponent = filteredComponents[Random.nextInt(filteredComponents.size)]
                selectedComponents[category] = randomComponent

                onComponentSelected(category, randomComponent)
            }
        }
        notifyDataSetChanged() // Refresca la UI con los nuevos componentes aleatorios
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
