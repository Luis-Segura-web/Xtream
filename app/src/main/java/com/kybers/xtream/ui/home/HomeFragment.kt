package com.kybers.xtream.ui.home

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.kybers.xtream.R
import com.kybers.xtream.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

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
        setupRecyclerViews()
        setupQuickAccess()
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