package com.example.reminder.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.reminder.data.DayEvent
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailsScreen(
    vm: AppViewModel,
    date: LocalDate,
    onBack: () -> Unit
) {
    val events: List<DayEvent> by vm.events.collectAsState(initial = emptyList())
    val marks: Map<LocalDate, Int> by vm.marks.collectAsState(initial = emptyMap())

    val dayEvents = remember(events, date) {
        events
            .filter { it.date == date }
            .sortedWith(compareBy<DayEvent>({ it.done }, { it.timeMinutes }))
    }

    var showAdd by remember { mutableStateOf(false) }
    var showMark by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<DayEvent?>(null) }

    val title = remember(date) {
        val fmt = DateTimeFormatter.ofPattern("EEEE, yyyy-MM-dd", Locale.getDefault())
        date.format(fmt)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { TextButton(onClick = onBack) { Text("←") } },
                actions = {
                    TextButton(onClick = { showMark = true }) { Text("Color") }
                    TextButton(onClick = { showAdd = true }) { Text("+ Event") }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            val currentMark: Int? = marks[date]

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Day color:", style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.width(10.dp))
                Box(
                    Modifier
                        .size(18.dp)
                        .background(
                            color = if (currentMark != null) Color(currentMark) else Color.Transparent,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(Modifier.width(10.dp))
                if (currentMark != null) {
                    TextButton(onClick = { vm.setDayMark(date, null) }) { Text("Clear") }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (dayEvents.isEmpty()) {
                Text("No events yet. Add one", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(items = dayEvents, key = { it.id }) { e ->
                        EventRow(
                            e = e,
                            onDone = { vm.toggleDone(e.id) },
                            onDelete = { vm.deleteEvent(e.id) },
                            onEdit = { editingEvent = e }
                        )
                    }
                }
            }
        }
    }
    if (showAdd) {
        AddEditEventDialog(
            title = "Add event",
            initialText = "",
            initialHour = "12",
            initialMinute = "00",
            initialColor = PALETTE[0],
            onDismiss = { showAdd = false },
            onSave = { timeMin, text, color ->
                vm.addEvent(date, timeMin, text, color)
                showAdd = false
            }
        )
    }
    if (editingEvent != null) {
        val e = editingEvent!!
        val hh = (e.timeMinutes / 60).toString().padStart(2, '0')
        val mm = (e.timeMinutes % 60).toString().padStart(2, '0')

        AddEditEventDialog(
            title = "Edit event",
            initialText = e.title,
            initialHour = hh,
            initialMinute = mm,
            initialColor = e.colorArgb,
            onDismiss = { editingEvent = null },
            onSave = { timeMin, text, color ->
                vm.updateEvent(
                    e.copy(
                        timeMinutes = timeMin,
                        title = text.trim(),
                        colorArgb = color
                    )
                )
                editingEvent = null
            }
        )
    }
    if (showMark) {
        DayMarkDialog(
            onDismiss = { showMark = false },
            onPick = { color ->
                vm.setDayMark(date, color)
                showMark = false
            }
        )
    }
}

@Composable
private fun EventRow(
    e: DayEvent,
    onDone: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val hh = e.timeMinutes / 60
    val mm = e.timeMinutes % 60
    val time = "%02d:%02d".format(hh, mm)

    val dotColor = if (e.done) Color.Gray else Color(e.colorArgb)
    val textAlpha = if (e.done) 0.45f else 1f

    Card {
        Row(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(12.dp).background(dotColor, shape = MaterialTheme.shapes.small)
            )
            Spacer(Modifier.width(10.dp))

            Column(
                Modifier
                    .weight(1f)
                    .clickable { onEdit() }
                    .alpha(textAlpha)
            ) {
                Text(time, style = MaterialTheme.typography.labelLarge)
                Text(e.title, style = MaterialTheme.typography.bodyLarge)
            }
            Column(){
                TextButton(onClick = onDone) {
                    Text(if (e.done) "Undo" else "Done")
                }
                TextButton(onClick = onDelete) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun AddEditEventDialog(
    title: String,
    initialText: String,
    initialHour: String,
    initialMinute: String,
    initialColor: Int,
    onDismiss: () -> Unit,
    onSave: (timeMinutes: Int, title: String, colorArgb: Int) -> Unit
) {
    var text by remember { mutableStateOf(initialText) }
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }
    var colorArgb by remember { mutableStateOf(initialColor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = hour,
                        onValueChange = { hour = it.filter(Char::isDigit).take(2) },
                        label = { Text("HH") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = minute,
                        onValueChange = { minute = it.filter(Char::isDigit).take(2) },
                        label = { Text("MM") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text("Color:")
                ColorPickerRow(selected = colorArgb, onPick = { colorArgb = it })
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val hh = hour.toIntOrNull() ?: 0
                val mm = minute.toIntOrNull() ?: 0
                val hh2 = min(23, max(0, hh))
                val mm2 = min(59, max(0, mm))
                val timeMin = hh2 * 60 + mm2

                val t = text.trim()
                if (t.isNotEmpty()) onSave(timeMin, t, colorArgb)
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun DayMarkDialog(
    onDismiss: () -> Unit,
    onPick: (colorArgb: Int) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pick day color") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Tap a color:")
                ColorPickerRow(selected = PALETTE[0], onPick = onPick)
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Close") } }
    )
}

private val PALETTE = listOf(
    0xFFEF5350.toInt(),
    0xFFAB47BC.toInt(),
    0xFF5C6BC0.toInt(),
    0xFF29B6F6.toInt(),
    0xFF66BB6A.toInt(),
    0xFFFFCA28.toInt(),
    0xFFFF7043.toInt()
)

@Composable
private fun ColorPickerRow(selected: Int, onPick: (Int) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PALETTE.forEach { c ->
            Box(
                Modifier
                    .size(26.dp)
                    .background(Color(c), shape = MaterialTheme.shapes.small)
                    .clickable { onPick(c) }
            )
        }
    }
}
