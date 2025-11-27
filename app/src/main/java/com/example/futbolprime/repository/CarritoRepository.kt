package com.example.futbolprime.repository

import android.util.Log
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.*
import com.example.futbolprime.R

/**
 * CarritoRepository ahora consume la API REST
 * Requiere un usuarioId para gestionar el carrito
 */
class CarritoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Agrega un producto al carrito del usuario
     */
    suspend fun agregarAlCarrito(usuarioId: Long, productoSku: String, cantidad: Int = 1): Boolean {
        return try {
            val request = CrearCarritoItemDTO(
                usuarioId = usuarioId,
                productoSku = productoSku,
                cantidad = cantidad
            )
            val response = apiService.agregarItemAlCarrito(request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Error agregando al carrito: ${e.message}", e)
            false
        }
    }

    /**
     * Obtiene el carrito completo del usuario
     */
    suspend fun obtenerCarrito(usuarioId: Long): List<Pair<Producto, Int>> {
        return try {
            val response = apiService.obtenerCarritoUsuario(usuarioId)
            if (response.isSuccessful) {
                val carritoDTO = response.body()
                carritoDTO?.items?.map { item ->
                    val producto = crearProductoDesdeItem(item)
                    Pair(producto, item.cantidad)
                } ?: emptyList()
            } else {
                Log.e("CarritoRepository", "Error: ${response.code()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Exception obteniendo carrito: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Actualiza la cantidad de un item en el carrito
     */
    suspend fun actualizarCantidad(itemId: Long, nuevaCantidad: Int): Boolean {
        return try {
            val request = ActualizarCarritoItemDTO(cantidad = nuevaCantidad)
            val response = apiService.actualizarItemCarrito(itemId, request)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Error actualizando cantidad: ${e.message}", e)
            false
        }
    }

    /**
     * Elimina un producto del carrito
     */
    suspend fun eliminarDelCarrito(carritoId: Long, productoId: Long): Boolean {
        return try {
            val response = apiService.eliminarProductoDelCarrito(carritoId, productoId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Error eliminando producto: ${e.message}", e)
            false
        }
    }

    /**
     * Vacía completamente el carrito
     */
    suspend fun vaciarCarrito(carritoId: Long): Boolean {
        return try {
            val response = apiService.vaciarCarrito(carritoId)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Error vaciando carrito: ${e.message}", e)
            false
        }
    }

    /**
     * Crea un objeto Producto desde un CarritoItemDTO
     */
    private fun crearProductoDesdeItem(item: CarritoItemDTO): Producto {
        return Producto(
            id = item.id.toInt(),
            sku = item.productoSku,
            nombre = item.productoNombre,
            precio = item.precioUnitario.toInt(),
            talla = 0, // No disponible en el DTO del carrito
            color = "N/A",
            stock = 0,
            marca = "N/A",
            descripcion = "",
            imagen = asignarImagenPorSku(item.productoSku)
        )
    }

    private fun asignarImagenPorSku(sku: String): Int {
        return when (sku) {
            "SKU001" -> R.drawable.balonadidas
            "SKU002" -> R.drawable.poleramilan
            "SKU003" -> R.drawable.zapatillasnike
            else -> R.drawable.ic_launcher_foreground
        }
    }
}