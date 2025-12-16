package com.dickel.zeiterfassung.data.local.dao

import androidx.room.*
import com.dickel.zeiterfassung.data.local.entity.Serviceschein
import kotlinx.coroutines.flow.Flow

@Dao
interface ServicescheinDao {
    @Query("SELECT * FROM serviceschein ORDER BY createdAt DESC")
    fun getAllServiceschein(): Flow<List<Serviceschein>>

    @Query("SELECT * FROM serviceschein WHERE isSent = 0 ORDER BY createdAt ASC")
    fun getUnsentServiceschein(): Flow<List<Serviceschein>>

    @Query("SELECT * FROM serviceschein WHERE id = :id")
    suspend fun getServicescheinById(id: Long): Serviceschein?

    @Insert
    suspend fun insert(serviceschein: Serviceschein): Long

    @Update
    suspend fun update(serviceschein: Serviceschein)

    @Delete
    suspend fun delete(serviceschein: Serviceschein)

    @Query("UPDATE serviceschein SET isSent = 1 WHERE id = :id")
    suspend fun markAsSent(id: Long)

    @Query("UPDATE serviceschein SET sendError = :error WHERE id = :id")
    suspend fun setSendError(id: Long, error: String)
}
