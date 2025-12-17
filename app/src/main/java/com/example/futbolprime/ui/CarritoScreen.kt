package com.example.futbolprime.ui

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.futbolprime.MainActivity
import com.example.futbolprime.R
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.utils.NotificationUtils
import com.example.futbolprime.utils.UserSessionManager
import com.example.futbolprime.viewmodel.CarritoViewModel
import com.example.futbolprime.viewmodel.UserViewModel

@Composable
fun CarritoScreen(
    navController: NavHostController,
    userViewModel: UserViewModel
) {
    val viewModel = remember { CarritoViewModel() }
    val context = LocalContext.current

    val usuarioId = UserSessionManager.getUserId(context)

    val carrito by viewModel.carrito.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val carritoId by viewModel.carritoId.collectAsState()

    // ==================== CARGA INICIAL ====================
    LaunchedEffect(usuarioId) {
        if (usuarioId == -1L || !UserSessionManager.isLoggedIn(context)) {
            Toast.makeText(context, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Carrito.route) { inclusive = true }
            }
        } else {
            viewModel.cargarCarrito(usuarioId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Header(navController = navController, userViewModel)

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }

        if (carrito.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Tu carrito está vacío")
            }
            return
        }

        // ==================== LISTA ====================
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(carrito) { item ->
                val producto = item.producto

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = producto.imagen,
                            contentDescription = producto.nombre,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                            error = painterResource(id = R.drawable.ic_launcher_foreground)
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 12.dp)
                        ) {
                            Text(
                                text = producto.nombre,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Text(
                                text = "Subtotal: $${producto.precio * item.cantidad}",
                                fontSize = 14.sp
                            )

                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // DISMINUIR
                                IconButton(
                                    enabled = !isLoading && item.cantidad > 1,
                                    onClick = {
                                        viewModel.actualizarCantidad(
                                            itemId = item.itemId,
                                            nuevaCantidad = item.cantidad - 1,
                                            usuarioId = usuarioId
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                }

                                Text(
                                    text = item.cantidad.toString(),
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )

                                // AUMENTAR
                                IconButton(
                                    enabled = !isLoading,
                                    onClick = {
                                        viewModel.actualizarCantidad(
                                            itemId = item.itemId,
                                            nuevaCantidad = item.cantidad + 1,
                                            usuarioId = usuarioId
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                }
                            }
                        }

                        // ELIMINAR
                        IconButton(
                            enabled = !isLoading && carritoId != null,
                            onClick = {
                                viewModel.eliminarProducto(
                                    productoId = producto.id,
                                    usuarioId = usuarioId
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        // ==================== TOTAL ====================
        val total = carrito.sumOf { it.producto.precio * it.cantidad }

        Text(
            text = "Total: $$total",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .align(Alignment.End)
                .padding(16.dp)
        )
    }
}
