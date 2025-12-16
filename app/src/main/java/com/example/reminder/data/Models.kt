package com.example.reminder.data

import java.time.LocalDate

data class DayMark(
    val date: LocalDate,
    val colorArgb: Int
)

data class DayEvent(
    val id: Long,
    val date: LocalDate,
    val timeMinutes: Int,
    val title: String,
    val colorArgb: Int,
    val done: Boolean = false
)
