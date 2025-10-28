package com.example.futbolprime.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * `HomeViewModel` se encarga de manejar la lógica de negocio para la pantalla principal
 * de productos. Se comunica con el `ProductoRepository` para obtener los datos desde la BD.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProductoRepository(application)

    // Estado observable para la lista de productos
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    init {
        cargarProductos()
    }

    /**
     * Carga los productos desde la base de datos usando una corrutina.
     */
    private fun cargarProductos() {
        viewModelScope.launch {
            _productos.value = repository.obtenerProductos()
        }
    }

    /**
     * Permite refrescar los productos manualmente.
     */
    fun actualizarLista() {
        cargarProductos()
    }

    /**
     * Agrega un nuevo producto.
     */
    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.agregarProducto(producto)
            cargarProductos()
        }
    }

    /**
     * Elimina un producto por su ID.
     */
    fun eliminarProducto(productoId: Int) {
        viewModelScope.launch {
            repository.eliminarProducto(productoId)
            cargarProductos()
        }
    }
}
