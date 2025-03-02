package com.example.proyecto

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.proyecto.data.AppApplication
import com.example.proyecto.databinding.FragmentLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.proyecto.dao.UserDao

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): android.view.View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        preferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val savedUsername = preferences.getString(getString(R.string.username), "")
        val savedPassword = preferences.getString(getString(R.string.password), "")
        val isRemembered = preferences.getBoolean(getString(R.string.sp_remember_me), false)
        if (isRemembered) {
            binding.etUsername.setText(savedUsername)
            binding.etPassword.setText(savedPassword)
            binding.cbRememberMe.isChecked = true
        }

        binding.loginButton.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val password = binding.etPassword.text.toString()


            with(preferences.edit()) {
                if (binding.cbRememberMe.isChecked) {
                    putString(getString(R.string.username), username)
                    putString(getString(R.string.password), password)
                    putBoolean(getString(R.string.sp_remember_me), true)
                } else {
                    remove(getString(R.string.username))
                    remove(getString(R.string.password))
                    putBoolean(getString(R.string.sp_remember_me), false)
                }
                apply()
            }


            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    AppApplication.database.userDao().getUser(username, password)
                }
                if (user != null) {

                    findNavController().navigate(
                        LoginFragmentDirections.actionLoginFragmentToHomeFragment(
                          user
                        )
                    )

                } else {
                    Toast.makeText(requireContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
        }


        binding.registerButtom.setOnClickListener {
            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
        }
    }
}
