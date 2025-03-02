package com.example.proyecto

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto.Entities.UserEntity
import com.example.proyecto.data.AppApplication
import com.example.proyecto.databinding.FragmentRegisterBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment : Fragment(R.layout.fragment_register) {
    private lateinit var binding: FragmentRegisterBinding

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegisterUsername.text.toString()
            val password = binding.etRegisterPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                Toast.makeText(requireContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val existingUser = withContext(Dispatchers.IO) {
                    AppApplication.database.userDao().getUserByUsername(username)
                }
                if (existingUser != null) {
                    Toast.makeText(requireContext(), "El usuario ya existe", Toast.LENGTH_SHORT).show()
                } else {
                    guardarUsuario(username, password)
                    Toast.makeText(requireContext(), "Usuario registrado", Toast.LENGTH_SHORT).show()

                    findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_RegisterFragment_to_LoginFragment)
        }
    }

    private fun guardarUsuario(username: String, password: String) {
        val user = UserEntity(username = username, password = password)
        lifecycleScope.launch(Dispatchers.IO) {
            AppApplication.database.userDao().insertUser(user)
        }
    }
}
