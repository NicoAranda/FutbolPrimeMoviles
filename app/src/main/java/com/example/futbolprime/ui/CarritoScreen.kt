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
import com.example.futbolprime.ui.components.FormularioPago
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

    LaunchedEffect(usuarioId) {
        if (usuarioId != -1L) {
            viewModel.cargarCarrito(usuarioId)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Header(navController = navController, userViewModel)

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }

        if (carrito.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío")
            }
            return
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(carrito) { item ->
                val producto = item.producto

                Card {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        AsyncImage(
                            model = producto.imagen,
                            contentDescription = producto.nombre,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.Crop,
                            placeholder = painterResource(R.drawable.ic_launcher_foreground),
                            error = painterResource(R.drawable.ic_launcher_foreground)
                        )

                        Column(
                            modifier = Modifier.weight(1f).padding(start = 12.dp)
                        ) {
                            Text(producto.nombre, fontWeight = FontWeight.Bold)

                            Text("Subtotal: $${producto.precio * item.cantidad}")

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    enabled = item.cantidad > 1,
                                    onClick = {
                                        viewModel.actualizarCantidad(
                                            item.itemId,
                                            item.cantidad - 1,
                                            usuarioId
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Remove, null)
                                }

                                Text(item.cantidad.toString())

                                IconButton(
                                    onClick = {
                                        viewModel.actualizarCantidad(
                                            item.itemId,
                                            item.cantidad + 1,
                                            usuarioId
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.Add, null)
                                }
                            }
                        }

                        IconButton(
                            onClick = {
                                viewModel.eliminarProducto(usuarioId, producto.id)
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }

        val total = carrito.sumOf { it.producto.precio * it.cantidad }

        FormularioPago(
            context = context,
            total = total,
            onPagoConfirmado = {
                // Opcional: aquí puedes vaciar el carrito
                // viewModel.vaciarCarrito(usuarioId)
                navController.navigate(Screen.Inicio.route) {
                    popUpTo(Screen.Carrito.route) { inclusive = true }
                }
            }
        )


        Text(
            text = "Total: $$total",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        )
    }
}
