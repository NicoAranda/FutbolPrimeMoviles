package com.example.futbolprime.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.futbolprime.ui.*

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Productos : Screen("productos")
    object Login : Screen("login")
    object Carrito : Screen("carrito")
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route
    ) {
        composable(Screen.Inicio.route) {
            InicioScreen(navController)
        }
        composable(Screen.Productos.route) {
            ProductosScreen(navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.Carrito.route) {
            CarritoScreen(navController)
        }
    }
}
