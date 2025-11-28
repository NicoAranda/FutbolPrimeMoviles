package com.example.futbolprime.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.util.Log
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
    var direccion = mutableStateOf("") // se mapeará a dirLinea1
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

    // Guardamos el id real del carrito devuelto por backend (si existe)
    private val _carritoId = MutableStateFlow<Long?>(null)

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val items = carritoRepo.obtenerCarrito(usuarioId)
                _carrito.value = items

                val response = apiService.obtenerCarritoUsuario(usuarioId)
                if (response.isSuccessful) {
                    _carritoId.value = response.body()?.id
                } else {
                    Log.w("CarritoVM", "No se pudo obtener carritoId: ${response.code()} ${response.message()}")
                    _carritoId.value = null
                }
            } catch (e: Exception) {
                Log.e("CarritoVM", "Error cargarCarrito: ${e.message}", e)
                _carrito.value = emptyList()
                _carritoId.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int, usuarioId: Long, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val exito = carritoRepo.actualizarCantidad(itemId, nuevaCantidad)
                if (exito) {
                    cargarCarrito(usuarioId)
                    onSuccess()
                } else {
                    Log.w("CarritoVM", "actualizarCantidad: fallo en repo para itemId=$itemId")
                }
            } catch (e: Exception) {
                Log.e("CarritoVM", "Exception actualizarCantidad: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProducto(carritoId: Long?, productoId: Long, usuarioId: Long, onSuccess: () -> Unit = {}) {
        if (carritoId == null) {
            Log.w("CarritoVM", "eliminarProducto: carritoId es null, intentando recargar")
            cargarCarrito(usuarioId)
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val exito = carritoRepo.eliminarDelCarrito(carritoId, productoId)
                if (exito) {
                    cargarCarrito(usuarioId)
                    onSuccess()
                } else {
                    Log.w("CarritoVM", "eliminarProducto: fallo repo eliminar carritoId=$carritoId productoId=$productoId")
                }
            } catch (e: Exception) {
                Log.e("CarritoVM", "Exception eliminarProducto: ${e.message}", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Finaliza la compra. Usa el CrearPedidoDTO que pegaste:
     * data class CrearPedidoDTO(usuarioId, items, envio, descuento, dirNombre, dirLinea1, ...)
     */
    fun finalizarCompra(usuarioId: Long, onSuccess: () -> Unit = {}, onError: (String) -> Unit) {
        if (!validarCampos()) {
            onError("Por favor completa todos los campos correctamente")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val carritoIdActual = _carritoId.value
                if (carritoIdActual == null) {
                    onError("No se pudo obtener el carrito (carritoId null)")
                    _isLoading.value = false
                    return@launch
                }

                // Mapear items del carrito a CrearPedidoItemDTO (incluyendo precioUnitSnap)
                val itemsParaPedido = _carrito.value.map { (producto, cantidad) ->
                    CrearPedidoItemDTO(
                        productoId = producto.id.toLong(),
                        cantidad = cantidad
                    )
                }

                val pedidoRequest = CrearPedidoDTO(
                    usuarioId = usuarioId,
                    items = itemsParaPedido,
                    envio = 0,
                    descuento = 0,
                    dirNombre = nombre.value.ifBlank { null },
                    dirLinea1 = direccion.value.ifBlank { null },
                    dirLinea2 = null,
                    dirCiudad = null,
                    dirRegion = null,
                    dirZip = null,
                    dirPais = null,
                    dirTelefono = null
                )

                val response = apiService.crearPedido(pedidoRequest)
                if (response.isSuccessful) {
                    // Intentar vaciar carrito en backend y UI
                    try {
                        carritoRepo.vaciarCarrito(carritoIdActual)
                    } catch (e: Exception) {
                        Log.w("CarritoVM", "No se pudo vaciarCarrito luego de crear pedido: ${e.message}")
                    }

                    _carrito.value = emptyList()
                    _carritoId.value = null
                    limpiarCampos()
                    onSuccess()
                } else {
                    val err = response.errorBody()?.string()
                    onError("Error al procesar el pedido: ${response.code()} ${response.message()} ${err ?: ""}")
                }
            } catch (e: Exception) {
                Log.e("CarritoVM", "Exception finalizarCompra: ${e.message}", e)
                onError("Error de conexión: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

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
