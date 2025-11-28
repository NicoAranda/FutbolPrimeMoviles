package com.example.futbolprime.model

import com.example.futbolprime.R

data class Producto(
    val id: Int,
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
