package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType {
    ORDER_STATUS,
    PRICE_SYNC,
    COMMISSION_CREDITED,
    PAYOUT_UPDATE,
    SECURITY_ALERT,
    SYSTEM
}

@Entity(tableName = "notifications")
data class NotificationItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleAr: String,
    val titleEn: String,
    val bodyAr: String,
    val bodyEn: String,
    val type: String = NotificationType.ORDER_STATUS.name,
    val referenceId: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
