package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.ui.theme.MadarEmeraldDark
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold
import com.example.ui.theme.MadarNavy
import com.example.ui.viewmodel.FinancialOverview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    overview: FinancialOverview,
    orders: List<Order>,
    products: List<Product>,
    settings: AppSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic = settings.language == "ar"
    val currency = if (settings.currency == "EGP" || settings.currency == "SAR") {
        if (isArabic) "ج.م" else "EGP"
    } else {
        settings.getCurrencySymbol()
    }

    var selectedStore by remember { mutableStateOf("ALL") }

    val storeFilters = listOf(
        "ALL" to if (isArabic) "جميع المتاجر" else "All Stores",
        "AMAZON" to if (isArabic) "أمازون مصر 🛒" else "Amazon EG",
        "NOON" to if (isArabic) "نون مصر 🟡" else "Noon EG",
        "JUMIA" to if (isArabic) "جوميا مصر 🟠" else "Jumia EG"
    )

    val savingsPoints = listOf(
        Pair(if (isArabic) "السبت" else "Sat", 18f),
        Pair(if (isArabic) "الأحد" else "Sun", 25f),
        Pair(if (isArabic) "الاثنين" else "Mon", 22f),
        Pair(if (isArabic) "الثلاثاء" else "Tue", 35f),
        Pair(if (isArabic) "الأربعاء" else "Wed", 40f),
        Pair(if (isArabic) "الخميس" else "Thu", 45f),
        Pair(if (isArabic) "الجمعة" else "Fri", 30f)
    )

    fun shareDealsGuide() {
        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val shareText = if (isArabic) {
            """
            🔥 دليل أقوى التخفيضات وعروض الشراء في مصر ($dateStr)
            عبر منصة MADAR | مدار:
            
            🛒 عروض أمازون مصر (Amazon Egypt): شحن سريع وضمان الوكيل.
            🟡 عروض نون مصر (Noon Egypt): نون إكسبرس وأسعار تنافسية.
            🟠 عروض جوميا مصر (Jumia Egypt): جوميا مول لمنتجات أصلية 100%.
            
            🌐 تصفح أقوى العروض بالجنيه المصري:
            https://madar-studio-2.ai.studio/
            """.trimIndent()
        } else {
            """
            🔥 Egypt Deals & Savings Guide ($dateStr)
            via MADAR | مدار:
            
            🛒 Amazon Egypt Deals: Fast delivery & official warranty.
            🟡 Noon Egypt Deals: Noon Express & competitive prices.
            🟠 Jumia Egypt Deals: Jumia Mall with 100% genuine products.
            
            🌐 Browse featured deals in EGP:
            https://madar-studio-2.ai.studio/
            """.trimIndent()
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }
        val chooserIntent = Intent.createChooser(sendIntent, if (isArabic) "مشاركة دليل التوفير" else "Share Deals Guide")
        context.startActivity(chooserIntent)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Share Guide Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) "دليل التوفير والمتاجر المصرية 🛡️" else "Egypt Savings & Stores Guide 🛡️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isArabic) "دليلك للتسوق الآمن بأفضل سعر من جوميا، نون، وأمازون مصر" else "Your guide to best deals from Amazon, Noon & Jumia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Button(
                    onClick = { shareDealsGuide() },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("share_guide_button")
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isArabic) "مشاركة" else "Share", fontSize = 12.sp)
                }
            }
        }

        // Stores Overview Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomerValueCard(
                    title = if (isArabic) "خصومات تصل إلى" else "Discounts Up to",
                    value = "45%",
                    subtitle = if (isArabic) "على أقوى المنتجات" else "On top products",
                    color = MadarEmeraldPrimary,
                    modifier = Modifier.weight(1f)
                )
                CustomerValueCard(
                    title = if (isArabic) "العملة المعتمدة" else "Store Currency",
                    value = "EGP ج.م",
                    subtitle = if (isArabic) "بالجنيه المصري" else "Egyptian Pound",
                    color = MadarGold,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CustomerValueCard(
                    title = if (isArabic) "التوصيل بالمحافظات" else "Coverage",
                    value = "27 محافظة",
                    subtitle = if (isArabic) "شحن لجميع أنحاء مصر" else "All Egypt Governorates",
                    color = Color(0xFF1976D2),
                    modifier = Modifier.weight(1f)
                )
                CustomerValueCard(
                    title = if (isArabic) "ضمان المنتجات" else "Warranty",
                    value = "100% أصلي",
                    subtitle = if (isArabic) "ضمان الوكيل وسياسة إرجاع" else "Full Official Warranty",
                    color = Color(0xFF7B1FA2),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Store Filter Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                storeFilters.forEach { (key, label) ->
                    FilterChip(
                        selected = selectedStore == key,
                        onClick = { selectedStore = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MadarEmeraldPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }
        }

        // Visual Savings Index Chart
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("savings_chart_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "مؤشر نسب الخصم والتوفير هذا الأسبوع (%)" else "Weekly Discount & Savings Index (%)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) "🔥 عروض نشطة" else "🔥 Active Deals",
                            fontSize = 11.sp,
                            color = MadarEmeraldPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Compose Canvas Chart
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val maxPoint = 50f
                            val barWidth = size.width / (savingsPoints.size * 2.2f)
                            val spacing = size.width / savingsPoints.size

                            val gridLines = 4
                            for (i in 0..gridLines) {
                                val y = size.height * (i.toFloat() / gridLines)
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.25f),
                                    start = Offset(0f, y),
                                    end = Offset(size.width, y),
                                    strokeWidth = 1f
                                )
                            }

                            savingsPoints.forEachIndexed { index, point ->
                                val x = index * spacing + spacing / 4f
                                val barHeight = (point.second / maxPoint) * (size.height * 0.85f)
                                val y = size.height - barHeight

                                drawRoundRect(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(MadarEmeraldPrimary, Color(0xFF48A999))
                                    ),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        savingsPoints.forEach { point ->
                            Text(
                                text = point.first,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }

        // Platform Comparison & Shopping Guide
        item {
            Text(
                text = if (isArabic) "دليل المنصات الشريكة في مصر" else "Partner Platforms in Egypt",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            PlatformGuideCard(
                name = if (isArabic) "أمازون مصر (Amazon Egypt)" else "Amazon Egypt",
                badge = "Amazon EG 🛒",
                color = Color(0xFF232F3E),
                benefits = if (isArabic) listOf(
                    "توصيل سريع مجاني لمشتركي أمازون برايم",
                    "الدفع عند الاستلام متاح لجميع المنتجات",
                    "إرجاع سهل ومجاني خلال 14-30 يوماً"
                ) else listOf(
                    "Fast shipping with Amazon Prime",
                    "Cash on Delivery available",
                    "Easy free returns within 14-30 days"
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            PlatformGuideCard(
                name = if (isArabic) "نون مصر (Noon Egypt)" else "Noon Egypt",
                badge = "Noon EG 🟡",
                color = Color(0xFFC7A800),
                benefits = if (isArabic) listOf(
                    "شحن سريع مع نون إكسبرس (Noon Express)",
                    "عروض تقسيط وبطاقات بنكية متنوعة",
                    "خدمة عملاء على مدار الساعة"
                ) else listOf(
                    "Fast shipping via Noon Express",
                    "Installment and bank card deals",
                    "24/7 customer support"
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            PlatformGuideCard(
                name = if (isArabic) "جوميا مصر (Jumia Egypt)" else "Jumia Egypt",
                badge = "Jumia EG 🟠",
                color = Color(0xFFF68B1E),
                benefits = if (isArabic) listOf(
                    "منتجات أصلية 100% معتمدة من جوميا مول (Jumia Mall)",
                    "خصومات إضافية عند الدفع عبر JumiaPay",
                    "نقاط استلام سريعة في مختلف المدن والمراكز"
                ) else listOf(
                    "100% genuine products from Jumia Mall",
                    "Extra discounts with JumiaPay",
                    "Convenient pickup stations across Egypt"
                )
            )
        }

        // Featured Deals List
        item {
            Text(
                text = if (isArabic) "أقوى الصفقات المتاحة للشراء الآن" else "Top Available Deals",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    products.take(5).forEachIndexed { idx, prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(text = "${idx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) prod.titleAr else prod.titleEn,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "${prod.platform} • تقييم ${prod.rating} ★",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${prod.affiliatePrice.toInt()} $currency",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 14.sp
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = MadarEmeraldPrimary.copy(alpha = 0.12f),
                                    modifier = Modifier.clickable {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(prod.affiliateUrl))
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (isArabic) "شراء ↗" else "Buy ↗",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MadarEmeraldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomerValueCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun PlatformGuideCard(
    name: String,
    badge: String,
    color: Color,
    benefits: List<String>
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = color
                ) {
                    Text(
                        text = badge,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            benefits.forEach { b ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Text(text = "✓", color = MadarEmeraldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = b,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
