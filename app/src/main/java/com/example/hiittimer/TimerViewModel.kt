package com.example.hiittimer

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    var intervalCount = mutableIntStateOf(1)
    var currentInterval = mutableIntStateOf(1)
    var timeRemaining = mutableIntStateOf(0)
    var timers = mutableStateListOf(
        TimerItem("Trabajo", 10),
        TimerItem("Descanso", 5)
    )
    var isRunning = mutableStateOf(false)
    var isPaused = mutableStateOf(false)
    var currentTimerIndex = mutableIntStateOf(0)


    // Modificar intervalos
    fun increaseIntervalCount() {
        intervalCount.intValue++
    }

    fun decreaseIntervalCount() {
        if (intervalCount.intValue > 1) {
            intervalCount.intValue--
        }
    }

    // Manejar los temporizadores
    fun incrementTimer(timer: TimerItem) {
        val index = timers.indexOf(timer)
        if (index != -1) {
            timers[index] = timer.copy(time = timer.time + 1)
        }
    }

    fun decrementTimer(timer: TimerItem) {
        val index = timers.indexOf(timer)
        if (index != -1 && timer.time > 0) {
            timers[index] = timer.copy(time = timer.time - 1)
        }
    }

    fun updateTimerName(timer: TimerItem, newName: String) {
        val index = timers.indexOf(timer)
        if (index != -1) {
            val uniqueName = ensureUniqueTimerName(timers, newName, timer.title)
            timers[index] = timer.copy(title = uniqueName)
        }
    }

    fun addNewTimer() {
        val uniqueTimerName = ensureUniqueTimerName(timers, "Nuevo temporizador")
        timers.add(TimerItem(uniqueTimerName, 10))
    }

    // Iniciar el temporizador
    fun startTimer(onCycleFinished: () -> Unit) {
        if (!isRunning.value && timers.isNotEmpty()) {
            isRunning.value = true
            isPaused.value = false
            viewModelScope.launch {
                for (interval in 1..intervalCount.intValue) {
                    currentInterval.intValue = interval
                    for (index in timers.indices) {
                        currentTimerIndex.intValue = index
                        val timer = timers[index]
                        timeRemaining.intValue = timer.time
                        while (timeRemaining.intValue > 0 && isRunning.value) {
                            delay(1000L)
                            if (!isPaused.value) {
                                timeRemaining.intValue--
                            }
                        }
                        if (!isRunning.value) {
                            break
                        }
                    }
                    if (!isRunning.value) {
                        break
                    }
                }
                isRunning.value = false
                onCycleFinished()
            }
        }
    }

    // Pausar el temporizador
    fun pauseTimer() {
        if (isRunning.value && !isPaused.value) {
            isPaused.value = true
        }
    }

    // Reanudar el temporizador
    fun resumeTimer() {
        if (isRunning.value && isPaused.value) {
            isPaused.value = false
        }
    }

    // Reiniciar el temporizador
    fun resetTimer() {
        isRunning.value = false
        isPaused.value = false
        timeRemaining.intValue = 0
        currentTimerIndex.intValue = 0
    }
}
