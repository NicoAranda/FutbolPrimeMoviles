package com.example.futbolprime.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.CarritoRepository
import com.example.futbolprime.utils.UserSessionManager
import kotlinx.coroutines.launch

@Composable
fun ProductoCard(producto: Producto, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val carritoRepo = remember { CarritoRepository() }

    var isLoading by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            // Imagen del producto
            Image(
                painter = painterResource(id = producto.imagen),
                contentDescription = producto.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Nombre del producto
            Text(
                text = producto.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Marca
            Text(
                text = "Marca: ${producto.marca}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Precio
            Text(
                text = "Precio: $${producto.precio}",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Detalles adicionales
            Text(
                text = "Talla: ${producto.talla}  |  Color: ${producto.color}",
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Stock disponible
            Text(
                text = "Stock disponible: ${producto.stock}",
                fontSize = 13.sp,
                color = if (producto.stock > 0)
                    MaterialTheme.colorScheme.secondary
                else
                    MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Botón para agregar al carrito
            Button(
                onClick = {
                    scope.launch {
                        isLoading = true

                        // Obtener el ID del usuario logueado
                        val usuarioId = UserSessionManager.getUserId(context)

                        if (usuarioId == -1L || !UserSessionManager.isLoggedIn(context)) {
                            Toast.makeText(
                                context,
                                "Debes iniciar sesión para agregar al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                            isLoading = false
                            return@launch
                        }

                        // Agregar al carrito usando la API
                        val exito = carritoRepo.agregarAlCarrito(
                            usuarioId = usuarioId,
                            productoSku = producto.sku,
                            cantidad = 1
                        )

                        isLoading = false

                        if (exito) {
                            vibrar(context, 120)
                            Toast.makeText(
                                context,
                                "Producto agregado al carrito",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "Error al agregar al carrito. Verifica tu conexión.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !isLoading && producto.stock > 0
            ) {
                Text(
                    text = if (isLoading) "Agregando..."
                    else if (producto.stock > 0) "Agregar al carrito"
                    else "Sin stock"
                )
            }
        }
    }
}

/**
 * Función auxiliar para generar vibración controlada
 */
private fun vibrar(context: Context, duracion: Long) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(
            VibrationEffect.createOneShot(duracion, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(duracion)
    }
}