package com.example.hiittimer

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun MainScreen(modifier: Modifier = Modifier, timerViewModel: TimerViewModel) {
    val intervalCount by timerViewModel.intervalCount
    val timers by timerViewModel.timers
    val isRunning by timerViewModel.isRunning
    val currentTimerIndex by timerViewModel.currentTimerIndex
    val timeRemaining by timerViewModel.timeRemaining
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF121212))) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = "Intervalos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(top = 36.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "➖",
                    fontSize = 24.sp,
                    color = Color.Green,
                    modifier = Modifier.clickable { if (intervalCount > 1) timerViewModel.decreaseIntervalCount() }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = intervalCount.toString(), fontSize = 36.sp, color = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    color = Color.Green,
                    modifier = Modifier.clickable { timerViewModel.increaseIntervalCount() }
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(timers) { timer ->
                    TimerWidget(
                        title = timer.title,
                        time = timer.time,
                        onIncrease = { timerViewModel.incrementTimer(timer) },
                        onDecrease = { timerViewModel.decrementTimer(timer) },
                        onNameChange = { newName -> timerViewModel.updateTimerName(timer, newName) }
                    )
                }

                // Botón para añadir más contadores
                item {
                    Button(
                        onClick = { timerViewModel.addNewTimer() },
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(CircleShape)
                            .wrapContentSize()
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_add_alarm),
                            contentDescription = "Añadir",
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }
        }

        // Botón de Play
        Button(
            onClick = {
                Log.d("MainScreen", "Botón de Play clickeado")
                if (!isRunning && timers.isNotEmpty()) {
                    Log.d("MainScreen", "Iniciando temporizadores")
                    coroutineScope.launch { timerViewModel.startTimer() }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clip(CircleShape)
                .wrapContentSize()
                .background(Color.Blue)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Play",
                modifier = Modifier.size(36.dp)
            )
        }

        // Muestra el temporizador actual cuando está corriendo
        if (isRunning && currentTimerIndex < timers.size) {
            Log.d("MainScreen", "Mostrando temporizador actual")
            Text(
                text = "${timers[currentTimerIndex].title}: ${String.format(Locale.US, "%02d:%02d", timeRemaining / 60, timeRemaining % 60)}",
                fontSize = 24.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
                    .background(Color.Gray, shape = CircleShape)
                    .padding(16.dp)
            )
        }
    }
}