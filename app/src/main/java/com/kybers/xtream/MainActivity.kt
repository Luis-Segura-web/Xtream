package com.kybers.xtream

import android.animation.ObjectAnimator
import android.os.Bundle
import android.util.Log
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.kybers.xtream.data.XtreamDataManager
import com.kybers.xtream.databinding.ActivityMainBinding
import com.kybers.xtream.ui.common.RefreshListener
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), RefreshListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var dataManager: XtreamDataManager
    
    companion object {
        var instance: MainActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Store instance for global access
        instance = this
        
        // Ocultar action bar para diseño limpio
        supportActionBar?.hide()

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
        
        // Initialize data manager
        dataManager = XtreamDataManager(this)
        
        startAnimations()
        loadInitialData()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
    
    private fun loadInitialData() {
        lifecycleScope.launch {
            try {
                Log.d("MainActivity", "Loading initial data...")
                val result = dataManager.loadContent()
                
                if (result.isSuccess) {
                    val content = result.getOrThrow()
                    Log.d("MainActivity", "Data loaded successfully: ${content.channels.size} channels, ${content.movies.size} movies, ${content.series.size} series")
                    
                    // Show success message
                    Toast.makeText(
                        this@MainActivity,
                        "Contenido cargado: ${content.channels.size} canales, ${content.movies.size} películas, ${content.series.size} series",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Log.e("MainActivity", "Failed to load data", result.exceptionOrNull())
                    Toast.makeText(
                        this@MainActivity,
                        "Error al cargar contenido: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error loading initial data", e)
                Toast.makeText(
                    this@MainActivity,
                    "Error al cargar datos: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    
    override suspend fun onRefreshRequested(): Boolean {
        return try {
            Log.d("MainActivity", "Manual refresh requested")
            val result = dataManager.refreshContent()
            
            if (result.isSuccess) {
                val content = result.getOrThrow()
                Log.d("MainActivity", "Manual refresh successful: ${content.channels.size} channels, ${content.movies.size} movies, ${content.series.size} series")
                
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Contenido actualizado: ${content.channels.size} canales, ${content.movies.size} películas, ${content.series.size} series",
                        Toast.LENGTH_LONG
                    ).show()
                }
                true
            } else {
                Log.e("MainActivity", "Manual refresh failed", result.exceptionOrNull())
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Error al actualizar: ${result.exceptionOrNull()?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                false
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error in manual refresh", e)
            runOnUiThread {
                Toast.makeText(
                    this,
                    "Error al actualizar: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
            false
        }
    }
    
    private fun startAnimations() {
        // Animación de entrada para el bottom navigation
        val translateY = ObjectAnimator.ofFloat(binding.navView, "translationY", 200f, 0f)
        val alpha = ObjectAnimator.ofFloat(binding.navView, "alpha", 0f, 1f)
        
        translateY.duration = 600
        translateY.interpolator = DecelerateInterpolator()
        alpha.duration = 600
        
        translateY.start()
        alpha.start()
    }
}