package com.example.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "day_marks")
data class DayMarkEntity(
    @PrimaryKey val date: String,
    val colorArgb: Int
)
