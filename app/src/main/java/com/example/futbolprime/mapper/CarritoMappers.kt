package com.example.futbolprime.mapper

import com.example.futbolprime.model.CarritoItem
import com.example.futbolprime.model.Producto
import com.example.futbolprime.network.CarritoItemDTO
import com.example.futbolprime.network.ProductoDTO

/* ================= PRODUCTO ================= */

fun ProductoDTO.toModel(): Producto {
    return Producto(
        id = id,
        sku = sku ?: "",
        nombre = nombre ?: "Producto",
        precio = precio ?: 0,
        imagen = imagen
    )
}

/* ================= CARRITO ITEM ================= */

fun CarritoItemDTO.toModel(): CarritoItem {
    return CarritoItem(
        itemId = id,
        producto = producto.toModel(),
        cantidad = cantidad,
        precioUnitSnap = precioUnitSnap
    )
}
