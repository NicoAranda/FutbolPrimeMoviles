package com.example.futbolprime.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.CrearPedidoDTO
import com.example.futbolprime.network.CrearPedidoItemDTO
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
    var direccion = mutableStateOf("") // aquí uso esta cadena como dirLinea1
    var tarjeta = mutableStateOf("")

    // Errores asociados
    var nombreError = mutableStateOf<String?>(null)
    var emailError = mutableStateOf<String?>(null)
    var direccionError = mutableStateOf<String?>(null)
    var tarjetaError = mutableStateOf<String?>(null)

    // Estado del carrito: lista de Pair<Producto, cantidad>
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
                } else {
                    _carritoId.value = null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _carritoId.value = null
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
     *
     * Construye CrearPedidoDTO a partir del carrito local.
     * Observación: mapea nombre -> dirNombre y direccion -> dirLinea1; otros campos se envían vacíos.
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
                    _isLoading.value = false
                    return@launch
                }

                // Mapear los items del carrito actual a CrearPedidoItemDTO
                val itemsParaPedido = _carrito.value.map { (producto, cantidad) ->
                    CrearPedidoItemDTO(
                        productoId = producto.id.toLong(),
                        cantidad = cantidad
                    )
                }

                // Construir el DTO que pide el backend
                val pedidoRequest = CrearPedidoDTO(
                    usuarioId = usuarioId,
                    items = itemsParaPedido,
                    envio = 0,           // ajustar si corresponde
                    descuento = 0,       // ajustar si corresponde
                    dirNombre = nombre.value.ifBlank { "" },
                    dirLinea1 = direccion.value.ifBlank { "" },
                    dirLinea2 = "",
                    dirCiudad = "",
                    dirRegion = "",
                    dirZip = "",
                    dirPais = "",
                    dirTelefono = ""
                )

                val response = apiService.crearPedido(pedidoRequest)
                if (response.isSuccessful) {
                    // Vaciar el carrito en backend y UI
                    carritoRepo.vaciarCarrito(carritoIdActual)
                    _carrito.value = emptyList()
                    _carritoId.value = null
                    limpiarCampos()
                    onSuccess()
                } else {
                    // Capturar cuerpo de error si existe
                    val err = response.errorBody()?.string()
                    onError("Error al procesar el pedido: ${response.message()}${if (err != null) " - $err" else ""}")
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