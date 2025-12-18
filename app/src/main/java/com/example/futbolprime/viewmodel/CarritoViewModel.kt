package com.example.futbolprime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.CarritoItem
import com.example.futbolprime.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CarritoViewModel : ViewModel() {

    private val repo = CarritoRepository()

    private val _carrito = MutableStateFlow<List<CarritoItem>>(emptyList())
    val carrito: StateFlow<List<CarritoItem>> = _carrito

    private val _carritoId = MutableStateFlow<Long?>(null)
    val carritoId: StateFlow<Long?> = _carritoId

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun cargarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            _isLoading.value = true

            val result = repo.obtenerCarritoCompleto(usuarioId)
            _carrito.value = result.items
            _carritoId.value = result.carritoId

            _isLoading.value = false
        }
    }

    fun actualizarCantidad(itemId: Long, nuevaCantidad: Int, usuarioId: Long) {
        viewModelScope.launch {
            if (repo.actualizarCantidad(itemId, nuevaCantidad)) {
                cargarCarrito(usuarioId)
            }
        }
    }

    fun eliminarProducto(usuarioId: Long, productoId: Long) {
        viewModelScope.launch {
            val id = _carritoId.value ?: return@launch
            if (repo.eliminarDelCarrito(id, productoId)) {
                cargarCarrito(usuarioId)
            }
        }
    }

    fun vaciarCarrito(usuarioId: Long) {
        viewModelScope.launch {
            val id = _carritoId.value ?: return@launch
            if (repo.vaciarCarrito(id)) {
                _carrito.value = emptyList()
            }
        }
    }
}
