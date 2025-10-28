package com.example.futbolprime.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.futbolprime.ui.components.Header
import com.example.futbolprime.ui.components.ProductoCard
import com.example.futbolprime.viewmodel.HomeViewModel

/**
 * `ProductosScreen` muestra la lista de productos disponibles.
 * Se conecta con `HomeViewModel` para obtener los datos desde la base de datos.
 */
@Composable
fun ProductosScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val productosState = viewModel.productos.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Header(navController = navController)

        // Título
        Text(
            text = "Productos disponibles",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp)
        )

        // Lista de productos
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(productosState.value) { producto ->
                ProductoCard(producto = producto)
            }
        }
    }
}
