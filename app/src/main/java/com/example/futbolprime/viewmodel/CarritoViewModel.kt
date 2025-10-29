package com.example.futbolprime.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class CarritoViewModel : ViewModel() {

    // Campos del formulario
    var nombre = mutableStateOf("")
    var email = mutableStateOf("")
    var direccion = mutableStateOf("")
    var tarjeta = mutableStateOf("")

    // Errores asociados
    var nombreError = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
    var direccionError = mutableStateOf<String?>(null)
    var tarjetaError = mutableStateOf<String?>(null)

    /**
     * Valida los campos del formulario.
     * Retorna true si todo es válido, false si hay errores.
     */
    fun validarCampos(): Boolean {
        var valido = true

        nombreError.value = if (nombre.value.isBlank()) {
            valido = false
            "El nombre no puede estar vacío"
        } else null

        emailError.value = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.value).matches()) {
            valido = false
            "Correo inválido"
        } else null

        direccionError.value = if (direccion.value.isBlank()) {
            valido = false
            "La dirección no puede estar vacía"
        } else null

        tarjetaError.value = if (tarjeta.value.length < 12) {
            valido = false
            "Número de tarjeta inválido (mínimo 12 dígitos)"
        } else null

        return valido
    }

    /** Limpia los campos y errores después de una compra exitosa. */
    fun limpiarCampos() {
        nombre.value = ""
        email.value = ""
        direccion.value = ""
        tarjeta.value = ""
        nombreError.value = null
        emailError.value = null
        direccionError.value = null
        tarjetaError.value = null
    }
}
