package com.example.futbolprime.model

data class Producto(
    val id: Int,
    val sku: String,
    val nombre: String,
    val precio: Int,
    val talla: Int,
    val color: String,
    val stock: Int,
    val marca: String,
    val descripcion: String,
    val imagen: Int
)
