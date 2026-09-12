package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.SupportTicket
import kotlinx.coroutines.flow.Flow

@Dao
interface SupportDao {
    @Query("SELECT * FROM support_tickets ORDER BY createdAt DESC")
    fun getAllTickets(): Flow<List<SupportTicket>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: SupportTicket): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTickets(tickets: List<SupportTicket>)

    @Update
    suspend fun updateTicket(ticket: SupportTicket)
}
