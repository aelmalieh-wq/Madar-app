package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val titleAr: String,
    val titleEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    val categoryAr: String,
    val categoryEn: String,
    val originalPrice: Double,       // Base store price from Madar
    val affiliatePrice: Double,      // Affiliate's selling price
    val commissionAmount: Double,    // Profit = affiliatePrice - originalPrice
    val commissionRatePercent: Double,
    val stockQuantity: Int,
    val platform: String = "أمازون مصر", // أمازون مصر / نون مصر / جوميا مصر
    val platformCode: String = "AMAZON_EG", // AMAZON_EG / NOON_EG / JUMIA_EG
    val madarUrl: String = "https://madar-studio-2.ai.studio/",
    val affiliateUrl: String = "https://www.amazon.eg/",
    val sourceUrl: String = "https://madar-studio-2.ai.studio/",
    val imageUrl: String = "",
    val rating: Float = 4.8f,
    val salesCount: Int = 0,
    val isTrending: Boolean = false,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)
