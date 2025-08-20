package com.kybers.xtream

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class XtreamApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
    }
}