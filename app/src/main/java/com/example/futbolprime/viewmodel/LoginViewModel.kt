package com.example.futbolprime.viewmodel

import android.util.Log
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

        passwordError.value = if (password.value.length < 6) {
            valido = false
            "La contraseña debe tener al menos 6 caracteres"
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
                    password = password.value  // ✅ CORREGIDO: Ahora usa "password"
                )

                // 🔹 LOG: Request que se está enviando
                Log.d("LoginViewModel", "Intentando login con:")
                Log.d("LoginViewModel", "Email: ${request.email}")
                Log.d("LoginViewModel", "Contraseña length: ${request.password.length}")

                val response = apiService.login(request)

                // 🔹 LOG: Response completo
                Log.d("LoginViewModel", "Response code: ${response.code()}")
                Log.d("LoginViewModel", "Response message: ${response.message()}")
                Log.d("LoginViewModel", "Response isSuccessful: ${response.isSuccessful}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    _usuarioLogueado.value = loginResponse
                    _loginState.value = LoginState.Success(loginResponse)
                    Log.d("LoginViewModel", "Login exitoso: ${loginResponse.nombre}")
                } else {
                    // Intentar leer el error del backend
                    val errorBody = response.errorBody()?.string()
                    Log.e("LoginViewModel", "Error body: $errorBody")

                    val errorMsg = when (response.code()) {
                        401 -> "Email o contraseña incorrectos"
                        400 -> "Datos inválidos. Verifica el email y contraseña"
                        404 -> "Usuario no encontrado"
                        500 -> "Error en el servidor"
                        else -> "Error: ${response.code()} - ${response.message()}"
                    }
                    Log.e("LoginViewModel", "Login fallido: $errorMsg")
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Excepción en login: ${e.message}", e)
                val errorMsg = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "No se puede conectar al servidor. Verifica que la API esté corriendo."
                    e.message?.contains("Failed to connect") == true ->
                        "Error de conexión. Verifica la URL de la API."
                    else -> "Error de conexión: ${e.message}"
                }
                _loginState.value = LoginState.Error(errorMsg)
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