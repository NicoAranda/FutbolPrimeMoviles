package com.example.futbolprime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.futbolprime.data.SessionManager

class UserViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _token = MutableLiveData<String?>(sessionManager.getToken())
    val token: LiveData<String?> = _token

    private val _nombre = MutableLiveData<String?>(sessionManager.getNombre())
    val nombre: LiveData<String?> = _nombre

    // ✅ Método para verificar si hay sesión activa
    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun login(token: String, nombre: String) {
        sessionManager.saveSession(token, nombre)
        _token.value = token
        _nombre.value = nombre
    }

    fun logout() {
        sessionManager.clearSession()
        _token.value = null
        _nombre.value = null
    }

    // ✅ Método para refrescar el estado desde SessionManager
    fun refreshSession() {
        _token.value = sessionManager.getToken()
        _nombre.value = sessionManager.getNombre()
    }
}