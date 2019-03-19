package com.example.euphoria.Extensions

import android.annotation.SuppressLint
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@SuppressLint("SimpleDateFormat")
fun Date.dateMonthString() : String {
    val formatter = SimpleDateFormat("MMM dd, yyyy h:mm:ss a")
    val symbols = DateFormatSymbols(Locale.getDefault())
    symbols.amPmStrings = arrayOf("am", "pm")
    formatter.dateFormatSymbols = symbols
    return formatter.format(this)
}

@SuppressLint("SimpleDateFormat")
fun Date.monthYearString() : String {
    val formatter = SimpleDateFormat("MMM, yyyy")
    return formatter.format(this)
}

fun Long.duration(): String {
    val hours = TimeUnit.SECONDS.toHours(this)
    val minutes = TimeUnit.SECONDS.toMinutes(this - (hours * 3600))
    val hoursString = hours.toString().padStart(2, '0')
    val minutesString = minutes.toString().padStart(2, '0')
    return hoursString + ":" + minutesString
}