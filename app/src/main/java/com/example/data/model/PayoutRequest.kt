package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class PayoutStatus(val titleAr: String, val titleEn: String) {
    PENDING("قيد المراجعة", "Pending Review"),
    PROCESSING("جاري التحويل", "Processing"),
    COMPLETED("تم التحويل بنجاح", "Completed"),
    REJECTED("مرفوض", "Rejected")
}

@Entity(tableName = "payout_requests")
data class PayoutRequest(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val payoutCode: String,
    val amount: Double,
    val payoutMethod: String, // Bank, InstaPay, Vodafone Cash, STC Pay
    val recipientInfo: String, // IBAN, Wallet Number, InstaPay handle
    val status: String = PayoutStatus.PENDING.name,
    val transactionRef: String = "",
    val requestedAt: Long = System.currentTimeMillis(),
    val completedAt: Long = 0L
)
