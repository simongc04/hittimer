package com.example.hiittimer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun TimerScreen(timerViewModel: TimerViewModel, backgroundColor: Color, modifier: Modifier = Modifier) {
    val currentTimerIndex by timerViewModel.currentTimerIndex
    val timeRemaining by timerViewModel.timeRemaining

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timerViewModel.timers[currentTimerIndex].title,
                fontSize = 36.sp,
                color = Color.White,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                fontSize = 48.sp,
                color = Color.White
            )
        }
    }
}
