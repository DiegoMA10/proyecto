package com.example.proyecto

import android.media.MediaPlayer
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.proyecto.Entities.ConfigurationEntity
import com.example.proyecto.Entities.FavoritesEntity
import com.example.proyecto.Entities.UserEntity
import com.example.proyecto.data.AppApplication
import com.example.proyecto.databinding.FragmentHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class HomeFragment : Fragment(), OnClickListener {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var adapter: HomeAdapter
    private lateinit var mediaPlayer: MediaPlayer
    private var currentPosition: Int = 0
    private lateinit var user: UserEntity

    val args: HomeFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        user = args.Usuario
        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.main_theme)
        mediaPlayer.setVolume(0.5f, 0.5f)
        mediaPlayer.isLooping = true
        mediaPlayer.seekTo(currentPosition)
        mediaPlayer.start()

        setupRecyclerView()
        binding.createConfigurationButton.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToAddConfigurationFragment(
                user
            )
            findNavController().navigate(action)
        }



        setConfiguraciones()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = HomeAdapter(mutableListOf(), mutableListOf(), this)

        binding.recyclerView?.apply {
            setHasFixedSize(true)
            this.layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HomeFragment.adapter
        }
    }


    private fun setConfiguraciones(){
        lifecycleScope.launch(Dispatchers.IO) {
            val configuraciones = AppApplication.database.configurationDao().getConfiguration()
            val favorites = AppApplication.database.favoritesDao().getFavoritesByUser(user.id)
            withContext(Dispatchers.Main) {
                adapter.setConfiguration(configuraciones)
                adapter.setFavorites(favorites)
            }
        }
    }


    override fun onClick(configurationEntity: ConfigurationEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val configurationWithComponents = AppApplication.database.configurationDao().getConfigurationWithComponents(configurationEntity.id)
            val action = configurationWithComponents?.let {
                HomeFragmentDirections.actionHomeFragmentToConfigurationDetailFragment(
                    Usuario = user,
                    ConfigurationWithComponets = it
                )
            }

            lifecycleScope.launch(Dispatchers.Main) {
                action?.let { findNavController().navigate(it) }
            }
        }
    }


    override fun onFavorite(configuratonEntity: ConfigurationEntity) {
       lifecycleScope.launch(Dispatchers.IO) {
           try {
               val currentFavorite = AppApplication.database.favoritesDao().getFavoritesByUser(user.id)
               val isFavorites = currentFavorite.any { it.configurationId == configuratonEntity.id }
               val favoriteEntity = FavoritesEntity(user.id, configuratonEntity.id)

               if(!isFavorites){
                    AppApplication.database.favoritesDao().insertFavorite(favoriteEntity)
               }else{
                   AppApplication.database.favoritesDao().deleteFavorite(favoriteEntity)
               }
               withContext(Dispatchers.Main) {
                   setConfiguraciones()
               }

           }catch (e: Exception){
               withContext(Dispatchers.Main) {
                   Toast.makeText(requireContext(), "Error al agregar a favoritos", Toast.LENGTH_SHORT).show()
               }
           }
       }
    }

    override fun onDelete(configuratonEntity: ConfigurationEntity) {
        adapter.deleteConfiguration(configuratonEntity)
        lifecycleScope.launch(Dispatchers.IO) {
            AppApplication.database.favoritesDao().deleteFavoritesByConfigurationId(configuratonEntity.id)
            AppApplication.database.configurationDao().deleteConfigurationWithComponents(configuratonEntity)
        }
    }

    override fun onPause() {
        super.onPause()
        currentPosition = mediaPlayer.currentPosition
        mediaPlayer.pause()

    }

    override fun onResume() {
        super.onResume()
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.seekTo(currentPosition)
            mediaPlayer.start()
        }
    }
}
