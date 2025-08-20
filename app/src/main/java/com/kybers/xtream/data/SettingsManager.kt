package com.kybers.xtream.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("xtream_settings", Context.MODE_PRIVATE)
    
    companion object {
        private const val KEY_SERVER_URL = "server_url"
        private const val KEY_USERNAME = "username"
        private const val KEY_PASSWORD = "password"
        private const val KEY_FIRST_SETUP = "first_setup_completed"
    }
    
    fun saveCredentials(serverUrl: String, username: String, password: String) {
        sharedPreferences.edit()
            .putString(KEY_SERVER_URL, serverUrl)
            .putString(KEY_USERNAME, username)
            .putString(KEY_PASSWORD, password)
            .putBoolean(KEY_FIRST_SETUP, true)
            .apply()
    }
    
    fun getServerUrl(): String? = sharedPreferences.getString(KEY_SERVER_URL, null)
    fun getUsername(): String? = sharedPreferences.getString(KEY_USERNAME, null)
    fun getPassword(): String? = sharedPreferences.getString(KEY_PASSWORD, null)
    
    fun hasCredentials(): Boolean {
        return getServerUrl() != null && getUsername() != null && getPassword() != null
    }
    
    fun isFirstSetupCompleted(): Boolean {
        return sharedPreferences.getBoolean(KEY_FIRST_SETUP, false)
    }
    
    fun clearCredentials() {
        sharedPreferences.edit()
            .remove(KEY_SERVER_URL)
            .remove(KEY_USERNAME)
            .remove(KEY_PASSWORD)
            .remove(KEY_FIRST_SETUP)
            .apply()
    }
}