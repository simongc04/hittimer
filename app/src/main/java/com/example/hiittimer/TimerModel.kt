package com.example.hiittimer

data class TimerItem(val title: String, val time: Int)

fun ensureUniqueTimerName(timers: List<TimerItem>, proposedName: String, originalName: String? = null): String {
    if (originalName != null && proposedName == originalName) return proposedName

    var newName = proposedName
    var counter = 1

    while (timers.any { it.title == newName && it.title != originalName }) {
        counter++
        newName = "${proposedName}_$counter"
    }

    return newName
}
