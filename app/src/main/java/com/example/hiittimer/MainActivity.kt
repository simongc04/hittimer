package com.example.hiittimer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
    var timeRemaining by remember { mutableLongStateOf(0L) }
    var timers = remember {
        mutableStateListOf(
            TimerItem("Trabajo", 10),
            TimerItem("Descanso", 10)
        )
    }
    var counter = CounterDown(1L)

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        Text(text = "Intervalos", fontSize = 24.sp)
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
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(timers.size) { index ->
                TimerWidget(
                    title = timers[index].title,
                    time = timers[index].time,
                    onIncrease = { timers[index].time++ },
                    onDecrease = { if (timers[index].time > 0) timers[index].time-- }
                )
            }
        }

        // Botón de Play
        Button(
            onClick = {
                if (!isRunning) {
                    currentInterval = 1
                    currentPhase = "Trabajo"
                    timeRemaining = (workTime.toLong() * 1000) + 1000
                    isRunning = true
                    CounterDown(workTime) { newTime ->
                        timeRemaining = newTime
                        if (newTime == 0L) togglePhase()
                    }.start()
                }
            },
            modifier = Modifier
                .padding(top = 32.dp)
                .clip(CircleShape)
                .wrapContentSize()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_play),
                contentDescription = "Play",
                modifier = Modifier.size(36.dp)
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
                modifier = Modifier.clickable { if (time > 0) onDecrease() }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = String.format(Locale.US, "%02d:%02d", time / 60, time % 60),
                fontSize = 36.sp
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "➕",
                fontSize = 24.sp,
                modifier = Modifier.clickable { onIncrease() }
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
