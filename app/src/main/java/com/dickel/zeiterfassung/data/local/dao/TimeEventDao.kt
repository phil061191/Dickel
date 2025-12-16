package com.dickel.zeiterfassung.data.local.dao

import androidx.room.*
import com.dickel.zeiterfassung.data.local.entity.TimeEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface TimeEventDao {
    @Query("SELECT * FROM time_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<TimeEvent>>

    @Query("SELECT * FROM time_events WHERE isSynced = 0 ORDER BY timestamp ASC")
    fun getUnsyncedEvents(): Flow<List<TimeEvent>>

    @Query("SELECT * FROM time_events ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastEvent(): TimeEvent?

    @Insert
    suspend fun insert(event: TimeEvent): Long

    @Update
    suspend fun update(event: TimeEvent)

    @Delete
    suspend fun delete(event: TimeEvent)

    @Query("UPDATE time_events SET isSynced = 1 WHERE id = :eventId")
    suspend fun markAsSynced(eventId: Long)

    @Query("UPDATE time_events SET syncError = :error WHERE id = :eventId")
    suspend fun setSyncError(eventId: Long, error: String)
}
