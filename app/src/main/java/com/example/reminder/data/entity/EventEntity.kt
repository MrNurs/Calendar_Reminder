package com.example.reminder.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val timeMinutes: Int,
    val title: String,
    val colorArgb: Int,
    val done: Boolean
)