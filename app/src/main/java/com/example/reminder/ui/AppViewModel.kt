package com.example.reminder.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminder.data.DayEvent
import com.example.reminder.data.EventRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class AppViewModel(
    private val repo: EventRepository
) : ViewModel() {

    // STATE
    val events: StateFlow<List<DayEvent>> =
        repo.events.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyList()
        )

    val marks =
        repo.dayMarks.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            emptyMap()
        )

    // EVENT

    fun addEvent(
        date: LocalDate,
        timeMinutes: Int,
        title: String,
        colorArgb: Int
    ) {
        viewModelScope.launch {
            repo.addEvent(
                DayEvent(
                    id = 0,
                    date = date,
                    timeMinutes = timeMinutes,
                    title = title.trim(),
                    colorArgb = colorArgb,
                    done = false
                )
            )
        }
    }

    fun updateEvent(e: DayEvent) {
        viewModelScope.launch {
            repo.updateEvent(e)
        }
    }

    fun deleteEvent(id: Long) {
        val e = events.value.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repo.deleteEvent(e)
        }
    }

    fun toggleDone(id: Long) {
        val e = events.value.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repo.updateEvent(e.copy(done = !e.done))
        }
    }

    // DAY MARK

    fun setDayMark(date: LocalDate, colorArgb: Int?) {
        viewModelScope.launch {
            repo.setDayMark(date, colorArgb)
        }
    }
}
