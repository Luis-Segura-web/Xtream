package com.kybers.xtream.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.kybers.xtream.data.XtreamDataManager
import com.kybers.xtream.databinding.ActivityTestConnectionBinding
import kotlinx.coroutines.launch

class TestConnectionActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityTestConnectionBinding
    private lateinit var xtreamDataManager: XtreamDataManager
    
    companion object {
        private const val TAG = "TestConnectionActivity"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestConnectionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        xtreamDataManager = XtreamDataManager(this)
        
        // Test with default credentials
        testConnectionWithDefaults()
    }
    
    private fun testConnectionWithDefaults() {
        binding.tvStatus.text = "Probando conexión con credenciales por defecto..."
        
        lifecycleScope.launch {
            try {
                Log.d(TAG, "Testing connection with default credentials")
                binding.tvStatus.text = "🔄 Conectando a ${XtreamDataManager.DEFAULT_SERVER_URL}..."
                
                // Primero limpiar completamente el caché
                val cacheManager = com.kybers.xtream.data.CacheManager(this@TestConnectionActivity)
                cacheManager.clearCache()
                
                val sharedPrefs = getSharedPreferences("xtream_cache", android.content.Context.MODE_PRIVATE)
                sharedPrefs.edit().clear().apply()
                
                binding.tvStatus.text = """
                    🔄 Conectando a servidor...
                    
                    Servidor: ${XtreamDataManager.DEFAULT_SERVER_URL}
                    Usuario: ${XtreamDataManager.DEFAULT_USERNAME}
                    Contraseña: ${XtreamDataManager.DEFAULT_PASSWORD}
                    
                    Limpiando caché y forzando recarga...
                """.trimIndent()
                
                val result = xtreamDataManager.loadContent(forceRefresh = true)
                
                if (result.isSuccess) {
                    val content = result.getOrThrow()
                    val statusMessage = """
                        ✅ ¡Conexión exitosa!
                        
                        📺 Canales: ${content.channels.size}
                        🎬 Películas: ${content.movies.size}
                        📺 Series: ${content.series.size}
                        ⏰ Cargado: ${java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}
                        
                        ${if (content.channels.isNotEmpty()) "Primeros 5 canales:" else "❌ Sin canales"}
                        ${content.channels.take(5).joinToString("\n") { "• ${it.name} (${it.category})" }}
                        
                        ${if (content.movies.isNotEmpty()) "Primeras 5 películas:" else "❌ Sin películas"}
                        ${content.movies.take(5).joinToString("\n") { "• ${it.name} (${it.category})" }}
                        
                        ${if (content.series.isNotEmpty()) "Primeras 5 series:" else "❌ Sin series"}
                        ${content.series.take(5).joinToString("\n") { "• ${it.name} (${it.category})" }}
                    """.trimIndent()
                    
                    binding.tvStatus.text = statusMessage
                    
                    Log.d(TAG, "Content loaded successfully:")
                    Log.d(TAG, "Channels: ${content.channels.size}")
                    Log.d(TAG, "Movies: ${content.movies.size}")
                    Log.d(TAG, "Series: ${content.series.size}")
                    
                    Toast.makeText(this@TestConnectionActivity, "¡Conexión exitosa! ${content.channels.size} canales cargados", Toast.LENGTH_LONG).show()
                } else {
                    val error = result.exceptionOrNull()
                    val errorMessage = """
                        ❌ Error de conexión
                        
                        Mensaje: ${error?.message ?: "Error desconocido"}
                        
                        Detalles:
                        • Servidor: ${XtreamDataManager.DEFAULT_SERVER_URL}
                        • Usuario: ${XtreamDataManager.DEFAULT_USERNAME}
                        • Contraseña: ${XtreamDataManager.DEFAULT_PASSWORD}
                        
                        Stack trace:
                        ${error?.stackTraceToString()?.take(500) ?: "No disponible"}
                    """.trimIndent()
                    
                    binding.tvStatus.text = errorMessage
                    
                    Log.e(TAG, "Connection failed", error)
                    Toast.makeText(this@TestConnectionActivity, "Error: ${error?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                val errorMessage = """
                    ❌ Excepción durante la conexión
                    
                    Mensaje: ${e.message}
                    
                    Stack trace:
                    ${e.stackTraceToString().take(500)}
                """.trimIndent()
                
                binding.tvStatus.text = errorMessage
                
                Log.e(TAG, "Exception during connection test", e)
                Toast.makeText(this@TestConnectionActivity, "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}