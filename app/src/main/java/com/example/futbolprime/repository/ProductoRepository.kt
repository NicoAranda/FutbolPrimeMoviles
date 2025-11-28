package com.example.futbolprime.repository

import android.util.Log
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.ProductoDTO
import com.example.futbolprime.network.RetrofitClient
import com.example.futbolprime.R

class ProductoRepository {

    private val apiService = RetrofitClient.apiService
    private val BASE_URL = "http://10.0.2.2:8080" // emulador -> backend local

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

    private fun ProductoDTO.toProducto(): Producto {
        // Normalizar imagen a URL absoluta si viene relativa
        val rawImagen = this.imagen
        val imagenUrl = rawImagen?.let {
            if (it.startsWith("http")) it
            else "$BASE_URL${if (it.startsWith("/")) "" else "/"}$it"
        }

        // Log para depuración
        Log.d("ProductoRepo", "DTO -> id=${this.id} sku=${this.sku} imagenRaw='$rawImagen' imagenUrl='$imagenUrl'")

        return Producto(
            id = this.id.toInt(),            // <-- usa el ID real que viene del backend
            sku = this.sku ?: "",
            nombre = this.nombre ?: "",
            precio = this.precio ?: 0,
            talla = this.talla?.toIntOrNull() ?: 0,
            color = this.color ?: "N/A",
            stock = this.stock ?: 0,
            marca = this.marcaNombre ?: "N/A",
            descripcion = "",                // o this.descripcion ?: ""
            imagen = imagenUrl               // String? con URL absoluta o null
        )
    }
}
