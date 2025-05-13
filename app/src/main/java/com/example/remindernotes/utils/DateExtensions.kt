package com.example.remindernotes.utils
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

fun LocalDate.toCustomString(): String {
    val month = this.month.getDisplayName(TextStyle.FULL, Locale.US)
    val day = this.dayOfMonth
    val year = this.year

    val daySuffix = when (day) {
        1, 21, 31 -> "st"
        2, 22 -> "nd"
        3, 23 -> "rd"
        else -> "th"
    }

    return "$month ${day}$daySuffix, $year"
}