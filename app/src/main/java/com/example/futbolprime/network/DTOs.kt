package com.example.futbolprime.network

import com.google.gson.annotations.SerializedName

//  PRODUCTOS (coinciden con backend)
data class ProductoDTO(
    val id: Long,
    val sku: String?,
    val nombre: String?,
    val precio: Int?,
    val oferta: Int?,
    val stock: Int?,
    val tipo: String?,
    val talla: String?,
    val color: String?,
    val imagen: String?, // <- única imagen (String?) según backend
    val marcaId: Long?,
    val marcaNombre: String?
)

data class CrearProductoDTO(
    val sku: String,
    val nombre: String,
    val precio: Int,
    val oferta: Int?,
    val tipo: String?,
    val talla: String?,
    val color: String?,
    val stock: Int,
    val marcaId: Long?,
    val descripcion: String?,
    val imagen: String?
)

data class ActualizarProductoDTO(
    val nombre: String?,
    val descripcion: String?,
    val precio: Int?,
    val oferta: Int?,
    val stock: Int?,
    val tipo: String?,
    val color: String?,
    val talla: String?,
    val marcaId: Long?
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
    val estado: String,
    val items: List<CarritoItemDTO> = emptyList()
)

data class CarritoItemDTO(
    val id: Long,
    val producto: ProductoDTO,
    val cantidad: Int,
    val precioUnitSnap: Int
)

data class CrearCarritoItemDTO(
    val usuarioId: Long,
    val productoId: Long,
    val cantidad: Int
)

data class ActualizarCarritoItemDTO(
    val cantidad: Int
)

// ==================== CATEGORÍAS ====================
data class CategoriaDTO(
    val id: Long,
    val nombre: String,
    val slug: String?,
    val descripcion: String?,
    val imagen: String?,   // mantengo imagen como String? si lo tuvieras; si no, lo puedes ignorar
    val activa: Boolean?,
    val padreId: Long?
)

data class CrearCategoriaDTO(
    val nombre: String,
    val slug: String?,
    val descripcion: String?,
    val imagen: String?,
    val padreId: Long?
)

data class ActualizarCategoriaDTO(
    val nombre: String?,
    val slug: String?,
    val descripcion: String?,
    val imagen: String?,
    val activa: Boolean?,
    val padreId: Long?
)

// ==================== MARCAS ====================
data class MarcaDTO(
    val id: Long,
    val nombre: String,
    val slug: String?,
    val descripcion: String?,
    val logoUrl: String?,
    val activa: Boolean?
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
// Ajustado a los DTOs de tu backend (PedidoDTO, PedidoItemDTO, CrearPedidoDTO, CrearPedidoItemDTO, ActualizarEstadoPedidoDTO)
data class PedidoItemDTO(
    val id: Long,
    val producto: ProductoDTO,
    val cantidad: Int,
    val precioUnitSnap: Int
)

data class PedidoDTO(
    val id: Long,
    val usuarioId: Long,
    val estado: String,
    val subtotal: Int?,
    val envio: Int?,
    val descuento: Int?,
    val total: Int?,

    val dirNombre: String?,
    val dirLinea1: String?,
    val dirLinea2: String?,
    val dirCiudad: String?,
    val dirRegion: String?,
    val dirZip: String?,
    val dirPais: String?,
    val dirTelefono: String?,

    val items: List<PedidoItemDTO> = emptyList()
)

data class CrearPedidoItemDTO(
    val productoId: Long,
    val cantidad: Int
)

data class CrearPedidoDTO(
    val usuarioId: Long,
    val items: List<CrearPedidoItemDTO>,
    val envio: Int?,
    val descuento: Int?,
    val dirNombre: String?,
    val dirLinea1: String?,
    val dirLinea2: String?,
    val dirCiudad: String?,
    val dirRegion: String?,
    val dirZip: String?,
    val dirPais: String?,
    val dirTelefono: String?
)

data class ActualizarEstadoPedidoDTO(
    val estado: String
)

// ==================== PAGOS ====================
data class PagoDTO(
    val id: Long,
    val pedidoId: Long,
    val monto: Int?,
    val metodoPago: String?,
    val estadoPago: String?,
    val fechaPago: String?
)

data class RegistrarPagoDTO(
    val monto: Int?,
    val metodoPago: String?
)

// ==================== LISTA DE DESEOS ====================
data class ListaDeseosDTO(
    val id: Long,
    val usuarioId: Long,
    val items: List<ListaDeseosItemDTO> = emptyList()
)

data class ListaDeseosItemDTO(
    val id: Long,
    val listaDeseosId: Long,
    val productoSku: String?,
    val productoNombre: String?,
    val fechaAgregado: String?
)

data class CrearListaDeseosItemDTO(
    val usuarioId: Long,
    val productoSku: String?
)
