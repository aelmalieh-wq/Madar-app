package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val code: String, val titleAr: String, val titleEn: String) {
    NEW("NEW", "جديد", "New"),
    PREPARING("PREPARING", "قيد التجهيز", "Preparing"),
    SHIPPED("SHIPPED", "تم الشحن", "Shipped"),
    DELIVERED("DELIVERED", "تم التسليم (معتمد)", "Delivered"),
    RETURNED("RETURNED", "مرتجع", "Returned"),
    CANCELLED("CANCELLED", "ملغي", "Cancelled")
}

enum class PaymentType(val code: String, val titleAr: String, val titleEn: String) {
    COD("COD", "الدفع عند الاستلام", "Cash on Delivery"),
    ONLINE_CARD("CARD", "بطاقة بنكية (فيزا / مدى)", "Credit / Mada Card"),
    INSTAPAY("INSTAPAY", "انستاباي (InstaPay)", "InstaPay"),
    STC_PAY("STC_PAY", "STC Pay", "STC Pay"),
    VODAFONE_CASH("VODAFONE", "فودافون كاش", "Vodafone Cash")
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerCity: String,
    val customerAddress: String,
    val productId: Long,
    val productSku: String,
    val productTitleAr: String,
    val productTitleEn: String,
    val quantity: Int = 1,
    val unitSellingPrice: Double,
    val unitBasePrice: Double,
    val commissionPerItem: Double,
    val totalOrderAmount: Double,
    val totalCommission: Double,
    val paymentType: String = PaymentType.COD.code,
    val status: String = OrderStatus.NEW.code,
    val trackingNumber: String = "",
    val customerNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
