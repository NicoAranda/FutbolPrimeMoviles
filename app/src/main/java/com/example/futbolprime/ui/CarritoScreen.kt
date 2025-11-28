package com.example.futbolprime.ui

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import kotlinx.coroutines.launch

@Composable
fun CarritoScreen(
    navController: NavHostController,
    viewModel: CarritoViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Verificar si hay usuario logueado
    val usuarioId = UserSessionManager.getUserId(context)

    // Redirigir a login si no hay sesión
    LaunchedEffect(usuarioId) {
        if (usuarioId == -1L || !UserSessionManager.isLoggedIn(context)) {
            Toast.makeText(context, "Debes iniciar sesión primero", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.Login.route) {
                popUpTo(Screen.Carrito.route) { inclusive = true }
            }
        } else {
            viewModel.cargarCarrito(usuarioId)
        }
    }

    val carrito by viewModel.carrito.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Permiso de notificaciones (Android 13+)
    val requestNotifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            enviarNotificacion(context)
        } else {
            Toast.makeText(context, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show()
        }
    }

    fun puedeNotificar(): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(navController = navController)

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Carrito de Compras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                if (carrito.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Tu carrito está vacío",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = {
                                navController.navigate(Screen.Productos.route)
                            }) {
                                Text("Ver Productos")
                            }
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(carrito) { (producto, cantidad) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(12.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    AsyncImage(
                                        model = producto.imagen,
                                        contentDescription = producto.nombre,
                                        modifier = Modifier
                                            .size(120.dp)
                                            .padding(end = 10.dp),
                                        contentScale = ContentScale.Crop,
                                        placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                                        error = painterResource(id = R.drawable.ic_launcher_foreground)
                                    )

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = producto.nombre,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "Subtotal: $${producto.precio * cantidad}",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            IconButton(onClick = {
                                                if (cantidad > 1) {
                                                    scope.launch {
                                                        viewModel.actualizarCantidad(
                                                            itemId = producto.id.toLong(),
                                                            nuevaCantidad = cantidad - 1,
                                                            usuarioId = usuarioId
                                                        ) {}
                                                    }
                                                }
                                            }) {
                                                Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                                            }

                                            Text(
                                                text = cantidad.toString(),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp)
                                            )

                                            IconButton(onClick = {
                                                scope.launch {
                                                    viewModel.actualizarCantidad(
                                                        itemId = producto.id.toLong(),
                                                        nuevaCantidad = cantidad + 1,
                                                        usuarioId = usuarioId
                                                    ) {}
                                                }
                                            }) {
                                                Icon(Icons.Default.Add, contentDescription = "Aumentar")
                                            }
                                        }
                                    }

                                    IconButton(onClick = {
                                        scope.launch {
                                            // Necesitamos el carritoId real de la API
                                            // Por ahora usamos un placeholder
                                            viewModel.eliminarProducto(
                                                carritoId = 1L, // TODO: Obtener ID real
                                                productoId = producto.id.toLong(),
                                                usuarioId = usuarioId
                                            ) {
                                                Toast.makeText(
                                                    context,
                                                    "Producto eliminado",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Eliminar producto",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val total = carrito.sumOf { (producto, cantidad) -> producto.precio * cantidad }

                    Text(
                        text = "Total: $${total}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = "Datos de Pago",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        textAlign = TextAlign.Center
                    )

                    val nombre = viewModel.nombre.value
                    val email = viewModel.email.value
                    val direccion = viewModel.direccion.value
                    val tarjeta = viewModel.tarjeta.value

                    val nombreError = viewModel.nombreError.value
                    val emailError = viewModel.emailError.value
                    val direccionError = viewModel.direccionError.value
                    val tarjetaError = viewModel.tarjetaError.value

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { viewModel.nombre.value = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nombre") },
                        isError = nombreError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (nombreError != null)
                        Text(nombreError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { viewModel.email.value = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Correo") },
                        isError = emailError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (emailError != null)
                        Text(emailError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = direccion,
                        onValueChange = { viewModel.direccion.value = it },
                        label = { Text("Dirección de entrega") },
                        leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Dirección") },
                        isError = direccionError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (direccionError != null)
                        Text(direccionError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = tarjeta,
                        onValueChange = { viewModel.tarjeta.value = it.filter { c -> c.isDigit() } },
                        label = { Text("Número de tarjeta") },
                        leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = "Tarjeta") },
                        isError = tarjetaError != null,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (tarjetaError != null)
                        Text(tarjetaError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.finalizarCompra(
                                    usuarioId = usuarioId,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Compra realizada con éxito",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Notificación
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                            if (puedeNotificar()) {
                                                enviarNotificacion(context)
                                            } else {
                                                requestNotifPermissionLauncher.launch(
                                                    Manifest.permission.POST_NOTIFICATIONS
                                                )
                                            }
                                        } else {
                                            enviarNotificacion(context)
                                        }
                                    },
                                    onError = { mensaje ->
                                        Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Confirmar",
                            modifier = Modifier.padding(end = 6.dp)
                        )
                        Text("Finalizar Compra")
                    }
                }
            }
        }
    }
}

/**
 * Función para enviar la notificación de compra exitosa
 */
fun enviarNotificacion(context: android.content.Context) {
    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, NotificationUtils.CHANNEL_ID)
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setContentTitle("Compra completada")
        .setContentText("Tu compra se ha procesado correctamente. ¡Gracias por confiar en Fútbol Prime!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)

    with(NotificationManagerCompat.from(context)) {
        try {
            notify(1001, builder.build())
        } catch (e: SecurityException) {
            e.printStackTrace()
            Toast.makeText(
                context,
                "No se pudo mostrar la notificación (permiso denegado)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}