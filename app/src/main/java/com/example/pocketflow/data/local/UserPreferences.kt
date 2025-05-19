package com.example.pocketflow.data.local

import android.content.Context
import android.content.SharedPreferences

class UserPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveUserSession(token: String, uid: String, nombre: String) {
        prefs.edit().apply {
            putString("access_token", token)
            putString("uid", uid)
            putString("nombre", nombre)
            apply()
        }
    }

    fun getAccessToken(): String? = prefs.getString("access_token", null)
    fun getUid(): String? = prefs.getString("uid", null)
    fun getNombre(): String? = prefs.getString("nombre", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}

