package com.kybers.xtream.ui.dashboard

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.kybers.xtream.R
import com.kybers.xtream.data.SettingsManager
import com.kybers.xtream.data.XtreamManager
import com.kybers.xtream.databinding.FragmentDashboardSimpleBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardSimpleBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var settingsManager: SettingsManager
    private lateinit var xtreamManager: XtreamManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardSimpleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        settingsManager = SettingsManager(requireContext())
        xtreamManager = XtreamManager.getInstance(requireContext())
        
        setupUI()
        loadSavedCredentials()
        updateConnectionStatus()
        startAnimations()
    }
    
    private fun setupUI() {
        binding.btnTestConnection.setOnClickListener {
            testConnection()
        }
        
        binding.btnSaveConfig.setOnClickListener {
            saveConfiguration()
        }
        
        binding.btnRefreshContent.setOnClickListener {
            refreshContent()
        }
        
        binding.btnClearCache.setOnClickListener {
            clearCache()
        }
        
        binding.btnTestDefault.setOnClickListener {
            startActivity(Intent(requireContext(), TestConnectionActivity::class.java))
        }
    }
    
    private fun loadSavedCredentials() {
        binding.etServerUrl.setText(settingsManager.getServerUrl() ?: "")
        binding.etUsername.setText(settingsManager.getUsername() ?: "")
        binding.etPassword.setText(settingsManager.getPassword() ?: "")
    }
    
    private fun testConnection() {
        val serverUrl = binding.etServerUrl.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnTestConnection.isEnabled = false
        binding.btnTestConnection.text = "Probando..."
        
        lifecycleScope.launch {
            try {
                val result = xtreamManager.connectAndLoadContent(serverUrl, username, password, true)
                
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Conexión exitosa", Toast.LENGTH_SHORT).show()
                    updateConnectionStatus(true)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                    updateConnectionStatus(false, error)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
                updateConnectionStatus(false, e.message)
            } finally {
                binding.btnTestConnection.isEnabled = true
                binding.btnTestConnection.text = "Probar"
            }
        }
    }
    
    private fun saveConfiguration() {
        val serverUrl = binding.etServerUrl.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        
        if (serverUrl.isEmpty() || username.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }
        
        settingsManager.saveCredentials(serverUrl, username, password)
        Toast.makeText(requireContext(), "Configuración guardada", Toast.LENGTH_SHORT).show()
        
        // Auto-connect after saving
        lifecycleScope.launch {
            val result = xtreamManager.connectAndLoadContent(serverUrl, username, password, false)
            if (result.isSuccess) {
                updateConnectionStatus(true)
            }
        }
    }
    
    private fun refreshContent() {
        if (!settingsManager.hasCredentials()) {
            Toast.makeText(requireContext(), "Configura la conexión primero", Toast.LENGTH_SHORT).show()
            return
        }
        
        binding.btnRefreshContent.isEnabled = false
        binding.btnRefreshContent.text = "Actualizando..."
        
        lifecycleScope.launch {
            try {
                val result = xtreamManager.connectAndLoadContent(
                    settingsManager.getServerUrl()!!,
                    settingsManager.getUsername()!!,
                    settingsManager.getPassword()!!,
                    true // Force refresh
                )
                
                if (result.isSuccess) {
                    Toast.makeText(requireContext(), "Contenido actualizado", Toast.LENGTH_SHORT).show()
                    updateConnectionStatus(true)
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    Toast.makeText(requireContext(), "Error: $error", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                binding.btnRefreshContent.isEnabled = true
                binding.btnRefreshContent.text = "Actualizar Contenido"
            }
        }
    }
    
    private fun clearCache() {
        xtreamManager.clearCache()
        
        // También limpiar SharedPreferences completamente para asegurar que no haya datos viejos
        val sharedPrefs = requireContext().getSharedPreferences("xtream_cache", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        
        Toast.makeText(requireContext(), "Caché completamente limpiado", Toast.LENGTH_SHORT).show()
        updateConnectionStatus(false, "Caché completamente limpiado")
        
        // Forzar recarga inmediata con credenciales por defecto
        lifecycleScope.launch {
            try {
                val result = xtreamManager.loadContentWithDefaults(forceRefresh = true)
                if (result.isSuccess) {
                    val content = result.getOrThrow()
                    Toast.makeText(requireContext(), "Datos reales cargados: ${content.channels.size} canales, ${content.movies.size} películas, ${content.series.size} series", Toast.LENGTH_LONG).show()
                    updateConnectionStatus(true)
                } else {
                    Toast.makeText(requireContext(), "Error cargando datos: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun updateConnectionStatus(connected: Boolean = false, message: String? = null) {
        if (connected) {
            binding.tvConnectionStatus.text = "Conectado"
            binding.tvConnectionStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_green_dark)
            )
            
            val currentTime = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            binding.tvLastUpdate.text = "Última actualización: $currentTime"
        } else {
            binding.tvConnectionStatus.text = message ?: "No conectado"
            binding.tvConnectionStatus.setTextColor(
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
            )
            
            if (message != null && message != "No conectado") {
                binding.tvLastUpdate.text = "Error: $message"
            } else {
                binding.tvLastUpdate.text = ""
            }
        }
    }
    
    private fun startAnimations() {
        // Simple fade in animation for the entire layout
        val rootAlpha = ObjectAnimator.ofFloat(binding.root, "alpha", 0f, 1f)
        val rootTranslateY = ObjectAnimator.ofFloat(binding.root, "translationY", 50f, 0f)
        
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(rootAlpha, rootTranslateY)
        animatorSet.duration = 600
        animatorSet.interpolator = DecelerateInterpolator()
        animatorSet.start()
    }

    override fun onResume() {
        super.onResume()
        // Check connection status when returning to fragment
        if (settingsManager.hasCredentials()) {
            val hasCache = xtreamManager.isCacheValid()
            updateConnectionStatus(hasCache)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}