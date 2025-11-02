package com.example.patientvisitapp.util

import android.content.Context

class AuthManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    fun saveToken(token: String) = prefs.edit().putString("auth_token", token).apply()
    fun getToken(): String? = prefs.getString("auth_token", null)
    fun clear() = prefs.edit().remove("auth_token").apply()
}
