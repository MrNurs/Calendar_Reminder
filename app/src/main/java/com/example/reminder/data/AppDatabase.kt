package com.example.reminder.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.reminder.data.dao.DayMarkDao
import com.example.reminder.data.dao.EventDao
import com.example.reminder.data.entity.DayMarkEntity
import com.example.reminder.data.entity.EventEntity

@Database(
    entities = [EventEntity::class, DayMarkEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao
    abstract fun dayMarkDao(): DayMarkDao
}

