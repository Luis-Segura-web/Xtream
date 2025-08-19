package com.kybers.xtream

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        
        supportActionBar?.hide()
        
        profileManager = ProfileManager(this)
        setupViews()
        loadProfiles()
    }
    
    private fun setupViews() {
        binding.btnLogin.setOnClickListener {
            if (validateForm()) {
                saveProfile()
                proceedToLoading()
            }
        }
        
        binding.tvAddProfile.setOnClickListener {
            showForm()
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
    
    private fun showProfileSelection(profiles: List<UserProfile>) {
        binding.rvProfiles.visibility = View.VISIBLE
        binding.tvAddProfile.visibility = View.VISIBLE
        binding.llForm.visibility = View.GONE
        
        profileAdapter = ProfileAdapter(profiles) { profile ->
            profileManager.setCurrentProfile(profile)
            proceedToLoading()
        }
        
        binding.rvProfiles.apply {
            layoutManager = LinearLayoutManager(this@LoginActivity)
            adapter = profileAdapter
        }
    }
    
    private fun showForm() {
        binding.rvProfiles.visibility = View.GONE
        binding.tvAddProfile.visibility = View.GONE
        binding.llForm.visibility = View.VISIBLE
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
        finish()
    }
}