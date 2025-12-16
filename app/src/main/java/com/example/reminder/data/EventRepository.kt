package com.example.reminder.data

import com.example.reminder.data.dao.DayMarkDao
import com.example.reminder.data.dao.EventDao
import com.example.reminder.data.entity.DayMarkEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class EventRepository(
    private val eventDao: EventDao,
    private val dayMarkDao: DayMarkDao
) {

    val events = eventDao.observeAll().map {
        it.map{
            e -> e.toModel()
        }
    }

    val dayMarks = dayMarkDao.observeAll()
        .map { list -> list.associate { it.toModel() } }

    suspend fun addEvent(e: DayEvent) {
        eventDao.insert(e.toEntity())
    }

    suspend fun updateEvent(e: DayEvent) {
        eventDao.update(e.toEntity())
    }

    suspend fun deleteEvent(e: DayEvent) {
        eventDao.delete(e.toEntity())
    }

    suspend fun setDayMark(date: LocalDate, color: Int?) {
        if (color == null) {
            dayMarkDao.delete(date.toString())
        } else {
            dayMarkDao.upsert(
                DayMarkEntity(
                    date = date.toString(),
                    colorArgb = color
                )
            )
        }
    }
}
