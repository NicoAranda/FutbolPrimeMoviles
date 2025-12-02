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
import com.example.futbolprime.data.SessionManager
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.viewmodel.*

@Composable
fun LoginScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    sessionManager: SessionManager
) {
    val context = LocalContext.current

    // ✅ Crear LoginViewModel con sessionManager verificado
    val viewModel = remember(sessionManager) {
        LoginViewModel(sessionManager)
    }

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

                // ✅ Guardar sesión usando email como token
                userViewModel.login(
                    token = usuario.email,
                    nombre = usuario.nombre
                )

                Toast.makeText(
                    context,
                    "Bienvenido ${usuario.nombre}",
                    Toast.LENGTH_SHORT
                ).show()

                navController.navigate(Screen.Productos.route) {
                    popUpTo(Screen.Inicio.route) { inclusive = true }
                }
                viewModel.resetLoginState()
            }

            is LoginState.Error -> {
                Toast.makeText(
                    context,
                    (loginState as LoginState.Error).mensaje,
                    Toast.LENGTH_LONG
                ).show()
                viewModel.resetLoginState()
            }

            else -> Unit
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Header(navController = navController, userViewModel)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Iniciar Sesión", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // EMAIL
            OutlinedTextField(
                value = username,
                onValueChange = { viewModel.username.value = it },
                label = { Text("Correo Electrónico") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Email") },
                isError = usernameError != null,
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            )
            if (usernameError != null) {
                Text(
                    usernameError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // PASSWORD
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
            if (passwordError != null) {
                Text(
                    passwordError,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LOADING
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            // BOTÓN LOGIN
            Button(
                onClick = { viewModel.login() },
                enabled = loginState !is LoginState.Loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Login, contentDescription = "Entrar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Entrar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // INFO DE PRUEBA
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        "Credenciales de Prueba:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Email: juan@correo.cl", style = MaterialTheme.typography.bodySmall)
                    Text("Contraseña: juan1234", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}