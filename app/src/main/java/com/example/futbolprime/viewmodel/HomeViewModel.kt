package com.example.futbolprime.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * HomeViewModel ahora carga productos desde la API REST
 */
class HomeViewModel : ViewModel() {

    private val repository = ProductoRepository()

    // Estado observable para la lista de productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    // Estado de carga
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Estado de error
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        cargarProductos()
    }

    /**
     * Carga los productos desde la API
     */
    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val productosObtenidos = repository.obtenerProductos()
                _productos.value = productosObtenidos

                if (productosObtenidos.isEmpty()) {
                    _error.value = "No se encontraron productos"
                }
            } catch (e: Exception) {
                _error.value = "Error al cargar productos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Permite refrescar los productos manualmente
     */
    fun actualizarLista() {
        cargarProductos()
    }

    /**
     * Busca productos por tipo
     */
}