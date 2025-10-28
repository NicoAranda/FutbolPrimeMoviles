package com.example.futbolprime.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * DatabaseHelper gestiona la creación, conexión y actualización
 * de la base de datos local SQLite de Fútbol Prime.
 *
 * Incluye las tablas: productos, usuarios y carrito.
 */
class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "futbolprime.db"
        private const val DATABASE_VERSION = 2 // 👈 Aumentado para forzar recreación

        // Tabla de Productos
        const val TABLE_PRODUCTOS = "productos"
        const val COLUMN_PROD_ID = "id"
        const val COLUMN_PROD_SKU = "sku"
        const val COLUMN_PROD_NOMBRE = "nombre"
        const val COLUMN_PROD_PRECIO = "precio"
        const val COLUMN_PROD_TALLA = "talla"
        const val COLUMN_PROD_COLOR = "color"
        const val COLUMN_PROD_STOCK = "stock"
        const val COLUMN_PROD_MARCA = "marca"
        const val COLUMN_PROD_DESCRIPCION = "descripcion"
        // ❌ Eliminamos la columna "imagen" porque ahora las usamos locales (R.drawable)

        // Tabla de Usuarios
        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_USU_ID = "id"
        const val COLUMN_USU_NOMBRE_USUARIO = "nombreUsuario"
        const val COLUMN_USU_CONTRASENA = "contrasena"

        // Tabla de Carrito
        const val TABLE_CARRITO = "carrito"
        const val COLUMN_CARRITO_ID = "id"
        const val COLUMN_CARRITO_PRODUCTO_ID = "producto_id"
        const val COLUMN_CARRITO_CANTIDAD = "cantidad"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Tabla de productos
        val createProductosTable = """
            CREATE TABLE $TABLE_PRODUCTOS (
                $COLUMN_PROD_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_PROD_SKU TEXT,
                $COLUMN_PROD_NOMBRE TEXT,
                $COLUMN_PROD_PRECIO INTEGER,
                $COLUMN_PROD_TALLA INTEGER,
                $COLUMN_PROD_COLOR TEXT,
                $COLUMN_PROD_STOCK INTEGER,
                $COLUMN_PROD_MARCA TEXT,
                $COLUMN_PROD_DESCRIPCION TEXT
            )
        """.trimIndent()
        db.execSQL(createProductosTable)

        // Tabla de usuarios
        val createUsuariosTable = """
            CREATE TABLE $TABLE_USUARIOS (
                $COLUMN_USU_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_USU_NOMBRE_USUARIO TEXT,
                $COLUMN_USU_CONTRASENA TEXT
            )
        """.trimIndent()
        db.execSQL(createUsuariosTable)

        // Tabla de carrito
        val createCarritoTable = """
            CREATE TABLE $TABLE_CARRITO (
                $COLUMN_CARRITO_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CARRITO_PRODUCTO_ID INTEGER,
                $COLUMN_CARRITO_CANTIDAD INTEGER,
                FOREIGN KEY($COLUMN_CARRITO_PRODUCTO_ID) REFERENCES $TABLE_PRODUCTOS($COLUMN_PROD_ID)
            )
        """.trimIndent()
        db.execSQL(createCarritoTable)

        // Insertar datos por defecto
        addInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CARRITO")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_PRODUCTOS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        onCreate(db)
    }

    /**
     * Inserta datos iniciales para los productos y usuarios.
     */
    private fun addInitialData(db: SQLiteDatabase) {
        // Productos por defecto (sin imagen, ya que se asignan en código)
        val productos = listOf(
            ContentValues().apply {
                put(COLUMN_PROD_SKU, "SKU001")
                put(COLUMN_PROD_NOMBRE, "Balón Adidas Pro")
                put(COLUMN_PROD_PRECIO, 45990)
                put(COLUMN_PROD_TALLA, 5)
                put(COLUMN_PROD_COLOR, "Blanco")
                put(COLUMN_PROD_STOCK, 10)
                put(COLUMN_PROD_MARCA, "Adidas")
                put(COLUMN_PROD_DESCRIPCION, "Balón profesional con alta durabilidad y control de vuelo.")
            },
            ContentValues().apply {
                put(COLUMN_PROD_SKU, "SKU002")
                put(COLUMN_PROD_NOMBRE, "Camiseta AC Milan")
                put(COLUMN_PROD_PRECIO, 77990)
                put(COLUMN_PROD_TALLA, 10)
                put(COLUMN_PROD_COLOR, "Roja y Negra")
                put(COLUMN_PROD_STOCK, 15)
                put(COLUMN_PROD_MARCA, "Puma")
                put(COLUMN_PROD_DESCRIPCION, "Camiseta oficial del AC Milan con tejido transpirable.")
            },
            ContentValues().apply {
                put(COLUMN_PROD_SKU, "SKU003")
                put(COLUMN_PROD_NOMBRE, "Zapatillas Hombre Fútbol")
                put(COLUMN_PROD_PRECIO, 89990)
                put(COLUMN_PROD_TALLA, 42)
                put(COLUMN_PROD_COLOR, "Rosa")
                put(COLUMN_PROD_STOCK, 8)
                put(COLUMN_PROD_MARCA, "Nike")
                put(COLUMN_PROD_DESCRIPCION, "Zapatillas con tracción avanzada y amortiguación ligera.")
            }
        )

        productos.forEach { db.insert(TABLE_PRODUCTOS, null, it) }

        // Usuarios por defecto
        val usuarios = listOf(
            ContentValues().apply {
                put(COLUMN_USU_NOMBRE_USUARIO, "admin")
                put(COLUMN_USU_CONTRASENA, "1234")
            },
            ContentValues().apply {
                put(COLUMN_USU_NOMBRE_USUARIO, "cliente")
                put(COLUMN_USU_CONTRASENA, "abcd")
            }
        )

        usuarios.forEach { db.insert(TABLE_USUARIOS, null, it) }
    }
}
