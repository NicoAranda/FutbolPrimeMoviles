package com.example.futbolprime.model

data class Producto(
    val id: Long,
    val sku: String,
    val nombre: String,
    val precio: Int,
    val talla: Int = 0,
    val color: String = "N/A",
    val stock: Int = 0,
    val marca: String = "N/A",
    val descripcion: String = "",
    val imagen: String? = null
)
