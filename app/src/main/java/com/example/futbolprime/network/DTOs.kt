package com.example.futbolprime.network

import com.google.gson.annotations.SerializedName

//  PRODUCTOS
data class ProductoDTO(
    val sku: String,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val tipo: String?,
    val talla: String?,
    val color: String?,
    val imagenUrl: String?,
    val activo: Boolean,
    val marcaId: Long?,
    val categoriaId: Long?
)

data class CrearProductoDTO(
    val sku: String,
    val nombre: String,
    val descripcion: String?,
    val precio: Double,
    val stock: Int,
    val tipo: String?,
    val talla: String?,
    val color: String?,
    val imagenUrl: String?,
    val marcaId: Long?,
    val categoriaId: Long?
)

data class ActualizarProductoDTO(
    val nombre: String?,
    val descripcion: String?,
    val precio: Double?,
    val stock: Int?,
    val tipo: String?,
    val talla: String?,
    val color: String?,
    val imagenUrl: String?,
    val activo: Boolean?,
    val marcaId: Long?,
    val categoriaId: Long?
)

// ==================== USUARIOS ====================
data class UsuarioDTO(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String
)

data class CrearUsuarioDTO(
    val nombre: String,
    val email: String,
    val password: String,
    val rol: String
)

data class ActualizarUsuarioDTO(
    val nombre: String?,
    val email: String?,
    val password: String?,
    val rol: String?,
    val habilitado: Boolean?
)

data class LoginRequestDTO(
    val email: String,
    val password: String
)

data class LoginResponseDTO(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String
)

// ==================== CARRITO ====================
data class CarritoDTO(
    val id: Long,
    val usuarioId: Long,
    val items: List<CarritoItemDTO>,
    val subtotal: Double,
    val impuestos: Double,
    val total: Double,
    val activo: Boolean
)

data class CarritoItemDTO(
    val id: Long,
    val carritoId: Long,
    val productoSku: String,
    val productoNombre: String,
    val cantidad: Int,
    val precioUnitario: Double,
    val subtotal: Double
)

data class CrearCarritoItemDTO(
    val usuarioId: Long,
    val productoSku: String,
    val cantidad: Int
)

data class ActualizarCarritoItemDTO(
    val cantidad: Int
)

// ==================== CATEGORÍAS ====================
data class CategoriaDTO(
    val id: Long,
    val nombre: String,
    val slug: String,
    val descripcion: String?,
    val imagenUrl: String?,
    val activa: Boolean,
    val padreId: Long?
)

data class CrearCategoriaDTO(
    val nombre: String,
    val descripcion: String?,
    val imagenUrl: String?,
    val padreId: Long?
)

data class ActualizarCategoriaDTO(
    val nombre: String?,
    val descripcion: String?,
    val imagenUrl: String?,
    val activa: Boolean?,
    val padreId: Long?
)

// ==================== MARCAS ====================
data class MarcaDTO(
    val id: Long,
    val nombre: String,
    val slug: String,
    val descripcion: String?,
    val logoUrl: String?,
    val activa: Boolean
)

data class CrearMarcaDTO(
    val nombre: String,
    val descripcion: String?,
    val logoUrl: String?
)

data class ActualizarMarcaDTO(
    val nombre: String?,
    val descripcion: String?,
    val logoUrl: String?,
    val activa: Boolean?
)

// ==================== PEDIDOS ====================
data class PedidoDTO(
    val id: Long,
    val usuarioId: Long,
    val carritoId: Long,
    val estado: String,
    val subtotal: Double,
    val impuestos: Double,
    val total: Double,
    val direccionEnvio: String?,
    val metodoPago: String?,
    val fechaCreacion: String,
    val fechaActualizacion: String
)

data class CrearPedidoDTO(
    val usuarioId: Long,
    val carritoId: Long,
    val direccionEnvio: String,
    val metodoPago: String
)

data class ActualizarEstadoPedidoDTO(
    val estado: String
)

// ==================== PAGOS ====================
data class PagoDTO(
    val id: Long,
    val pedidoId: Long,
    val monto: Double,
    val metodoPago: String,
    val estadoPago: String,
    val fechaPago: String
)

data class RegistrarPagoDTO(
    val monto: Double,
    val metodoPago: String
)

// ==================== LISTA DE DESEOS ====================
data class ListaDeseosDTO(
    val id: Long,
    val usuarioId: Long,
    val items: List<ListaDeseosItemDTO>
)

data class ListaDeseosItemDTO(
    val id: Long,
    val listaDeseosId: Long,
    val productoSku: String,
    val productoNombre: String,
    val fechaAgregado: String
)

data class CrearListaDeseosItemDTO(
    val usuarioId: Long,
    val productoSku: String
)