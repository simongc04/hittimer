package com.example.hiittimer

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
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
    var workTime by remember { mutableIntStateOf(0) }
    var restTime by remember { mutableIntStateOf(0) }
    var currentInterval by remember { mutableIntStateOf(1) }
    var isRunning by remember { mutableStateOf(false) }
    var isPaused by remember { mutableStateOf(false) }
    var currentPhase by remember { mutableStateOf("Trabajo") }
    var timeRemaining by remember { mutableLongStateOf(0L) }
    var counterDown: CounterDown? by remember { mutableStateOf(null) }

    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf(MediaPlayer.create(context, R.raw.rest_sound)) }

    fun resetMediaPlayer() {
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer.create(context, R.raw.rest_sound)
    }

    fun togglePhase() {
        if (currentPhase == "Trabajo") {
            currentPhase = "Descanso"
            timeRemaining = (restTime.toLong() * 1000) + 1000
            mediaPlayer.start()
            counterDown = CounterDown(restTime) { newTime ->
                timeRemaining = newTime
                if (newTime == 0L) {
                    mediaPlayer.stop()
                    resetMediaPlayer()
                    togglePhase()
                }
            }.apply { start() }
        } else {
            if (currentInterval < intervalCount) {
                currentInterval++
                currentPhase = "Trabajo"
                timeRemaining = (workTime.toLong() * 1000) + 1000
                counterDown = CounterDown(workTime) { newTime ->
                    timeRemaining = newTime
                    if (newTime == 0L) togglePhase()
                }.apply { start() }
            } else {
                isRunning = false
            }
        }
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        if (isRunning) {
            Text(
                text = if (currentPhase == "Trabajo") "Tiempo de Trabajar" else "Tiempo de Descansar",
                fontSize = 36.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Text(
                text = String.format(Locale.US, "%02d:%02d", timeRemaining / 60, timeRemaining % 60),
                fontSize = 48.sp
            )
            Button(
                onClick = {
                    if (isPaused) {
                        counterDown?.toggle()
                        counterDown = CounterDown(timeRemaining.toInt()) { newTime ->
                            timeRemaining = newTime
                            if (newTime == 0L) togglePhase()
                        }.apply { start() }
                    } else {
                        counterDown?.toggle()
                    }
                    isPaused = !isPaused
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Icon(
                    painter = if (isPaused) {
                        painterResource(id = R.drawable.ic_play)
                    } else {
                        painterResource(id = R.drawable.ic_pause)
                    },
                    contentDescription = if (isPaused) "Play" else "Pause",
                    modifier = Modifier.size(36.dp)
                )
            }
        } else {
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

            Text(text = "Tiempo de Trabajo", fontSize = 24.sp, modifier = Modifier.padding(top = 24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "➖",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { if (workTime > 0) workTime-- }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format(Locale.US, "%02d:%02d", workTime / 60, workTime % 60),
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { workTime++ }
                )
            }

            // Tiempo de Descanso
            Text(text = "Tiempo de Descanso", fontSize = 24.sp, modifier = Modifier.padding(top = 24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "➖",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { if (restTime > 0) restTime-- }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = String.format(Locale.US, "%02d:%02d", restTime / 60, restTime % 60),
                    fontSize = 36.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "➕",
                    fontSize = 24.sp,
                    modifier = Modifier.clickable { restTime++ }
                )
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
}

@Preview(showBackground = true)
@Composable
fun CounterPreview() {
    HIITTimerTheme {
        MainScreen()
    }
}
