package com.kybers.xtream

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.kybers.xtream.databinding.ActivityLoginBinding
import com.kybers.xtream.data.ProfileManager
import com.kybers.xtream.data.model.UserProfile

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private lateinit var profileManager: ProfileManager
    private var profileAdapter: ProfileAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        profileManager = ProfileManager(this)
        setupViews()
        loadProfiles()
        startAnimations()
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                saveProfile()
                proceedToLoading()
            }
        }
        
        binding.tvAddProfile.setOnClickListener {
            showFormAnimated()
        }
    }
    
    private fun loadProfiles() {
        val profiles = profileManager.getAllProfiles()
        if (profiles.isNotEmpty()) {
            showProfileSelection(profiles)
        } else {
            showForm()
        }
    }
    
    
    private fun validateForm(): Boolean {
        val profileName = binding.etProfileName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val serverUrl = binding.etServerUrl.text.toString().trim()
        
        when {
            profileName.isEmpty() -> {
                binding.etProfileName.error = "Ingrese nombre del perfil"
                return false
            }
            username.isEmpty() -> {
                binding.etUsername.error = "Ingrese usuario"
                return false
            }
            password.isEmpty() -> {
                binding.etPassword.error = "Ingrese contraseña"
                return false
            }
            serverUrl.isEmpty() -> {
                binding.etServerUrl.error = "Ingrese URL del servidor"
                return false
            }
            !serverUrl.startsWith("http") -> {
                binding.etServerUrl.error = "URL debe comenzar con http o https"
                return false
            }
        }
        return true
    }
    
    private fun saveProfile() {
        val profile = UserProfile(
            profileName = binding.etProfileName.text.toString().trim(),
            username = binding.etUsername.text.toString().trim(),
            password = binding.etPassword.text.toString().trim(),
            serverUrl = binding.etServerUrl.text.toString().trim()
        )
        
        try {
            profileManager.saveProfile(profile)
            profileManager.setCurrentProfile(profile)
            Toast.makeText(this, "Perfil guardado exitosamente", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar perfil: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun proceedToLoading() {
        startActivity(Intent(this, LoadingActivity::class.java))
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        finish()
    }
    
    private fun startAnimations() {
        // Header animation
        val translateYHeader = ObjectAnimator.ofFloat(binding.cardHeader, "translationY", -100f, 0f)
        val alphaHeader = ObjectAnimator.ofFloat(binding.cardHeader, "alpha", 0f, 1f)
        
        val headerAnimatorSet = AnimatorSet()
        headerAnimatorSet.playTogether(translateYHeader, alphaHeader)
        headerAnimatorSet.duration = 600
        headerAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Form animation
        val translateYForm = ObjectAnimator.ofFloat(binding.cardForm, "translationY", 100f, 0f)
        val alphaForm = ObjectAnimator.ofFloat(binding.cardForm, "alpha", 0f, 1f)
        
        val formAnimatorSet = AnimatorSet()
        formAnimatorSet.playTogether(translateYForm, alphaForm)
        formAnimatorSet.duration = 600
        formAnimatorSet.interpolator = DecelerateInterpolator()
        formAnimatorSet.startDelay = 300
        
        // Start animations
        headerAnimatorSet.start()
        formAnimatorSet.start()
    }
    
    private fun showProfileSelection(profiles: List<UserProfile>) {
        binding.cardProfiles.visibility = View.VISIBLE
        binding.cardForm.visibility = View.GONE
        
        // Animate profiles card
        val alphaProfiles = ObjectAnimator.ofFloat(binding.cardProfiles, "alpha", 0f, 1f)
        val scaleXProfiles = ObjectAnimator.ofFloat(binding.cardProfiles, "scaleX", 0.8f, 1f)
        val scaleYProfiles = ObjectAnimator.ofFloat(binding.cardProfiles, "scaleY", 0.8f, 1f)
        
        val profilesAnimatorSet = AnimatorSet()
        profilesAnimatorSet.playTogether(alphaProfiles, scaleXProfiles, scaleYProfiles)
        profilesAnimatorSet.duration = 400
        profilesAnimatorSet.interpolator = DecelerateInterpolator()
        profilesAnimatorSet.start()
        
        profileAdapter = ProfileAdapter(profiles) { profile ->
            profileManager.setCurrentProfile(profile)
            proceedToLoading()
        }
        
        binding.rvProfiles.apply {
            layoutManager = LinearLayoutManager(this@LoginActivity)
            adapter = profileAdapter
        }
    }
    
    private fun showFormAnimated() {
        binding.cardProfiles.visibility = View.GONE
        binding.cardForm.visibility = View.VISIBLE
        
        // Animate form card
        val alphaForm = ObjectAnimator.ofFloat(binding.cardForm, "alpha", 0f, 1f)
        val scaleXForm = ObjectAnimator.ofFloat(binding.cardForm, "scaleX", 0.8f, 1f)
        val scaleYForm = ObjectAnimator.ofFloat(binding.cardForm, "scaleY", 0.8f, 1f)
        
        val formAnimatorSet = AnimatorSet()
        formAnimatorSet.playTogether(alphaForm, scaleXForm, scaleYForm)
        formAnimatorSet.duration = 400
        formAnimatorSet.interpolator = DecelerateInterpolator()
        formAnimatorSet.start()
    }
    
    private fun showForm() {
        binding.cardProfiles.visibility = View.GONE
        binding.cardForm.visibility = View.VISIBLE
    }
}