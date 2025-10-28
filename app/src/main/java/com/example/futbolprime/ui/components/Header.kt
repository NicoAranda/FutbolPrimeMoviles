package com.example.futbolprime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.futbolprime.R
import com.example.futbolprime.navigation.Screen

/**
 * Componente superior (Header) de la aplicación.
 * Incluye el logo, el título "Fútbol Prime" y un menú desplegable con opciones de navegación.
 */
@Composable
fun Header(navController: NavController) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A60FF))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Logo y título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.futbolprime),
                    contentDescription = "Logo Fútbol Prime",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Fútbol Prime",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            // Botón de menú desplegable
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "Menú",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Inicio") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Inicio.route) {
                                popUpTo(Screen.Inicio.route) { inclusive = true }
                            }
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Productos") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Productos.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Carrito") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Carrito.route)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Iniciar Sesión") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Login.route)
                        }
                    )

                }
            }
        }
    }
}
