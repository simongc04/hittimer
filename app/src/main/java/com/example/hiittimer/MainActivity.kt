package com.example.hiittimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
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
    val timerViewModel: TimerViewModel = viewModel()

    NavHost(navController = navController, startDestination = "mainScreen") {
        composable("mainScreen") {
            MainScreen(timerViewModel = timerViewModel, navController = navController)
        }
        composable("timerScreen") {
            TimerScreen(
                timerViewModel = timerViewModel,
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