package com.example.data.model

data class AppSettings(
    val language: String = "ar", // "ar" or "en"
    val currency: String = "EGP", // Primary: EGP (الجنيه المصري)
    val isSecurityPinEnabled: Boolean = false,
    val securityPin: String = "",
    val isBiometricEnabled: Boolean = false,
    val isBalanceHidden: Boolean = false, // Privacy mode
    val partnerTier: String = "GOLD", // SILVER, GOLD, PLATINUM
    val isAutoSyncEnabled: Boolean = true,
    val syncIntervalMinutes: Int = 15,
    val lastCloudSyncTime: Long = System.currentTimeMillis() - 1000 * 60 * 12,
    val partnerName: String = "شريك مدار - مصر",
    val partnerEmail: String = "affiliate@madar-studio-2.ai.studio",
    val partnerPhone: String = "+20 100 123 4567",
    val madarStoreUrl: String = "https://madar-studio-2.ai.studio/",
    val userRole: String = "مسوق معتمد (Certified Partner)"
) {
    fun getCurrencySymbol(): String {
        return when (currency) {
            "EGP" -> if (language == "ar") "ج.م" else "EGP"
            "SAR" -> if (language == "ar") "ر.س" else "SAR"
            "AED" -> if (language == "ar") "د.إ" else "AED"
            "USD" -> "$"
            else -> currency
        }
    }

    fun formatMoney(amount: Double): String {
        val symbol = getCurrencySymbol()
        val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else kotlin.math.round(amount * 10) / 10.0
        return if (isBalanceHidden) "••••" else "$rounded $symbol"
    }
}

fun formatCurrency(amount: Double, currency: String = "EGP", isArabic: Boolean = true): String {
    val symbol = when (currency) {
        "EGP" -> if (isArabic) "ج.م" else "EGP"
        "SAR" -> if (isArabic) "ر.س" else "SAR"
        "AED" -> if (isArabic) "د.إ" else "AED"
        "USD" -> "$"
        else -> currency
    }
    val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else kotlin.math.round(amount * 10) / 10.0
    return "$rounded $symbol"
}
