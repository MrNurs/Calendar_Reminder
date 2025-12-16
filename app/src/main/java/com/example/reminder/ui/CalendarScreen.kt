package com.example.reminder.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items

@Composable
fun CalendarScreen(
    vm: AppViewModel,
    onOpenDay: (LocalDate) -> Unit
) {
    val events by vm.events.collectAsState()
    val marks by vm.marks.collectAsState()

    var month by remember { mutableStateOf(YearMonth.now()) }
    val today = remember { LocalDate.now() }

    val monthDays = remember(month) { buildMonthGrid(month) }

    val eventsByDate = remember(events, month) {
        events.groupBy { it.date }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        TopRow(
            month = month,
            onPrev = { month = month.minusMonths(1) },
            onNext = { month = month.plusMonths(1) }
        )

        Spacer(Modifier.height(12.dp))

        WeekHeader()

        Spacer(Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(monthDays) { cell ->
                val isInMonth = cell != null && cell.month == month.month
                val date = cell
                val markColor = if (date != null) marks[date] else null
                val eventColors = if (date != null) (eventsByDate[date]?.map { it.colorArgb } ?: emptyList()) else emptyList()
                DayCell(
                    date = date,
                    isInMonth = isInMonth,
                    isToday = date == today,
                    markColorArgb = markColor,
                    eventColorArgbList = eventColors,
                    onClick = { if (date != null) onOpenDay(date) }
                )
            }
        }
        Tasks(
            month = month,
            events = events,
            onOpenDay = onOpenDay
        )
    }
}

@Composable
private fun TopRow(month: YearMonth, onPrev: () -> Unit, onNext: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        TextButton(onClick = onPrev) { Text("←") }
        Spacer(Modifier.weight(1f))
        Text(
            text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } +
                    " ${month.year}",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.weight(1f))
        TextButton(onClick = onNext) { Text("→") }
    }
}

@Composable
private fun WeekHeader() {
    val names = listOf(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY
    ).map { it.getDisplayName(TextStyle.SHORT, Locale.getDefault()) }

    Row(Modifier.fillMaxWidth()) {
        names.forEach { n ->
            Text(
                text = n,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    isInMonth: Boolean,
    isToday: Boolean,
    markColorArgb: Int?,
    eventColorArgbList: List<Int>,
    onClick: () -> Unit
) {
    val bg = when {
        markColorArgb != null -> Color(markColorArgb)
        else -> Color.Transparent
    }

    val alpha = if (isInMonth) 1f else 0.35f

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = date != null, onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = if (isToday) BorderStroke(1.dp, Color.Black) else null

    ) {
        Box(
            Modifier.fillMaxSize().background(bg.copy(alpha = 0.25f)).padding(6.dp)
        ) {
            Text(
                text = date?.dayOfMonth?.toString() ?: "",
                modifier = Modifier.align(Alignment.TopStart),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                style = MaterialTheme.typography.bodyMedium
            )
            Row(
                modifier = Modifier.align(Alignment.BottomStart),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                eventColorArgbList.distinct().take(3).forEach { c ->
                    Box(
                        Modifier.size(8.dp).background(Color(c), shape = MaterialTheme.shapes.small)
                    )
                }
            }
        }
    }
}
@Composable
private fun Tasks(month: YearMonth, events: List<com.example.reminder.data.DayEvent>, onOpenDay: (LocalDate) -> Unit){
    var selectedColor by remember { mutableStateOf<Int?>(null) }

    val monthEvents = remember(events, month) {
        val start = month.atDay(1)
        val end = month.atEndOfMonth()
        events
            .asSequence()
            .filter { it.date >= start && it.date <= end }
            .sortedWith(
                compareBy<com.example.reminder.data.DayEvent>(
                    { it.date },
                    { it.timeMinutes }
                )
            )
            .toList()
    }
    Spacer(Modifier.height(10.dp))
    if(monthEvents.isEmpty()){
        Text(text = "No task")
        return
    }
    Text(text = "This month's task's")
//-------------------------------------------
//    FILTER COLORS
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            Modifier
                .size(26.dp)
                .background(Color.White, shape = MaterialTheme.shapes.small)
                .border(1.dp, Color.Black, shape = MaterialTheme.shapes.small)
                .clickable { selectedColor = null }
        )

        monthEvents
            .map { it.colorArgb }
            .distinct()
            .forEach { c ->
                Box(
                    Modifier
                        .size(26.dp)
                        .background(Color(c), shape = MaterialTheme.shapes.small)
                        .clickable { selectedColor = c }
                )
            }
    }


    Spacer(Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
    ) {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(
                if (selectedColor == null)
                    monthEvents
                else
                    monthEvents.filter { it.colorArgb == selectedColor }
            ) { e ->

            TaskRow(e = e, onClick = { onOpenDay(e.date) })
            }
        }
    }

}
//        if (monthEvents.size > 8) {
//            Text(
//                text = "+",
//                style = MaterialTheme.typography.labelMedium,
//                color = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }


@Composable
private fun TaskRow(e: com.example.reminder.data.DayEvent, onClick: () -> Unit){
    val h = e.timeMinutes / 60
    val m = e.timeMinutes % 60
    val time = "%02d:%02d".format(h, m)
    val dateText = "%02d.%02d".format(e.date.dayOfMonth, e.date.monthValue)

    val alpha = if (e.done) 0.45f else 1f
    val dotColor = if (e.done) Color.Gray else Color(e.colorArgb)
//    val backColor = if (e.done) Color
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(250, 250, 250))
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(2.dp)
    )
 {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(10.dp)
                    .background(dotColor, shape = MaterialTheme.shapes.small)
            )
            Spacer(Modifier.width(10.dp))

            Text(
                text = "$dateText $time",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.width(90.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha)
            )

            Text(
                text = e.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha),
                maxLines = 1
            )
        }
    }

}
private fun buildMonthGrid(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val last = month.atEndOfMonth()

    val firstDow = first.dayOfWeek.value
    val leading = firstDow - 1

    val days = mutableListOf<LocalDate?>()
    repeat(leading) { days += null }
    var d = first
    while (!d.isAfter(last)) {
        days += d
        d = d.plusDays(1)
    }
    while (days.size % 7 != 0) days += null
    while (days.size < 42) days += null
    return days
}
