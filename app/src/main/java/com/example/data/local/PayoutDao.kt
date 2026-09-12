package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PayoutRequest
import kotlinx.coroutines.flow.Flow

@Dao
interface PayoutDao {
    @Query("SELECT * FROM payout_requests ORDER BY requestedAt DESC")
    fun getAllPayouts(): Flow<List<PayoutRequest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayout(request: PayoutRequest): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPayouts(requests: List<PayoutRequest>)

    @Update
    suspend fun updatePayout(request: PayoutRequest)

    @Query("UPDATE payout_requests SET status = :status, transactionRef = :ref, completedAt = :completedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, ref: String, completedAt: Long)
}
