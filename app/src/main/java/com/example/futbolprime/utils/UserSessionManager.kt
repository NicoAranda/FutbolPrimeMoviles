package com.example.futbolprime.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.futbolprime.network.LoginResponseDTO

/**
 * Singleton para gestionar la sesión del usuario logueado
 * Usa SharedPreferences para persistir los datos
 */
object UserSessionManager {
    private const val PREF_NAME = "futbolprime_session"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ROL = "user_rol"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Guarda la información del usuario al hacer login
     */
    fun saveUserSession(context: Context, usuario: LoginResponseDTO) {
        getPrefs(context).edit().apply {
            putLong(KEY_USER_ID, usuario.id)
            putString(KEY_USER_NAME, usuario.nombreCompleto)
            putString(KEY_USER_EMAIL, usuario.email)
            putString(KEY_USER_ROL, usuario.rol)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Obtiene el ID del usuario logueado
     */
    fun getUserId(context: Context): Long {
        return getPrefs(context).getLong(KEY_USER_ID, -1L)
    }

    /**
     * Obtiene el nombre del usuario logueado
     */
    fun getUserName(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_NAME, null)
    }

    /**
     * Obtiene el email del usuario logueado
     */
    fun getUserEmail(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_EMAIL, null)
    }

    /**
     * Obtiene el rol del usuario
     */
    fun getUserRol(context: Context): String? {
        return getPrefs(context).getString(KEY_USER_ROL, null)
    }

    /**
     * Verifica si hay un usuario logueado
     */
    fun isLoggedIn(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    }

    /**
     * Cierra la sesión del usuario
     */
    fun logout(context: Context) {
        getPrefs(context).edit().clear().apply()
    }

    /**
     * Obtiene toda la información del usuario como un objeto
     */
    fun getUserInfo(context: Context): UserInfo? {
        if (!isLoggedIn(context)) return null

        return UserInfo(
            id = getUserId(context),
            nombreCompleto = getUserName(context) ?: "",
            email = getUserEmail(context) ?: "",
            rol = getUserRol(context) ?: ""
        )
    }
}

/**
 * Data class para representar la información del usuario
 */
data class UserInfo(
    val id: Long,
    val nombreCompleto: String,
    val email: String,
    val rol: String
)