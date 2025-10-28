package com.example.futbolprime.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.R
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header

/**
 * Pantalla de inicio de la aplicación.
 * Muestra el encabezado, un mensaje de bienvenida e incluye botones
 * para acceder a los productos o iniciar sesión.
 */
@Composable
fun InicioScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 🔹 Header fijo arriba, sin padding
        Header(navController = navController)

        // 🔹 Contenido con padding y centrado
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Imagen opcional de bienvenida
            Image(
                painter = painterResource(id = R.drawable.futbolprime),
                contentDescription = "Logo Fútbol Prime",
                modifier = Modifier
                    .size(180.dp)
                    .padding(bottom = 24.dp)
            )

            // Texto de bienvenida
            Text(
                text = "Bienvenido a Fútbol Prime",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Botón para ir a los productos
            Button(
                onClick = { navController.navigate(Screen.Productos.route) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(text = "Ver Productos")
            }

            // Botón para iniciar sesión
            Button(
                onClick = { navController.navigate(Screen.Login.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Iniciar Sesión")
            }
        }
    }
}
