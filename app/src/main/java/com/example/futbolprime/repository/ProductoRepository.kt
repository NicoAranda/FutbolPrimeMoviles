package com.example.futbolprime.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.futbolprime.R
import com.example.futbolprime.model.Producto

/**
 * `ProductoRepository` gestiona las operaciones CRUD sobre la base de datos de productos.
 * Actúa como intermediario entre los ViewModels y la base de datos local SQLite.
 */
class ProductoRepository(context: Context) {

    // Instancia del helper de base de datos
    private val dbHelper = DatabaseHelper(context)

    /**
     * Convierte una fila del cursor en un objeto `Producto`.
     * Asigna imágenes locales según el ID del producto.
     */
    private fun cursorToProducto(cursor: Cursor): Producto {
        val id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_ID))

        return Producto(
            id = id,
            sku = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_SKU)),
            nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_NOMBRE)),
            precio = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_PRECIO)),
            talla = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_TALLA)),
            color = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_COLOR)),
            stock = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_STOCK)),
            marca = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_MARCA)),
            descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_PROD_DESCRIPCION)),
            imagen = when (id) { // 👇 Imagen según el producto
                1 -> R.drawable.balonadidas
                2 -> R.drawable.poleramilan
                3 -> R.drawable.zapatillasnike
                else -> R.drawable.ic_launcher_foreground
            }
        )
    }

    /**
     * Obtiene todos los productos desde la base de datos.
     */
    fun obtenerProductos(): List<Producto> {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_PRODUCTOS, null, null, null, null, null, null)
        val productos = mutableListOf<Producto>()

        with(cursor) {
            while (moveToNext()) {
                productos.add(cursorToProducto(this))
            }
        }

        cursor.close()
        db.close()
        return productos
    }

    /**
     * Inserta un nuevo producto en la base de datos.
     * (sin imagen, ya que se asigna en código).
     */
    fun agregarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROD_SKU, producto.sku)
            put(DatabaseHelper.COLUMN_PROD_NOMBRE, producto.nombre)
            put(DatabaseHelper.COLUMN_PROD_PRECIO, producto.precio)
            put(DatabaseHelper.COLUMN_PROD_TALLA, producto.talla)
            put(DatabaseHelper.COLUMN_PROD_COLOR, producto.color)
            put(DatabaseHelper.COLUMN_PROD_STOCK, producto.stock)
            put(DatabaseHelper.COLUMN_PROD_MARCA, producto.marca)
            put(DatabaseHelper.COLUMN_PROD_DESCRIPCION, producto.descripcion)
        }
        db.insert(DatabaseHelper.TABLE_PRODUCTOS, null, values)
        db.close()
    }

    /**
     * Actualiza un producto existente.
     */
    fun actualizarProducto(producto: Producto) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_PROD_SKU, producto.sku)
            put(DatabaseHelper.COLUMN_PROD_NOMBRE, producto.nombre)
            put(DatabaseHelper.COLUMN_PROD_PRECIO, producto.precio)
            put(DatabaseHelper.COLUMN_PROD_TALLA, producto.talla)
            put(DatabaseHelper.COLUMN_PROD_COLOR, producto.color)
            put(DatabaseHelper.COLUMN_PROD_STOCK, producto.stock)
            put(DatabaseHelper.COLUMN_PROD_MARCA, producto.marca)
            put(DatabaseHelper.COLUMN_PROD_DESCRIPCION, producto.descripcion)
        }
        db.update(
            DatabaseHelper.TABLE_PRODUCTOS,
            values,
            "${DatabaseHelper.COLUMN_PROD_ID} = ?",
            arrayOf(producto.id.toString())
        )
        db.close()
    }

    /**
     * Elimina un producto por su ID.
     */
    fun eliminarProducto(productoId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(
            DatabaseHelper.TABLE_PRODUCTOS,
            "${DatabaseHelper.COLUMN_PROD_ID} = ?",
            arrayOf(productoId.toString())
        )
        db.close()
    }

    /**
     * Busca un producto específico por su ID.
     */
    fun obtenerProductoPorId(productoId: Int): Producto? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_PRODUCTOS,
            null,
            "${DatabaseHelper.COLUMN_PROD_ID} = ?",
            arrayOf(productoId.toString()),
            null, null, null, "1"
        )

        var producto: Producto? = null
        if (cursor.moveToFirst()) {
            producto = cursorToProducto(cursor)
        }

        cursor.close()
        db.close()
        return producto
    }
}
