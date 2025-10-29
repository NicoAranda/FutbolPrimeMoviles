package com.example.futbolprime.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.repository.DatabaseHelper
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.viewmodel.*

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    val username = viewModel.username.value
    val password = viewModel.password.value

    val usernameError = viewModel.usernameError.value
    val passwordError = viewModel.passwordError.value

    Column(modifier = Modifier.fillMaxSize()) {
        Header(navController = navController)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Usuario / Mail") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Usuario") },
                isError = usernameError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (usernameError != null)
                Text(usernameError, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.bodySmall.fontSize)

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError != null)
                Text(passwordError, color = MaterialTheme.colorScheme.error, fontSize = MaterialTheme.typography.bodySmall.fontSize)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (viewModel.validarCampos()) {
                        val db = dbHelper.readableDatabase
                        val cursor = db.rawQuery(
                            "SELECT * FROM ${DatabaseHelper.TABLE_USUARIOS} WHERE ${DatabaseHelper.COLUMN_USU_NOMBRE_USUARIO}=? AND ${DatabaseHelper.COLUMN_USU_CONTRASENA}=?",
                            arrayOf(username, password)
                        )
                        if (cursor.moveToFirst()) {
                            Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                            cursor.close()
                            db.close()
                            navController.navigate(Screen.Productos.route) {
                                popUpTo(Screen.Inicio.route) { inclusive = true }
                            }
                        } else {
                            Toast.makeText(context, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            cursor.close()
                            db.close()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = "Entrar", modifier = Modifier.padding(end = 8.dp))
                Text("Entrar")
            }
        }
    }
}
