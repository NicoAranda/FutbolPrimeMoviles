package com.example.futbolprime.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.futbolprime.R
import com.example.futbolprime.model.Producto

class CarritoRepository(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    /**
     * Agrega un producto al carrito.
     * Si ya existe, incrementa la cantidad.
     */
    fun agregarAlCarrito(producto: Producto) {
        val db = dbHelper.writableDatabase

        // Verificar si el producto ya existe en el carrito
        val cursor = db.query(
            DatabaseHelper.TABLE_CARRITO,
            arrayOf(DatabaseHelper.COLUMN_CARRITO_CANTIDAD),
            "${DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID} = ?",
            arrayOf(producto.id.toString()),
            null, null, null
        )

        if (cursor.moveToFirst()) {
            // Ya existe → aumentar cantidad
            val cantidadActual =
                cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARRITO_CANTIDAD))
            val valores = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CARRITO_CANTIDAD, cantidadActual + 1)
            }
            db.update(
                DatabaseHelper.TABLE_CARRITO,
                valores,
                "${DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID} = ?",
                arrayOf(producto.id.toString())
            )
        } else {
            // No existe → agregar nuevo
            val valores = ContentValues().apply {
                put(DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID, producto.id)
                put(DatabaseHelper.COLUMN_CARRITO_CANTIDAD, 1)
            }
            db.insert(DatabaseHelper.TABLE_CARRITO, null, valores)
        }

        cursor.close()
        db.close()
    }

    /**
     * Obtiene todos los productos del carrito junto con sus cantidades.
     */
    fun obtenerCarrito(): List<Pair<Producto, Int>> {
        val db = dbHelper.readableDatabase
        val carrito = mutableListOf<Pair<Producto, Int>>()

        val cursor = db.rawQuery(
            """
            SELECT p.*, c.cantidad 
            FROM ${DatabaseHelper.TABLE_CARRITO} c
            JOIN ${DatabaseHelper.TABLE_PRODUCTOS} p
            ON c.${DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID} = p.${DatabaseHelper.COLUMN_PROD_ID}
            """.trimIndent(),
            null
        )

        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_ID))
                val producto = Producto(
                    id = id,
                    sku = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_SKU)),
                    nombre = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_NOMBRE)),
                    precio = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_PRECIO)),
                    talla = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_TALLA)),
                    color = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_COLOR)),
                    stock = getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_STOCK)),
                    marca = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_MARCA)),
                    descripcion = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_DESCRIPCION)),
                    imagen = when (id) { // 👈 Asigna imagen local según ID
                        1 -> R.drawable.balonadidas
                        2 -> R.drawable.poleramilan
                        3 -> R.drawable.zapatillasnike
                        else -> R.drawable.ic_launcher_foreground
                    }
                )
                val cantidad =
                    getInt(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CARRITO_CANTIDAD))
                carrito.add(Pair(producto, cantidad))
            }
        }

        cursor.close()
        db.close()
        return carrito
    }

    /**
     * Elimina un producto del carrito.
     */
    fun eliminarDelCarrito(productoId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_CARRITO,
            "${DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID} = ?",
            arrayOf(productoId.toString())
        )
        db.close()
    }

    /**
     * Vacía completamente el carrito.
     */
    fun vaciarCarrito() {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_CARRITO, null, null)
        db.close()
    }

    fun actualizarCantidad(productoId: Int, nuevaCantidad: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CARRITO_CANTIDAD, nuevaCantidad)
        }
        db.update(
            DatabaseHelper.TABLE_CARRITO,
            values,
            "${DatabaseHelper.COLUMN_CARRITO_PRODUCTO_ID} = ?",
            arrayOf(productoId.toString())
        )
        db.close()
    }

}
