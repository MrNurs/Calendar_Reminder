package com.example.reminder.data

import com.example.reminder.data.entity.DayMarkEntity
import com.example.reminder.data.entity.EventEntity
import java.time.LocalDate

fun EventEntity.toModel(): DayEvent = DayEvent(
    id = id,
    date = LocalDate.parse(date),
    timeMinutes = timeMinutes,
    title = title,
    colorArgb = colorArgb,
    done = done
)

fun DayEvent.toEntity(): EventEntity = EventEntity(
    id = id,
    date = date.toString(),
    timeMinutes = timeMinutes,
    title = title,
    colorArgb = colorArgb,
    done = done
)
fun DayMarkEntity.toModel(): Pair<LocalDate, Int> =
    LocalDate.parse(date) to colorArgb

fun Pair<LocalDate, Int>.toEntity(): DayMarkEntity =
    DayMarkEntity(
        date = first.toString(),
        colorArgb = second
    )
