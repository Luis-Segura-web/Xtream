package com.kybers.xtream.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kybers.xtream.data.XtreamManager
import com.kybers.xtream.R
import com.kybers.xtream.databinding.FragmentHomeBinding
import com.kybers.xtream.ui.dashboard.TestConnectionActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var xtreamManager: XtreamManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        xtreamManager = XtreamManager.getInstance(requireContext())
        
        setupRecyclerViews()
        setupQuickAccess()
        setupDiagnosticButtons()
        startAnimations()
    }
    
    private fun setupRecyclerViews() {
        // TODO: Setup trending RecyclerView
        binding.rvTrending.layoutManager = LinearLayoutManager(
            requireContext(), 
            LinearLayoutManager.HORIZONTAL, 
            false
        )
        
        // TODO: Setup recommended RecyclerView  
        binding.rvRecommended.layoutManager = LinearLayoutManager(
            requireContext(), 
            LinearLayoutManager.HORIZONTAL, 
            false
        )
    }
    
    private fun setupQuickAccess() {
        binding.cardTv.setOnClickListener {
            findNavController().navigate(R.id.navigation_tv)
        }
        
        binding.cardMovies.setOnClickListener {
            findNavController().navigate(R.id.navigation_movies)
        }
        
        binding.cardSeries.setOnClickListener {
            findNavController().navigate(R.id.navigation_series)
        }
    }
    
    private fun setupDiagnosticButtons() {
        binding.btnClearCacheHome.setOnClickListener {
            clearCacheAndReload()
        }
        
        binding.btnTestConnectionHome.setOnClickListener {
            startActivity(Intent(requireContext(), TestConnectionActivity::class.java))
        }
    }
    
    private fun clearCacheAndReload() {
        lifecycleScope.launch {
            try {
                // Limpiar completamente el caché
                xtreamManager.clearCache()
                
                val sharedPrefs = requireContext().getSharedPreferences("xtream_cache", android.content.Context.MODE_PRIVATE)
                sharedPrefs.edit().clear().apply()
                
                Toast.makeText(requireContext(), "🔄 Limpiando caché y recargando datos...", Toast.LENGTH_SHORT).show()
                
                // Forzar recarga con credenciales por defecto
                val result = xtreamManager.loadContentWithDefaults(forceRefresh = true)
                
                if (result.isSuccess) {
                    val content = result.getOrThrow()
                    Toast.makeText(
                        requireContext(), 
                        "✅ Datos cargados: ${content.channels.size} canales, ${content.movies.size} películas, ${content.series.size} series", 
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error desconocido"
                    Toast.makeText(requireContext(), "❌ Error: $error", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "❌ Excepción: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
    
    private fun startAnimations() {
        // Header animation
        val headerAlpha = ObjectAnimator.ofFloat(binding.cardHeader, "alpha", 0f, 1f)
        val headerTranslateY = ObjectAnimator.ofFloat(binding.cardHeader, "translationY", -50f, 0f)
        
        val headerAnimatorSet = AnimatorSet()
        headerAnimatorSet.playTogether(headerAlpha, headerTranslateY)
        headerAnimatorSet.duration = 600
        headerAnimatorSet.interpolator = DecelerateInterpolator()
        
        // Stagger other card animations
        val cards = listOf(
            binding.rvTrending.parent as View,
            binding.rvRecommended.parent as View,
            binding.cardTv.parent.parent as View
        )
        
        cards.forEachIndexed { index, card ->
            val alpha = ObjectAnimator.ofFloat(card, "alpha", 0f, 1f)
            val translateY = ObjectAnimator.ofFloat(card, "translationY", 50f, 0f)
            
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(alpha, translateY)
            animatorSet.duration = 500
            animatorSet.startDelay = 200L * (index + 1)
            animatorSet.interpolator = DecelerateInterpolator()
            animatorSet.start()
        }
        
        headerAnimatorSet.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}