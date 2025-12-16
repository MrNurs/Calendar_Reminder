package com.example.reminder.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.reminder.data.entity.EventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun observeAll(): Flow<List<EventEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insert(e: EventEntity): Long

    @Update
    suspend fun update(e: EventEntity)

    @Delete
    suspend fun delete(e: EventEntity)
}