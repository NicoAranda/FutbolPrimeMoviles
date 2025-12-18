package com.example.futbolprime.repository

import android.util.Log
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.*
import com.example.futbolprime.R
import com.example.futbolprime.model.CarritoItem
import com.example.futbolprime.model.CarritoResult
import com.google.gson.Gson

class CarritoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Agrega un producto al carrito del usuario
     * Ahora recibe productoId (Long) que es lo que espera el backend.
     */
    suspend fun agregarAlCarrito(usuarioId: Long, productoId: Long, cantidad: Int = 1): Boolean {
        return try {
            val request = CrearCarritoItemDTO(
                usuarioId = usuarioId,
                productoId = productoId,
                cantidad = cantidad
            )

            Log.d("CarritoRepo", "POST /api/carritos/item body: ${Gson().toJson(request)}")

            val response = apiService.agregarItemAlCarrito(request)
            if (response.isSuccessful) {
                Log.d("CarritoRepository", "Agregar al carrito OK: ${Gson().toJson(response.body())}")
                true
            } else {
                val err = response.errorBody()?.string()
                Log.e("CarritoRepository", "Agregar al carrito FALLÓ: code=${response.code()} body=$err")
                false
            }
        } catch (e: Exception) {
            Log.e("CarritoRepository", "Error agregando al carrito: ${e.message}", e)
            false
        }
    }

    /**
     * Obtiene el carrito completo del usuario
     */
    suspend fun obtenerCarritoCompleto(usuarioId: Long): CarritoResult {
        return try {
            val resp = apiService.obtenerCarritoUsuario(usuarioId)
            val carritoDto = resp.body() ?: return CarritoResult(0L, emptyList())

            val items = carritoDto.items.map { item ->
                CarritoItem(
                    itemId = item.id,
                    producto = Producto(
                        id = item.producto.id,
                        sku = item.producto.sku ?: "",
                        nombre = item.producto.nombre ?: "",
                        precio = item.producto.precio ?: 0,
                        talla = item.producto.talla?.toIntOrNull() ?: 0,
                        color = item.producto.color ?: "N/A",
                        stock = item.producto.stock ?: 0,
                        marca = item.producto.marcaNombre ?: "N/A",
                        imagen = item.producto.imagen
                    ),
                    cantidad = item.cantidad,
                    precioUnitSnap = item.precioUnitSnap
                )
            }

            CarritoResult(
                carritoId = carritoDto.id,
                items = items
            )

        } catch (e: Exception) {
            Log.e("CarritoRepo", "Error obtenerCarritoCompleto", e)
            CarritoResult(0L, emptyList())
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
     * Crea un objeto Producto desde un CarritoItemDTO (mapeo correcto)
     */
    private fun crearProductoDesdeItem(item: CarritoItemDTO): Producto {
        val p = item.producto
        return Producto(
            id = p.id.toLong(),
            sku = p.sku ?: "",
            nombre = p.nombre ?: "",
            precio = p.precio ?: 0,
            talla = p.talla?.toIntOrNull() ?: 0,
            color = p.color ?: "N/A",
            stock = p.stock ?: 0,
            marca = p.marcaNombre ?: "N/A",
            descripcion = "",
            imagen = p.imagen // cadena con URL ya normalizada por ProductoRepository
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
