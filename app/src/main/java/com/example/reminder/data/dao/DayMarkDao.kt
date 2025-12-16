package com.example.reminder.data.dao

import androidx.room.*
import com.example.reminder.data.entity.DayMarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DayMarkDao {

    @Query("SELECT * FROM day_marks")
    fun observeAll(): Flow<List<DayMarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(mark: DayMarkEntity)

    @Query("DELETE FROM day_marks WHERE date = :date")
    suspend fun delete(date: String)
}
