package com.example.hiittimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hiittimer.ui.theme.HIITTimerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HIITTimerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(timerViewModel = TimerViewModel(), navController = navController)
        }
        composable("timerScreen") { backStackEntry ->
            // Recoger parámetros o cualquier argumento adicional necesario.
            val timerViewModel = TimerViewModel()
            val timer = timerViewModel.timers.first() // Ejemplo para obtener un temporizador. Puede adaptarse según la lógica.

            TimerScreen(
                timer = timer,
                timeRemaining = timerViewModel.timeRemaining.intValue,
                backgroundColor = Color.Blue
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    HIITTimerTheme {
        AppNavigation()
    }
}
