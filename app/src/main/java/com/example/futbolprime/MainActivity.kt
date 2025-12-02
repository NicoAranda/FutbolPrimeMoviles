package com.example.futbolprime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.futbolprime.data.SessionManager
import com.example.futbolprime.navigation.AppNavigation
import com.example.futbolprime.ui.theme.FutbolPrimeTheme
import com.example.futbolprime.utils.NotificationUtils
import com.example.futbolprime.viewmodel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sessionManager = SessionManager(applicationContext)
        val userViewModel = UserViewModel(sessionManager)
        setContent {
            FutbolPrimeTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        userViewModel = userViewModel,
                        sessionManager = sessionManager
                    )
                }
            }
        }
        NotificationUtils.createNotificationChannel(this)
    }
}
