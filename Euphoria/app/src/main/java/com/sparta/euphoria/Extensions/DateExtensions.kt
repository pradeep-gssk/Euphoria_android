package com.sparta.euphoria.Extensions

import java.util.concurrent.TimeUnit

fun Long.duration(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this - (hours * 3600))
    val hoursString = hours.toString().padStart(2, '0')
    val minutesString = minutes.toString().padStart(2, '0')
    return hoursString + ":" + minutesString
}

fun Long.fullDuration(): String {
    val milliSeconds = this * 1000
    val hours = TimeUnit.MILLISECONDS.toHours(milliSeconds)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(milliSeconds - (hours * 3600000))
    val seconds = TimeUnit.MILLISECONDS.toSeconds(milliSeconds - (hours * 3600000) - (minutes * 60000))
    val hoursString = hours.toString().padStart(2, '0')
    val minutesString = minutes.toString().padStart(2, '0')
    val secondsString = seconds.toString().padStart(2, '0')
    return hoursString + ":" + minutesString + ":" + secondsString
}