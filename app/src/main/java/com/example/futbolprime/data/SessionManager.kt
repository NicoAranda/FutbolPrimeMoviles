package com.example.futbolprime.data

import android.content.Context

class SessionManager(context: Context) {

    private val prefs = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveSession(token: String, nombre: String) {
        prefs.edit()
            .putString("token", token)
            .putString("nombre", nombre)
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getNombre(): String? {
        return prefs.getString("nombre", null)
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
