package com.example.hiittimer

import android.app.Application
import android.media.MediaPlayer
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewModel(application: Application) : AndroidViewModel(application) {
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

    val context = application.applicationContext
    var mediaPlayer: MediaPlayer? = null

    private val sharedPreferencesHelper = SharedPreferencesHelper(context)

    // Leer timers de memoria
    fun loadTimersFromSharedPreferences() {
        timers.clear()
        timers.addAll(sharedPreferencesHelper.getTimers())
    }

    // Guardar timers en memoria
    fun saveTimersToSharedPreferences() {
        sharedPreferencesHelper.saveTimers(timers)
    }

    // Función para reproducir sonidos
    private fun playSound(soundResourceId: Int, duration: Long? = null) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, soundResourceId).apply {
            start()
            duration?.let {
                // Detener el sonido después de un tiempo específico
                val handler = android.os.Handler()
                handler.postDelayed({
                    if (isPlaying) {
                        stop()
                        release()
                    }
                }, it)
            }
            setOnCompletionListener { release() }
        }
    }

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

                        // Reproducir el sonido de inicio para cada temporizador al comienzo
                        playSound(R.raw.start_sound, 1000)

                        // Iniciar el conteo regresivo para el temporizador actual
                        while (timeRemaining.intValue > 0 && isRunning.value) {
                            delay(1000L)

                            if (!isPaused.value) {
                                timeRemaining.intValue--

                                // Reproducir sonido en los últimos 3 segundos del temporizador
                                if (timeRemaining.intValue in 1..3) {
                                    playSound(R.raw.finish_sound) // Para el descanso y el trabajo
                                }
                            }
                        }

                        if (!isRunning.value) {
                            break
                        }
                        playSound(R.raw.cycle_end_sound) // Reproducir sonido de fin de ciclo
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
            playSound(R.raw.pause_sound) // Reproducir sonido de pausa
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
        mediaPlayer?.release() // Liberar el MediaPlayer al reiniciar
        mediaPlayer = null // Asegurar que no haya referencias a un MediaPlayer liberado
    }
}

