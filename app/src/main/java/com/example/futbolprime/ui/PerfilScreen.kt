package com.example.futbolprime.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.navigation.Screen
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.utils.UserSessionManager
import com.example.futbolprime.viewmodel.UserViewModel

@Composable
fun PerfilScreen(navController: NavController, userViewModel: UserViewModel) {
    val context = LocalContext.current

    // ✅ Obtener información del usuario desde UserSessionManager
    val userInfo = remember {
        UserSessionManager.getUserInfo(context)
    }

    // ✅ Debug: Verificar si hay sesión
    val isLoggedIn = UserSessionManager.isLoggedIn(context)

    LaunchedEffect(Unit) {
        println("🔍 DEBUG - Perfil Screen")
        println("¿Está logueado? $isLoggedIn")
        println("UserInfo: $userInfo")
    }

    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Header(navController = navController, userViewModel)

        // Si no hay usuario, mostrar pantalla de login requerido
        if (userInfo == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Sin sesión",
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No hay sesión activa",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Inicia sesión para ver tu perfil",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Perfil.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.6f)
                ) {
                    Icon(Icons.Default.Login, contentDescription = "Login")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Iniciar Sesión")
                }
            }
        } else {
            // Usuario logueado - Mostrar perfil
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar circular con inicial
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userInfo.nombre.firstOrNull()?.uppercase() ?: "U",
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Perfil del Usuario",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Card con información del usuario
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // ID de Usuario
                        InfoRow(
                            icon = Icons.Default.Badge,
                            label = "ID de Usuario",
                            value = userInfo.id.toString()
                        )

                        HorizontalDivider()

                        // Nombre
                        InfoRow(
                            icon = Icons.Default.Person,
                            label = "Nombre Completo",
                            value = userInfo.nombre
                        )

                        HorizontalDivider()

                        // Email
                        InfoRow(
                            icon = Icons.Default.Email,
                            label = "Correo Electrónico",
                            value = userInfo.email
                        )

                        HorizontalDivider()

                        // Rol
                        InfoRow(
                            icon = Icons.Default.AdminPanelSettings,
                            label = "Rol",
                            value = userInfo.rol
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón Cerrar Sesión
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        Icons.Default.Logout,
                        contentDescription = "Cerrar Sesión",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Cerrar Sesión")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón Volver
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Volver")
                }
            }
        }
    }

    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        // ✅ Cerrar sesión en ambos lugares
                        UserSessionManager.logout(context)
                        userViewModel.logout()

                        Toast.makeText(
                            context,
                            "Sesión cerrada exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navegar a inicio
                        navController.navigate(Screen.Inicio.route) {
                            popUpTo(0) { inclusive = true }
                        }
                        showLogoutDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = label,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}