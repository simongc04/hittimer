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
    var currentTimerIndex = mutableIntStateOf(0)

    // Métodos para modificar la cantidad de intervalos
    fun increaseIntervalCount() {
        intervalCount.intValue++
    }

    fun decreaseIntervalCount() {
        if (intervalCount.intValue > 1) {
            intervalCount.intValue--
        }
    }

    // Métodos para manejar los temporizadores
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
        val uniqueTimerName = ensureUniqueTimerName(timers, "Nuevo contador")
        timers.add(TimerItem(uniqueTimerName, 10))
    }

    // Método para iniciar el temporizador
    fun startTimer() {
        if (!isRunning.value && timers.isNotEmpty()) {
            isRunning.value = true
            currentTimerIndex.intValue = 0
            timeRemaining.intValue = timers[currentTimerIndex.intValue].time

            viewModelScope.launch {
                while (currentTimerIndex.intValue < timers.size && isRunning.value) {
                    val timer = timers[currentTimerIndex.intValue]
                    timeRemaining.intValue = timer.time
                    while (timeRemaining.intValue > 0 && isRunning.value) {
                        delay(1000L)
                        timeRemaining.intValue--
                    }
                    if (isRunning.value) {
                        currentTimerIndex.intValue++
                    }
                }
                isRunning.value = false
            }
        }
    }
}
