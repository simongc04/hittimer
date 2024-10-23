package com.example.hiittimer

import android.os.CountDownTimer

class CounterDown(initialTimeMillis: Long) {
    private var countDownTime: Long = initialTimeMillis
    private var timeRemaining: Long = initialTimeMillis
    private var countDownTimer: CountDownTimer? = null
    private var isPaused: Boolean = false

    fun start(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        if (countDownTimer == null) {
            createCountDownTimer(timeRemaining, onTick, onFinish)
        }
        countDownTimer?.start()
        isPaused = false
    }

    fun pause() {
        countDownTimer?.cancel()
        isPaused = true
    }

    fun resume(onTick: (Long) -> Unit, onFinish: () -> Unit) {
        if (isPaused) {
            createCountDownTimer(timeRemaining, onTick, onFinish)
            countDownTimer?.start()
            isPaused = false
        }
    }

    fun reset() {
        countDownTimer?.cancel()
        timeRemaining = countDownTime
        countDownTimer = null
        isPaused = false
    }

    fun changeTime(newTimeMillis: Long) {
        countDownTimer?.cancel()
        countDownTime = newTimeMillis
        timeRemaining = newTimeMillis
        countDownTimer = null
        isPaused = false
    }

    private fun createCountDownTimer(timeMillis: Long, onTick: (Long) -> Unit, onFinish: () -> Unit) {
        countDownTimer = object : CountDownTimer(timeMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                onTick(millisUntilFinished)
            }

            override fun onFinish() {
                timeRemaining = 0
                onFinish()
            }
        }
    }
}