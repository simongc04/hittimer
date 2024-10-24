package com.example.hiittimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hiittimer.ui.theme.HIITTimerTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HIITTimerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    var intervalCount by remember { mutableIntStateOf(1) }
    var currentInterval by remember { mutableIntStateOf(1) }
    var timeRemaining by remember { mutableIntStateOf(0) }
    var timers = remember {
        mutableStateListOf(
            TimerItem("Trabajo", 10),
            TimerItem("Descanso", 10)
        )
    }
    var isRunning by remember { mutableStateOf(false) }
    var currentTimerIndex by remember { mutableIntStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Text(text = "Intervalos", fontSize = 24.sp,
                modifier = Modifier.padding(top = 36.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "➖",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { if (intervalCount > 1) intervalCount-- }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = intervalCount.toString(), fontSize = 36.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { intervalCount++ }
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
                        onIncrease = {
                            val index = timers.indexOf(timer)
                            timers[index] = timer.copy(time = timer.time + 1)
                        },
                        onDecrease = {
                            val index = timers.indexOf(timer)
                            if (timer.time > 0) {
                                timers[index] = timer.copy(time = timer.time - 1)
                            }
                        }
                    )
                }

                // Botón para añadir más contadores
                item {
                    Button(
                        onClick = {
                            timers.add(TimerItem("Nuevo contador", 10))
                        },
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
                if (!isRunning) {
                    isRunning = true
                    currentTimerIndex = 0
                    timeRemaining = timers[currentTimerIndex].time
                    coroutineScope.launch {
                        while (currentTimerIndex < timers.size) {
                            val timer = timers[currentTimerIndex]
                            timeRemaining = timer.time
                            while (timeRemaining > 0) {
                                delay(1000L)
                                timeRemaining--
                            }
                            currentTimerIndex++
                        }
                        isRunning = false
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .clip(CircleShape)
                .wrapContentSize()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Play",
                modifier = Modifier.size(36.dp)
            )
        }

        // Muestra el temporizador actual cuando está corriendo
        if (isRunning && currentTimerIndex < timers.size) {
            Text(
                text = "${timers[currentTimerIndex].title}: ${String.format(Locale.US, "%02d:%02d", timeRemaining / 60, timeRemaining % 60)}",
                fontSize = 24.sp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 64.dp)
            )
        }
    }
}

// Widget reutilizable de temporizador
@Composable
fun TimerWidget(
    title: String,
    time: Int,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    var displayedTime by remember { mutableIntStateOf(time) }

    LaunchedEffect(time) {
        displayedTime = time
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        Text(text = title, fontSize = 24.sp, modifier = Modifier.padding(bottom = 8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "➖",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    if (displayedTime > 0) {
                        onDecrease()
                    }
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format(Locale.US, "%02d:%02d", displayedTime / 60, displayedTime % 60),
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "➕",
                fontSize = 24.sp,
                modifier = Modifier.clickable {
                    onIncrease()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CounterPreview() {
    HIITTimerTheme {
        MainScreen()
    }
}
