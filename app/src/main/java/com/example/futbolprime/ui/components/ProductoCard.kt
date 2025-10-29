package com.example.futbolprime.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.CarritoRepository
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProductoCard(producto: Producto, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // 🔹 Controla la animación de rebote
    val scale = remember { Animatable(1f) }

    // 🔹 Estado del botón (si fue agregado)
    var agregado by remember { mutableStateOf(false) }

    // 🎬 Animación de aparición del card
    AnimatedVisibility(
        visible = true,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 })
    ) {
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

                // 🪄 Botón con animación y feedback
                Button(
                    onClick = {
                        scope.launch {
                            // Rebote visual
                            scale.animateTo(1.2f, tween(100))
                            scale.animateTo(
                                1f,
                                spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )

                            // Cambio de ícono y mensaje
                            agregado = true
                            val carritoRepo = CarritoRepository(context)
                            carritoRepo.agregarAlCarrito(producto)
                            Toast.makeText(context, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()

                            // Volver al estado original tras 1,5 s
                            delay(1500)
                            agregado = false
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .scale(scale.value)
                ) {
                    // 👇 Ícono cambia dinámicamente
                    if (agregado) {
                        Icon(Icons.Default.Check, contentDescription = "Agregado", tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregado")
                    } else {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = "Agregar", tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Agregar al carrito")
                    }
                }
            }
        }
    }
}
