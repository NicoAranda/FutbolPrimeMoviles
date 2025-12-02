package com.example.futbolprime.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.futbolprime.viewmodel.UserViewModel

@Composable
fun PerfilScreen(navController: NavController, userViewModel: UserViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Perfil del Usuario", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Aquí mostrarás los datos del usuario logueado")

        Spacer(modifier = Modifier.height(30.dp))

        Button(onClick = { navController.navigateUp() }) {
            Text("Volver")
        }
    }
}
