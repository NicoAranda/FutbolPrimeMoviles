package com.example.futbolprime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.data.SessionManager
import com.example.futbolprime.network.LoginRequestDTO
import com.example.futbolprime.network.LoginResponseDTO
import com.example.futbolprime.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(private val sessionManager: SessionManager) : ViewModel() {

    private val apiService = RetrofitClient.apiService

    var username = mutableStateOf("")
    var password = mutableStateOf("")

    var usernameError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    private val _usuarioLogueado = MutableStateFlow<LoginResponseDTO?>(null)
    val usuarioLogueado: StateFlow<LoginResponseDTO?> = _usuarioLogueado

    fun validarCampos(): Boolean {
        var valido = true

        usernameError.value = if (username.value.isBlank()) {
            valido = false
            "El usuario no puede estar vacío"
        } else null

        passwordError.value = if (password.value.length < 6) {
            valido = false
            "La contraseña debe tener al menos 6 caracteres"
        } else null

        return valido
    }

    fun login() {
        if (!validarCampos()) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val request = LoginRequestDTO(
                    email = username.value,
                    password = password.value
                )

                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!

                    // ✅ GUARDAR SESIÓN usando email como token
                    sessionManager.saveSession(
                        token = loginResponse.email, // Usar email como identificador
                        nombre = loginResponse.nombre
                    )

                    _usuarioLogueado.value = loginResponse
                    _loginState.value = LoginState.Success(loginResponse)

                    Log.d("LoginViewModel", "Login exitoso: ${loginResponse.nombre}")

                } else {
                    val errorMsg = when (response.code()) {
                        401 -> "Email o contraseña incorrectos"
                        400 -> "Datos inválidos"
                        404 -> "Usuario no encontrado"
                        else -> "Error: ${response.code()}"
                    }
                    _loginState.value = LoginState.Error(errorMsg)
                    Log.e("LoginViewModel", "Error en login: $errorMsg")
                }

            } catch (e: Exception) {
                val errorMsg = "Error de conexión: ${e.message}"
                _loginState.value = LoginState.Error(errorMsg)
                Log.e("LoginViewModel", "Excepción en login", e)
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: LoginResponseDTO) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}