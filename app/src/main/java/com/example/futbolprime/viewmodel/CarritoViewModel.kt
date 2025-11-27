package com.example.futbolprime.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.CrearPedidoDTO
import com.example.futbolprime.network.RetrofitClient
import com.example.futbolprime.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val apiService = RetrofitClient.apiService
    private val carritoRepo = CarritoRepository()

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

    // Estado del carrito
    private val _carrito = MutableStateFlow<List<Pair<Producto, Int>>>(emptyList())
    val carrito: StateFlow<List<Pair<Producto, Int>>> = _carrito

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _carritoId = MutableStateFlow<Long?>(null)

    /**
     * Carga el carrito desde la API
     */
    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = carritoRepo.obtenerCarrito(usuarioId)
                _carrito.value = items

                // Obtener el ID del carrito para operaciones futuras
                val response = apiService.obtenerCarritoUsuario(usuarioId)
                if (response.isSuccessful) {
                    _carritoId.value = response.body()?.id
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Actualiza la cantidad de un producto
     */
    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int, usuarioId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val exito = carritoRepo.actualizarCantidad(itemId, nuevaCantidad)
            if (exito) {
                cargarCarrito(usuarioId)
                onSuccess()
            }
        }
    }

    /**
     * Elimina un producto del carrito
     */
    fun eliminarProducto(carritoId: Long, productoId: Long, usuarioId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            val exito = carritoRepo.eliminarDelCarrito(carritoId, productoId)
            if (exito) {
                cargarCarrito(usuarioId)
                onSuccess()
            }
        }
    }

    /**
     * Finaliza la compra creando un pedido
     */
    fun finalizarCompra(usuarioId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (!validarCampos()) {
            onError("Por favor completa todos los campos correctamente")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val carritoIdActual = _carritoId.value
                if (carritoIdActual == null) {
                    onError("No se pudo obtener el carrito")
                    return@launch
                }

                // Crear el pedido
                val pedidoRequest = CrearPedidoDTO(
                    usuarioId = usuarioId,
                    carritoId = carritoIdActual,
                    direccionEnvio = direccion.value,
                    metodoPago = "Tarjeta de crédito"
                )

                val response = apiService.crearPedido(pedidoRequest)
                if (response.isSuccessful) {
                    // Vaciar el carrito
                    carritoRepo.vaciarCarrito(carritoIdActual)
                    _carrito.value = emptyList()
                    limpiarCampos()
                    onSuccess()
                } else {
                    onError("Error al procesar el pedido: ${response.message()}")
                }
            } catch (e: Exception) {
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

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