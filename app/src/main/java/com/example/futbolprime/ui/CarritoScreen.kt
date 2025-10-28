package com.example.futbolprime.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.painterResource
import com.example.futbolprime.model.Producto
import com.example.futbolprime.repository.CarritoRepository
import com.example.futbolprime.ui.components.Header

@Composable
fun CarritoScreen(navController: NavHostController) {
    val context = LocalContext.current
    val carritoRepo = remember { CarritoRepository(context) }

    var carrito by remember { mutableStateOf(listOf<Pair<Producto, Int>>()) }

    LaunchedEffect(Unit) {
        carrito = carritoRepo.obtenerCarrito()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Carrito de Compras",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
                    Text(
                        text = "Tu carrito está vacío",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                Image(
                                    painter = painterResource(id = producto.imagen),
                                    contentDescription = producto.nombre,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .padding(end = 10.dp),
                                    contentScale = ContentScale.Crop
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
                                        text = "Precio: $${producto.precio}",
                                        fontSize = 14.sp
                                    )
                                    Text(
                                        text = "Cantidad: $cantidad",
                                        fontSize = 14.sp
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        carritoRepo.eliminarDelCarrito(producto.id)
                                        carrito = carritoRepo.obtenerCarrito()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
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
                    text = "💳 Datos de Pago",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                var nombre by remember { mutableStateOf("") }
                var email by remember { mutableStateOf("") }
                var direccion by remember { mutableStateOf("") }
                var tarjeta by remember { mutableStateOf("") }

                var nombreError by remember { mutableStateOf<String?>(null) }
                var emailError by remember { mutableStateOf<String?>(null) }
                var direccionError by remember { mutableStateOf<String?>(null) }
                var tarjetaError by remember { mutableStateOf<String?>(null) }

                fun validarCampos(): Boolean {
                    var valido = true

                    nombreError = if (nombre.isBlank()) {
                        valido = false
                        "El nombre no puede estar vacío"
                    } else null

                    emailError = if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        valido = false
                        "Correo inválido"
                    } else null

                    direccionError = if (direccion.isBlank()) {
                        valido = false
                        "La dirección no puede estar vacía"
                    } else null

                    tarjetaError = if (tarjeta.length < 12) {
                        valido = false
                        "Número de tarjeta inválido (mínimo 12 dígitos)"
                    } else null

                    return valido
                }

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    isError = nombreError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nombreError != null) Text(nombreError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    isError = emailError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (emailError != null) Text(emailError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección de entrega") },
                    isError = direccionError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (direccionError != null) Text(direccionError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = tarjeta,
                    onValueChange = { tarjeta = it.filter { char -> char.isDigit() } },
                    label = { Text("Número de tarjeta") },
                    isError = tarjetaError != null,
                    modifier = Modifier.fillMaxWidth()
                )
                if (tarjetaError != null) Text(tarjetaError!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (validarCampos()) {
                            carritoRepo.vaciarCarrito()
                            carrito = emptyList()

                            nombre = ""
                            email = ""
                            direccion = ""
                            tarjeta = ""

                            nombreError = null
                            emailError = null
                            direccionError = null
                            tarjetaError = null

                            Toast.makeText(context, "Compra realizada con éxito", Toast.LENGTH_LONG).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finalizar Compra")
                }


            }
        }
    }
}
