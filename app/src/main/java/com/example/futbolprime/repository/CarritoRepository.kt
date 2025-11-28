package com.example.futbolprime.repository

import android.util.Log
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.*
import com.example.futbolprime.R
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
    suspend fun obtenerCarrito(usuarioId: Long): List<Pair<Producto, Int>> {
        return try {
            val resp = apiService.obtenerCarritoUsuario(usuarioId)
            if (!resp.isSuccessful) {
                Log.e("CarritoRepo", "obtenerCarrito error: ${resp.code()} ${resp.message()}")
                return emptyList()
            }

            val carritoDto: CarritoDTO = resp.body() ?: return emptyList()

            val resultado = mutableListOf<Pair<Producto, Int>>()

            for (item in carritoDto.items) {
                val prodDto = item.producto ?: continue

                // Si imagen nula/blank -> intentar obtener detalle por SKU (si hay)
                val finalProductoDto = if (prodDto.imagen.isNullOrBlank()) {
                    val sku = prodDto.sku
                    if (!sku.isNullOrBlank()) {
                        try {
                            val detalleResp = apiService.obtenerProductoPorSku(sku)
                            if (detalleResp.isSuccessful) detalleResp.body() ?: prodDto
                            else prodDto
                        } catch (e: Exception) {
                            Log.w("CarritoRepo", "Error obtener por SKU='$sku': ${e.message}")
                            prodDto
                        }
                    } else {
                        prodDto
                    }
                } else {
                    prodDto
                }

                // Convertir finalProductoDto (ProductoDTO) a Producto local explícitamente
                val producto = Producto(
                    id = (finalProductoDto.id ?: 0L).toInt(),
                    sku = finalProductoDto.sku ?: "",
                    nombre = finalProductoDto.nombre ?: "",
                    precio = finalProductoDto.precio ?: 0,
                    talla = finalProductoDto.talla?.toIntOrNull() ?: 0,
                    color = finalProductoDto.color ?: "N/A",
                    stock = finalProductoDto.stock ?: 0,
                    marca = finalProductoDto.marcaNombre ?: "N/A",
                    imagen = finalProductoDto.imagen // puede seguir siendo null
                )

                val cantidad = item.cantidad ?: 1
                resultado.add(Pair(producto, cantidad))
            }

            resultado.toList()
        } catch (e: Exception) {
            Log.e("CarritoRepo", "Exception obtenerCarrito: ${e.message}", e)
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
     * Crea un objeto Producto desde un CarritoItemDTO (mapeo correcto)
     */
    private fun crearProductoDesdeItem(item: CarritoItemDTO): Producto {
        val p = item.producto
        return Producto(
            id = p.id.toInt(),
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
