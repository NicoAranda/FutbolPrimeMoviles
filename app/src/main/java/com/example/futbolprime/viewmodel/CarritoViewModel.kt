package com.example.futbolprime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.CarritoItem
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.CarritoRepository
import com.example.futbolprime.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val carritoRepo = CarritoRepository()
    private val productoRepo = ProductoRepository()

    private val _carrito = MutableStateFlow<List<CarritoItem>>(emptyList())
    val carrito: StateFlow<List<CarritoItem>> = _carrito

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _carritoId = MutableStateFlow<Long?>(null)
    val carritoId: StateFlow<Long?> = _carritoId

    // Cache SKU -> imagen
    private val imagenesPorSku = mutableMapOf<String, String?>()

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            val items = carritoRepo.obtenerCarrito(usuarioId)

            // Cargar imágenes solo una vez
            if (imagenesPorSku.isEmpty()) {
                productoRepo.obtenerProductos().forEach { producto ->
                    imagenesPorSku[producto.sku] = producto.imagen
                }
            }

            val itemsConImagen = items.map { item ->
                val producto = item.producto

                if (producto.imagen.isNullOrBlank()) {
                    val imagen = imagenesPorSku[producto.sku]

                    item.copy(
                        producto = producto.copy(imagen = imagen)
                    )
                } else {
                    item
                }
            }

            _carrito.value = itemsConImagen
            _carritoId.value = itemsConImagen.firstOrNull()?.itemId
            _isLoading.value = false
        }
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int, usuarioId: Long) {
        viewModelScope.launch {
            if (carritoRepo.actualizarCantidad(itemId, nuevaCantidad)) {
                cargarCarrito(usuarioId)
            }
        }
    }

    fun eliminarProducto(usuarioId: Long, productoId: Long) {
        viewModelScope.launch {
            val id = _carritoId.value ?: return@launch
            if (carritoRepo.eliminarDelCarrito(id, productoId)) {
                cargarCarrito(usuarioId)
            }
        }
    }
}
