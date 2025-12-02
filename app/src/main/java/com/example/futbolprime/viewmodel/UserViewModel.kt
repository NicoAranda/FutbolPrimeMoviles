package com.example.futbolprime.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.data.SessionManager
import kotlinx.coroutines.launch

class UserViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val _token = MutableLiveData<String?>(sessionManager.getToken())
    val token: LiveData<String?> = _token

    private val _nombre = MutableLiveData<String?>(sessionManager.getNombre())
    val nombre: LiveData<String?> = _nombre

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
}