package com.kybers.xtream

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.kybers.xtream.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySplashBinding
    private val splashDelay = 3000L // 3 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        startAnimations()
        
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, splashDelay)
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
        
        // App name animation
        val translateYAppName = ObjectAnimator.ofFloat(binding.tvAppName, "translationY", 50f, 0f)
        val alphaAppName = ObjectAnimator.ofFloat(binding.tvAppName, "alpha", 0f, 1f)
        
        val appNameAnimatorSet = AnimatorSet()
        appNameAnimatorSet.playTogether(translateYAppName, alphaAppName)
        appNameAnimatorSet.duration = 600
        appNameAnimatorSet.interpolator = DecelerateInterpolator()
        appNameAnimatorSet.startDelay = 400
        
        // Subtitle animation
        val alphaSubtitle = ObjectAnimator.ofFloat(binding.tvSubtitle, "alpha", 0f, 1f)
        alphaSubtitle.duration = 500
        alphaSubtitle.startDelay = 800
        
        // Progress bar animation
        val alphaProgress = ObjectAnimator.ofFloat(binding.progressBar, "alpha", 0f, 1f)
        val alphaLoading = ObjectAnimator.ofFloat(binding.tvLoading, "alpha", 0f, 1f)
        
        val progressAnimatorSet = AnimatorSet()
        progressAnimatorSet.playTogether(alphaProgress, alphaLoading)
        progressAnimatorSet.duration = 400
        progressAnimatorSet.startDelay = 1200
        
        // Background circles animation
        val rotateCircle1 = ObjectAnimator.ofFloat(binding.circle1, "rotation", 0f, 360f)
        rotateCircle1.duration = 8000
        rotateCircle1.repeatCount = ObjectAnimator.INFINITE
        
        val rotateCircle2 = ObjectAnimator.ofFloat(binding.circle2, "rotation", 360f, 0f)
        rotateCircle2.duration = 12000
        rotateCircle2.repeatCount = ObjectAnimator.INFINITE
        
        // Start all animations
        logoAnimatorSet.start()
        appNameAnimatorSet.start()
        alphaSubtitle.start()
        progressAnimatorSet.start()
        rotateCircle1.start()
        rotateCircle2.start()
    }
}