package com.example.proyecto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.proyecto.Entities.ConfigurationWithComponents
import com.example.proyecto.Entities.UserEntity
import com.example.proyecto.databinding.FragmentConfigurationDetailBinding

class ConfigurationDetailFragment : Fragment() {

    private lateinit var binding: FragmentConfigurationDetailBinding
    private lateinit var adapter: ConfigurationDetailAdapter
    private val args: ConfigurationDetailFragmentArgs by navArgs()
    private lateinit var configuration: ConfigurationWithComponents
    private lateinit var user: UserEntity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConfigurationDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener datos pasados desde la lista de configuraciones
        configuration = args.ConfigurationWithComponets
        user = args.Usuario

        configuration.let {
            binding.tvNombreConfiguracion.text = it.configuration.nombre
            Glide.with(binding.tvFoto.context)
                .load(it.configuration.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .centerCrop()
                .into(binding.tvFoto)


            binding.tvDescripcion.text = it.configuration.descripcion
            binding.tvPrecioTotal.text = "Total: $%.2f".format(it.components.sumOf { c -> c.price })

            adapter = ConfigurationDetailAdapter(it.components)
            binding.rvComponentes.layoutManager = LinearLayoutManager(requireContext())
            binding.rvComponentes.adapter = adapter
        }

        // Ejemplo: agregar listener al botón de editar
        binding.btnEditar.setOnClickListener {
            // Navegar a la pantalla de edición de configuración
            val action = ConfigurationDetailFragmentDirections.actionConfigurationDetailFragmentToEditConfigurationFragment(user,configuration)
            findNavController().navigate(action)
        }
    }
}
