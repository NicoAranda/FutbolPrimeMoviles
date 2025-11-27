package com.example.futbolprime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.network.LoginRequestDTO
import com.example.futbolprime.network.LoginResponseDTO
import com.example.futbolprime.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService

    var username = mutableStateOf("")
    var password = mutableStateOf("")

    var usernameError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)

    // Estado para manejar el login
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // Usuario logueado
    private val _usuarioLogueado = MutableStateFlow<LoginResponseDTO?>(null)
    val usuarioLogueado: StateFlow<LoginResponseDTO?> = _usuarioLogueado

    fun validarCampos(): Boolean {
        var valido = true

        usernameError.value = if (username.value.isBlank()) {
            valido = false
            "El usuario no puede estar vacío"
        } else null

        passwordError.value = if (password.value.length < 4) {
            valido = false
            "La contraseña debe tener al menos 4 caracteres"
        } else null

        return valido
    }

    /**
     * Realiza el login usando la API
     */
    fun login() {
        if (!validarCampos()) return

        viewModelScope.launch {
            _loginState.value = LoginState.Loading

            try {
                val request = LoginRequestDTO(
                    email = username.value,
                    contrasena = password.value
                )

                val response = apiService.login(request)

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    _usuarioLogueado.value = loginResponse
                    _loginState.value = LoginState.Success(loginResponse)
                } else {
                    _loginState.value = LoginState.Error("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }
}

/**
 * Estados posibles del login
 */
sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val usuario: LoginResponseDTO) : LoginState()
    data class Error(val mensaje: String) : LoginState()
}