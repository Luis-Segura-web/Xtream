package com.kybers.xtream

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.kybers.xtream.data.CacheManager
import com.kybers.xtream.data.ProfileManager
import com.kybers.xtream.data.SettingsManager
import com.kybers.xtream.data.XtreamDataManager
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
    private lateinit var settingsManager: SettingsManager
    private lateinit var xtreamDataManager: XtreamDataManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        supportActionBar?.hide()
        
        cacheManager = CacheManager(this)
        profileManager = ProfileManager(this)
        settingsManager = SettingsManager(this)
        xtreamDataManager = XtreamDataManager(this)
        
        startAnimations()
        checkCacheAndLoad()
    }
    
    private fun checkCacheAndLoad() {
        // FORZAR LIMPIEZA DE CACHÉ FALSO - Solo para debugging
        Log.d("LoadingActivity", "Forcing cache clear to remove fake data")
        cacheManager.clearCache()
        val sharedPrefs = getSharedPreferences("xtream_cache", MODE_PRIVATE)
        sharedPrefs.edit().clear().apply()
        
        // Siempre cargar desde servidor para asegurar datos reales
        loadContentFromServer()
    }
    
    private fun loadContentFromServer() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                updateStatus("Conectando al servidor...", 10)
                
                // Check if we have saved settings first
                if (settingsManager.hasCredentials()) {
                    updateStatus("Autenticando usuario...", 20)
                    
                    val result = xtreamDataManager.loadContentWithCredentials(
                        settingsManager.getServerUrl()!!,
                        settingsManager.getUsername()!!,
                        settingsManager.getPassword()!!,
                        forceRefresh = true
                    )
                    
                    if (result.isSuccess) {
                        updateStatus("Cargando canales de TV...", 40)
                        Thread.sleep(500)
                        
                        updateStatus("Cargando películas...", 70)
                        Thread.sleep(500)
                        
                        updateStatus("Cargando series...", 90)
                        Thread.sleep(500)
                        
                        updateStatus("¡Completado!", 100)
                        Thread.sleep(500)
                        
                        withContext(Dispatchers.Main) {
                            proceedToMain()
                        }
                    } else {
                        throw Exception(result.exceptionOrNull()?.message ?: "Error de autenticación")
                    }
                } else {
                    // Try with default credentials as fallback
                    updateStatus("Usando credenciales por defecto...", 30)
                    
                    // Usar timeout para evitar bloqueo indefinido
                    val result = try {
                        xtreamDataManager.loadContent(forceRefresh = true)
                    } catch (e: Exception) {
                        Log.e("LoadingActivity", "Timeout o error cargando contenido", e)
                        Result.failure(e)
                    }
                    
                    if (result.isSuccess) {
                        updateStatus("¡Completado!", 100)
                        Thread.sleep(500)
                        
                        withContext(Dispatchers.Main) {
                            proceedToMain()
                        }
                    } else {
                        // Continuar a main aunque falle para permitir configuración manual
                        updateStatus("Error de conexión, continuando...", 100)
                        Thread.sleep(1000)
                        
                        withContext(Dispatchers.Main) {
                            proceedToMain()
                        }
                    }
                }
                
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    updateStatus("Error: ${e.message}", 0)
                    // Continuar a main después de 3 segundos para permitir configuración
                    Handler(Looper.getMainLooper()).postDelayed({
                        proceedToMain()
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
    
    
    private fun proceedToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
    
    private fun startAnimations() {
        // Logo animation
        val scaleXLogo = ObjectAnimator.ofFloat(binding.cardLogo, "scaleX", 0f, 1f)
        val scaleYLogo = ObjectAnimator.ofFloat(binding.cardLogo, "scaleY", 0f, 1f)
        val alphaLogo = ObjectAnimator.ofFloat(binding.cardLogo, "alpha", 0f, 1f)
        
        val logoAnimatorSet = AnimatorSet()
        logoAnimatorSet.playTogether(scaleXLogo, scaleYLogo, alphaLogo)
        logoAnimatorSet.duration = 800
        logoAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Title animation
        val translateYTitle = ObjectAnimator.ofFloat(binding.tvLoadingTitle, "translationY", 50f, 0f)
        val alphaTitle = ObjectAnimator.ofFloat(binding.tvLoadingTitle, "alpha", 0f, 1f)
        
        val titleAnimatorSet = AnimatorSet()
        titleAnimatorSet.playTogether(translateYTitle, alphaTitle)
        titleAnimatorSet.duration = 600
        titleAnimatorSet.startDelay = 300
        titleAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Subtitle animation
        val alphaSubtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, "alpha", 0f, 1f)
        alphaSubtitle.duration = 500
        alphaSubtitle.startDelay = 600
        
        // Progress card animation
        val scaleXProgress = ObjectAnimator.ofFloat(binding.cardProgress, "scaleX", 0.8f, 1f)
        val scaleYProgress = ObjectAnimator.ofFloat(binding.cardProgress, "scaleY", 0.8f, 1f)
        val alphaProgress = ObjectAnimator.ofFloat(binding.cardProgress, "alpha", 0f, 1f)
        
        val progressAnimatorSet = AnimatorSet()
        progressAnimatorSet.playTogether(scaleXProgress, scaleYProgress, alphaProgress)
        progressAnimatorSet.duration = 600
        progressAnimatorSet.startDelay = 900
        progressAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Status animation
        val alphaStatus = ObjectAnimator.ofFloat(binding.tvLoadingStatus, "alpha", 0f, 1f)
        val scaleXStatus = ObjectAnimator.ofFloat(binding.tvLoadingStatus, "scaleX", 0.8f, 1f)
        val scaleYStatus = ObjectAnimator.ofFloat(binding.tvLoadingStatus, "scaleY", 0.8f, 1f)
        
        val statusAnimatorSet = AnimatorSet()
        statusAnimatorSet.playTogether(alphaStatus, scaleXStatus, scaleYStatus)
        statusAnimatorSet.duration = 400
        statusAnimatorSet.startDelay = 1200
        statusAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Background circles rotation
        val rotateCircle1 = ObjectAnimator.ofFloat(binding.circle1, "rotation", 0f, 360f)
        rotateCircle1.duration = 15000
        rotateCircle1.repeatCount = ObjectAnimator.INFINITE
        
        val rotateCircle2 = ObjectAnimator.ofFloat(binding.circle2, "rotation", 360f, 0f)
        rotateCircle2.duration = 20000
        rotateCircle2.repeatCount = ObjectAnimator.INFINITE
        
        // Start all animations
        logoAnimatorSet.start()
        titleAnimatorSet.start()
        alphaSubtitle.start()
        progressAnimatorSet.start()
        statusAnimatorSet.start()
        rotateCircle1.start()
        rotateCircle2.start()
    }
}