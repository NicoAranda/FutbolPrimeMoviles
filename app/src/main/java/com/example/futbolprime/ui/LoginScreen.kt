package com.example.futbolprime.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.utils.UserSessionManager
import com.example.futbolprime.viewmodel.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val loginState by viewModel.loginState.collectAsState()

    val username = viewModel.username.value
    val password = viewModel.password.value

    val usernameError = viewModel.usernameError.value
    val passwordError = viewModel.passwordError.value

    // Observar el estado del login
    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val usuario = (loginState as LoginState.Success).usuario

                // 🔹 IMPORTANTE: Guardar la sesión del usuario
                UserSessionManager.saveUserSession(context, usuario)

                Toast.makeText(
                    context,
                    "Bienvenido ${usuario.nombreCompleto}",
                    Toast.LENGTH_SHORT
                ).show()

                navController.navigate(Screen.Productos.route) {
                    popUpTo(Screen.Inicio.route) { inclusive = true }
                }
                viewModel.resetLoginState()
            }
            is LoginState.Error -> {
                val mensaje = (loginState as LoginState.Error).mensaje
                Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                viewModel.resetLoginState()
            }
            else -> { /* Idle o Loading */ }
        }
    }

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
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            if (usernameError != null)
                Text(
                    usernameError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.password.value = it },
                label = { Text("Contraseña") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = passwordError != null,
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError != null)
                Text(
                    passwordError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )

            Spacer(modifier = Modifier.height(24.dp))

            // Mostrar indicador de carga
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            Button(
                onClick = { viewModel.login() },
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    Icons.Default.Login,
                    contentDescription = "Entrar",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text("Entrar")
            }
        }
    }
}