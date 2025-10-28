package com.example.futbolprime.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.repository.DatabaseHelper
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header

/**
 * Pantalla de inicio de sesión.
 * Permite a los usuarios autenticarse con los datos almacenados en SQLite.
 */
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // 🔹 Header fijo arriba, sin padding
        Header(navController = navController)

        // 🔹 Contenido con padding independiente
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Iniciar Sesión",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de usuario
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de contraseña
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de inicio de sesión
            Button(
                onClick = {
                    val db = dbHelper.readableDatabase
                    val cursor = db.rawQuery(
                        "SELECT * FROM ${DatabaseHelper.TABLE_USUARIOS} WHERE ${DatabaseHelper.COLUMN_USU_NOMBRE_USUARIO}=? AND ${DatabaseHelper.COLUMN_USU_CONTRASENA}=?",
                        arrayOf(username, password)
                    )

                    if (cursor.moveToFirst()) {
                        Toast.makeText(context, "Inicio de sesión exitoso ✅", Toast.LENGTH_SHORT).show()
                        cursor.close()
                        db.close()
                        navController.navigate(Screen.Productos.route) {
                            popUpTo(Screen.Inicio.route) { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Usuario o contraseña incorrectos ❌", Toast.LENGTH_SHORT).show()
                        cursor.close()
                        db.close()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate(Screen.Inicio.route) }) {
                Text("Volver al inicio")
            }
        }
    }
}
