package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TicketPriority(val titleAr: String, val titleEn: String) {
    LOW("عادية", "Low"),
    MEDIUM("متوسطة", "Medium"),
    HIGH("عاجلة", "Urgent")
}

@Entity(tableName = "support_tickets")
data class SupportTicket(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ticketNumber: String,
    val subject: String,
    val category: String, // Commissions, Orders, Price Sync, App Technical
    val priority: String = TicketPriority.MEDIUM.name,
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED
    val message: String,
    val adminReply: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
