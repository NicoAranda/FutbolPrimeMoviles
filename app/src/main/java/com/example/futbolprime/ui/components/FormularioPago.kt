package com.example.futbolprime.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.futbolprime.utils.*
import com.example.futbolprime.utils.NotificationUtils.createNotificationChannel

@Composable
fun FormularioPago(
    context: Context,
    total: Int,
    onPagoConfirmado: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Datos de envío",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre completo") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = direccion,
                onValueChange = { direccion = it },
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error!!,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(Modifier.height(16.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    when {
                        nombre.isBlank() ->
                            error = "Debes ingresar tu nombre"
                        direccion.isBlank() ->
                            error = "Debes ingresar tu dirección"
                        telefono.isBlank() ->
                            error = "Debes ingresar un teléfono"
                        else -> {
                            error = null

                            Toast.makeText(
                                context,
                                "Compra realizada por $$total",
                                Toast.LENGTH_LONG
                            ).show()

                            createNotificationChannel(context)
                            onPagoConfirmado()
                        }
                    }
                }
            ) {
                Text("Finalizar compra • $$total")
            }
        }
    }
}
