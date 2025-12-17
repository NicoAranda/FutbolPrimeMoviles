package com.example.futbolprime.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.CarritoItem
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.RetrofitClient
import com.example.futbolprime.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val api = RetrofitClient.apiService
    private val repo = CarritoRepository()

    private val _carrito = MutableStateFlow<List<CarritoItem>>(emptyList())
    val carrito: StateFlow<List<CarritoItem>> = _carrito

    private val _carritoId = MutableStateFlow<Long?>(null)
    val carritoId: StateFlow<Long?> = _carritoId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // ==================== CARGAR ====================
    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = api.obtenerCarritoUsuario(usuarioId)
                if (response.isSuccessful && response.body() != null) {

                    val dto = response.body()!!
                    _carritoId.value = dto.id

                    _carrito.value = dto.items.map { itemDto ->
                        CarritoItem(
                            itemId = itemDto.id,
                            producto = Producto(
                                id = itemDto.producto.id,
                                sku = itemDto.producto.sku ?: "",
                                nombre = itemDto.producto.nombre ?: "",
                                precio = itemDto.producto.precio ?: 0,
                                talla = itemDto.producto.talla?.toIntOrNull() ?: 0,
                                color = itemDto.producto.color ?: "N/A",
                                stock = itemDto.producto.stock ?: 0,
                                marca = itemDto.producto.marcaNombre ?: "N/A",
                                imagen = itemDto.producto.imagen
                            ),
                            cantidad = itemDto.cantidad,
                            precioUnitSnap = itemDto.precioUnitSnap
                        )
                    }
                } else {
                    _carrito.value = emptyList()
                    _carritoId.value = null
                }
            } catch (e: Exception) {
                Log.e("CarritoVM", "Error cargarCarrito", e)
                _carrito.value = emptyList()
                _carritoId.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ==================== ACTUALIZAR ====================
    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int, usuarioId: Long) {
        viewModelScope.launch {
            val ok = repo.actualizarCantidad(itemId, nuevaCantidad)
            if (ok) cargarCarrito(usuarioId)
        }
    }

    // ==================== ELIMINAR ====================
    fun eliminarProducto(usuarioId: Long, productoId: Long) {
        viewModelScope.launch {
            val id = _carritoId.value ?: return@launch
            val ok = repo.eliminarDelCarrito(id, productoId)
            if (ok) cargarCarrito(usuarioId)
        }
    }
}
