package com.kybers.xtream

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.ProfileManager
import com.kybers.xtream.data.XtreamApiService
import com.kybers.xtream.data.model.Channel
import com.kybers.xtream.data.model.Movie
import com.kybers.xtream.data.model.Series
import com.kybers.xtream.data.model.XtreamContent
import com.kybers.xtream.databinding.ActivityLoadingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoadingActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoadingBinding
    private lateinit var cacheManager: CacheManager
    private lateinit var profileManager: ProfileManager
    private lateinit var apiService: XtreamApiService
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        
        cacheManager = CacheManager(this)
        profileManager = ProfileManager(this)
        
        checkCacheAndLoad()
    }
    
    private fun checkCacheAndLoad() {
        if (cacheManager.isCacheValid()) {
            updateStatus("Usando contenido guardado...", 100)
            Handler(Looper.getMainLooper()).postDelayed({
                proceedToMain()
            }, 1000)
        } else {
            loadContentFromServer()
        }
    }
    
    private fun loadContentFromServer() {
        val currentProfile = profileManager.getCurrentProfile()
        if (currentProfile == null) {
            // Regresar al login si no hay perfil
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        
        apiService = XtreamApiService.create(currentProfile.serverUrl)
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateStatus("Conectando al servidor...", 10)
                
                // Simular autenticación
                updateStatus("Autenticando usuario...", 20)
                Thread.sleep(1000)
                
                // Cargar canales
                updateStatus("Cargando canales de TV...", 40)
                val channels = loadChannels()
                Thread.sleep(500)
                
                // Cargar películas
                updateStatus("Cargando películas...", 70)
                val movies = loadMovies()
                Thread.sleep(500)
                
                // Cargar series
                updateStatus("Cargando series...", 90)
                val series = loadSeries()
                Thread.sleep(500)
                
                // Guardar en cache
                updateStatus("Guardando contenido...", 95)
                val content = XtreamContent(
                    channels = channels,
                    movies = movies,
                    series = series
                )
                cacheManager.saveContent(content)
                
                updateStatus("¡Completado!", 100)
                Thread.sleep(500)
                
                withContext(Dispatchers.Main) {
                    proceedToMain()
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateStatus("Error: ${e.message}", 0)
                    // Regresar al login después de 3 segundos
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity(Intent(this@LoadingActivity, LoginActivity::class.java))
                        finish()
                    }, 3000)
                }
            }
        }
    }
    
    private fun updateStatus(status: String, progress: Int) {
        runOnUiThread {
            binding.tvLoadingStatus.text = status
            binding.progressHorizontal.progress = progress
            binding.tvProgressPercent.text = "$progress%"
        }
    }
    
    private suspend fun loadChannels(): List<Channel> {
        // TODO: Implementar llamada real a la API de Xtream
        return listOf(
            Channel("1", "Canal 1", "http://example.com/stream1", "General"),
            Channel("2", "Canal 2", "http://example.com/stream2", "Deportes"),
            Channel("3", "Canal 3", "http://example.com/stream3", "Noticias")
        )
    }
    
    private suspend fun loadMovies(): List<Movie> {
        // TODO: Implementar llamada real a la API de Xtream
        return listOf(
            Movie("1", "Película 1", "http://example.com/movie1", "Acción", tmdbId = "12345"),
            Movie("2", "Película 2", "http://example.com/movie2", "Comedia", tmdbId = "67890"),
            Movie("3", "Película 3", "http://example.com/movie3", "Drama", tmdbId = "11111")
        )
    }
    
    private suspend fun loadSeries(): List<Series> {
        // TODO: Implementar llamada real a la API de Xtream
        return listOf(
            Series("1", "Serie 1", "Acción", tmdbId = "22222"),
            Series("2", "Serie 2", "Comedia", tmdbId = "33333"),
            Series("3", "Serie 3", "Drama", tmdbId = "44444")
        )
    }
    
    private fun proceedToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}