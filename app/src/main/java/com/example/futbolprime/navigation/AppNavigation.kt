package com.example.futbolprime.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.futbolprime.data.SessionManager
import com.example.futbolprime.ui.*
import com.example.futbolprime.viewmodel.UserViewModel

sealed class Screen(val route: String) {
    object Inicio : Screen("inicio")
    object Productos : Screen("productos")
    object Login : Screen("login")
    object Carrito : Screen("carrito")
    object Perfil : Screen("perfil")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    userViewModel: UserViewModel,
    sessionManager: SessionManager
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Inicio.route
    ) {
        composable(Screen.Inicio.route) {
            InicioScreen(navController, userViewModel)
        }
        composable(Screen.Productos.route) {
            ProductosScreen(navController, userViewModel)
        }
        composable(Screen.Login.route) {
            LoginScreen(
                navController = navController,
                userViewModel = userViewModel,
                sessionManager = sessionManager
            )
        }
        composable(Screen.Carrito.route) {
            CarritoScreen(navController, userViewModel)
        }
        composable(Screen.Perfil.route) {
            PerfilScreen(navController, userViewModel)
        }
    }
}

