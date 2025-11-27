package com.example.futbolprime.repository

import android.util.Log
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.ProductoDTO
import com.example.futbolprime.network.RetrofitClient
import com.example.futbolprime.R

/**
 * ProductoRepository ahora consume datos desde la API REST
 * en lugar de SQLite local
 */
class ProductoRepository {

    private val apiService = RetrofitClient.apiService

    /**
     * Obtiene todos los productos desde la API
     */
    suspend fun obtenerProductos(): List<Producto> {
        return try {
            val response = apiService.obtenerTodosLosProductos()
            if (response.isSuccessful) {
                response.body()?.map { it.toProducto() } ?: emptyList()
            } else {
                Log.e("ProductoRepository", "Error: ${response.code()} - ${response.message()}")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Obtiene un producto específico por SKU
     */
    suspend fun obtenerProductoPorSku(sku: String): Producto? {
        return try {
            val response = apiService.obtenerProductoPorSku(sku)
            if (response.isSuccessful) {
                response.body()?.toProducto()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Exception: ${e.message}", e)
            null
        }
    }

    /**
     * Obtiene productos filtrados por tipo
     */
    suspend fun obtenerProductosPorTipo(tipo: String): List<Producto> {
        return try {
            val response = apiService.obtenerProductosPorTipo(tipo)
            if (response.isSuccessful) {
                response.body()?.map { it.toProducto() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("ProductoRepository", "Exception: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Convierte ProductoDTO de la API a Producto del modelo local
     * Nota: Las imágenes ahora pueden venir de URL o asignar locales por defecto
     */
    private fun ProductoDTO.toProducto(): Producto {
        return Producto(
            id = sku.hashCode(), // Usamos hash del SKU como ID temporal
            sku = sku,
            nombre = nombre,
            precio = precio.toInt(),
            talla = talla?.toIntOrNull() ?: 0,
            color = color ?: "N/A",
            stock = stock,
            marca = "Marca genérica", // Podrías hacer otra consulta para obtener el nombre de la marca
            descripcion = descripcion ?: "",
            imagen = asignarImagenLocal(sku) // Función helper para asignar imágenes locales
        )
    }

    /**
     * Asigna una imagen local según el SKU
     * Puedes expandir esto o cargar imágenes desde URL usando Coil/Glide
     */
    private fun asignarImagenLocal(sku: String): Int {
        return when (sku) {
            "SKU001" -> R.drawable.balonadidas
            "SKU002" -> R.drawable.poleramilan
            "SKU003" -> R.drawable.zapatillasnike
            else -> R.drawable.ic_launcher_foreground
        }
    }
}