package com.example.futbolprime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf

class LoginViewModel : ViewModel() {

    var username = mutableStateOf("")
    var password = mutableStateOf("")

    var usernameError = mutableStateOf<String?>(null)
    var passwordError = mutableStateOf<String?>(null)

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
}
