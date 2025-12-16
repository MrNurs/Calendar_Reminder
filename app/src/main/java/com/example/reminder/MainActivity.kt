package com.example.reminder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.room.Room
import com.example.reminder.data.AppDatabase
import com.example.reminder.data.EventRepository
import com.example.reminder.ui.AppNav
import com.example.reminder.ui.AppViewModel
import com.example.reminder.data.dao.EventDao

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "reminder.db"
        )
            .fallbackToDestructiveMigration()
            .build()

        val repo = EventRepository(
            eventDao = db.eventDao(),
            dayMarkDao = db.dayMarkDao()
        )

        val vm = AppViewModel(repo)

        setContent {
            MaterialTheme {
                Surface {
                    AppNav(vm)
                }
            }
        }
    }
}
