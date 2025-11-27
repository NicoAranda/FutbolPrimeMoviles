package com.example.futbolprime.network

import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz que define todos los endpoints de la API de Fútbol Prime
 */
interface ApiService {

    // ==================== PRODUCTOS ====================
    @GET("api/productos")
    suspend fun obtenerTodosLosProductos(): Response<List<ProductoDTO>>

    @GET("api/productos/{sku}")
    suspend fun obtenerProductoPorSku(@Path("sku") sku: String): Response<ProductoDTO>

    @GET("api/productos/tipo/{tipo}")
    suspend fun obtenerProductosPorTipo(@Path("tipo") tipo: String): Response<List<ProductoDTO>>

    @POST("api/productos")
    suspend fun crearProducto(@Body request: CrearProductoDTO): Response<ProductoDTO>

    @PUT("api/productos/{sku}")
    suspend fun actualizarProducto(
        @Path("sku") sku: String,
        @Body request: ActualizarProductoDTO
    ): Response<ProductoDTO>

    @DELETE("api/productos/{sku}")
    suspend fun eliminarProducto(@Path("sku") sku: String): Response<Unit>

    // ==================== USUARIOS ====================
    @POST("api/usuarios")
    suspend fun registrarUsuario(@Body request: CrearUsuarioDTO): Response<UsuarioDTO>

    @POST("api/usuarios/login")
    suspend fun login(@Body request: LoginRequestDTO): Response<LoginResponseDTO>

    @GET("api/usuarios")
    suspend fun listarUsuarios(): Response<List<UsuarioDTO>>

    @GET("api/usuarios/{id}")
    suspend fun obtenerUsuarioPorId(@Path("id") id: Long): Response<UsuarioDTO>

    @PUT("api/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: Long,
        @Body request: ActualizarUsuarioDTO
    ): Response<UsuarioDTO>

    @DELETE("api/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: Long): Response<Unit>

    // ==================== CARRITO ====================
    @GET("api/carritos/usuario/{usuarioId}")
    suspend fun obtenerCarritoUsuario(@Path("usuarioId") usuarioId: Long): Response<CarritoDTO>

    @POST("api/carritos/item")
    suspend fun agregarItemAlCarrito(@Body request: CrearCarritoItemDTO): Response<CarritoItemDTO>

    @PUT("api/carritos/item/{itemId}")
    suspend fun actualizarItemCarrito(
        @Path("itemId") itemId: Long,
        @Body request: ActualizarCarritoItemDTO
    ): Response<CarritoItemDTO>

    @DELETE("api/carritos/{carritoId}/producto/{productoId}")
    suspend fun eliminarProductoDelCarrito(
        @Path("carritoId") carritoId: Long,
        @Path("productoId") productoId: Long
    ): Response<Unit>

    @DELETE("api/carritos/{carritoId}/vaciar")
    suspend fun vaciarCarrito(@Path("carritoId") carritoId: Long): Response<Unit>

    @GET("api/carritos/{carritoId}/items")
    suspend fun listarItemsCarrito(@Path("carritoId") carritoId: Long): Response<List<CarritoItemDTO>>

    // ==================== CATEGORÍAS ====================
    @GET("api/categorias")
    suspend fun obtenerTodasLasCategorias(): Response<List<CategoriaDTO>>

    @GET("api/categorias/{id}")
    suspend fun obtenerCategoriaPorId(@Path("id") id: Long): Response<CategoriaDTO>

    @GET("api/categorias/slug/{slug}")
    suspend fun obtenerCategoriaPorSlug(@Path("slug") slug: String): Response<CategoriaDTO>

    // ==================== MARCAS ====================
    @GET("api/marcas")
    suspend fun obtenerTodasLasMarcas(): Response<List<MarcaDTO>>

    @GET("api/marcas/{id}")
    suspend fun obtenerMarcaPorId(@Path("id") id: Long): Response<MarcaDTO>

    @GET("api/marcas/slug/{slug}")
    suspend fun obtenerMarcaPorSlug(@Path("slug") slug: String): Response<MarcaDTO>

    // ==================== PEDIDOS ====================
    @POST("api/pedidos")
    suspend fun crearPedido(@Body request: CrearPedidoDTO): Response<PedidoDTO>

    @GET("api/pedidos/{id}")
    suspend fun obtenerPedido(@Path("id") id: Long): Response<PedidoDTO>

    @GET("api/pedidos/usuario/{usuarioId}")
    suspend fun obtenerPedidosUsuario(@Path("usuarioId") usuarioId: Long): Response<List<PedidoDTO>>

    @PATCH("api/pedidos/{id}/estado")
    suspend fun actualizarEstadoPedido(
        @Path("id") id: Long,
        @Body request: ActualizarEstadoPedidoDTO
    ): Response<PedidoDTO>

    @POST("api/pedidos/{id}/pagos")
    suspend fun registrarPago(
        @Path("id") id: Long,
        @Body request: RegistrarPagoDTO
    ): Response<PagoDTO>

    // ==================== LISTA DE DESEOS ====================
    @GET("api/lista-deseos/usuario/{usuarioId}")
    suspend fun obtenerListaDeseos(@Path("usuarioId") usuarioId: Long): Response<ListaDeseosDTO>

    @POST("api/lista-deseos")
    suspend fun agregarProductoAListaDeseos(@Body request: CrearListaDeseosItemDTO): Response<ListaDeseosItemDTO>

    @DELETE("api/lista-deseos/usuario/{usuarioId}/producto/{productoId}")
    suspend fun eliminarProductoDeListaDeseos(
        @Path("usuarioId") usuarioId: Long,
        @Path("productoId") productoId: Long
    ): Response<Unit>

    @GET("api/lista-deseos/usuario/{usuarioId}/items")
    suspend fun listarItemsListaDeseos(@Path("usuarioId") usuarioId: Long): Response<List<ListaDeseosItemDTO>>
}