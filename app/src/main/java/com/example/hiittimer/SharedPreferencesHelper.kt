package com.example.hiittimer

import android.content.Context
import com.google.gson.Gson

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("hiit_timer_prefs", Context.MODE_PRIVATE)

    fun getTimers(): List<TimerItem> {
        val timersJson = sharedPreferences.getString("timers", "[]")
        return if (timersJson != null) {
            Gson().fromJson(timersJson, Array<TimerItem>::class.java).toList()
        } else {
            emptyList()
        }
    }

    fun saveTimers(timers: List<TimerItem>) {
        val editor = sharedPreferences.edit()
        val timersJson = Gson().toJson(timers)
        editor.putString("timers", timersJson)
        editor.apply()
    }
}