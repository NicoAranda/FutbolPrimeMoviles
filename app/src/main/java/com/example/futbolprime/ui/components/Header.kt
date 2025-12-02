package com.example.futbolprime.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
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
import com.example.futbolprime.viewmodel.UserViewModel

/**
 * Componente superior (Header) de la aplicación.
 * Incluye el logo, el título "Fútbol Prime" y un menú desplegable con opciones de navegación.
 */
@Composable
fun Header(navController: NavController, userViewModel: UserViewModel) {

    val token by userViewModel.token.observeAsState()

    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0A60FF))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.futbolprime),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp)
                )
                Text(
                    text = "Fútbol Prime",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Menú
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.menu),
                        contentDescription = "Menú",
                        tint = Color.White
                    )
                }

                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {

                    DropdownMenuItem(
                        text = { Text("Inicio") },
                        onClick = {
                            expanded = false
                            navController.navigate(Screen.Inicio.route)
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

                    // 🔹 Si el usuario está logueado → Ver Perfil
                    if (token != null) {
                        DropdownMenuItem(
                            text = { Text("Ver Perfil") },
                            onClick = {
                                expanded = false
                                navController.navigate(Screen.Perfil.route)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                expanded = false
                                userViewModel.logout()
                                navController.navigate(Screen.Inicio.route)
                            }
                        )
                    } else {
                        // 🔹 Si NO está logueado → Iniciar Sesión
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
}

