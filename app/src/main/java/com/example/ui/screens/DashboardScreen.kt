package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.model.AppSettings
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.ui.components.CreateOrderDialog
import com.example.ui.components.MainNavTab
import com.example.ui.components.ProfitCalculatorDialog
import com.example.ui.components.ShareAffiliateDialog
import com.example.ui.theme.MadarEmeraldDark
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold
import com.example.ui.theme.MadarNavy
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusShipped
import com.example.ui.viewmodel.FinancialOverview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    overview: FinancialOverview,
    products: List<Product>,
    recentOrders: List<Order>,
    settings: AppSettings,
    isPriceSyncing: Boolean,
    onNavigateTab: (MainNavTab) -> Unit,
    onSyncPrices: () -> Unit,
    onTogglePrivacy: () -> Unit,
    onCreateOrder: (
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        product: Product,
        quantity: Int,
        sellingPrice: Double,
        paymentType: String,
        notes: String
    ) -> Unit,
    onAdvanceOrderStatus: (orderId: Long, newStatus: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic = settings.language == "ar"
    val currencyLabel = if (settings.currency == "EGP" || settings.currency == "SAR") {
        if (isArabic) "ج.م" else "EGP"
    } else {
        settings.getCurrencySymbol()
    }
    val isHidden = settings.isBalanceHidden

    var orderDialogProduct by remember { mutableStateOf<Product?>(null) }
    var calcDialogProduct by remember { mutableStateOf<Product?>(null) }
    var shareDialogProduct by remember { mutableStateOf<Product?>(null) }

    fun formatMoney(amount: Double): String {
        val rounded = if (amount % 1.0 == 0.0) amount.toLong().toString() else "${kotlin.math.round(amount * 10) / 10}"
        return if (isHidden) "••••" else "$rounded $currencyLabel"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Customer Welcome & Brand Hero Banner (No profit or balance visible to customer)
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = MadarNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_hero_banner")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MadarNavy, Color(0xFF13283E), Color(0xFF0F1E2E))
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.madar_logo),
                                contentDescription = "Madar Logo",
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "MADAR | متجر مدار",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MadarGold.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = if (isArabic) "🇪🇬 مصر" else "🇪🇬 Egypt",
                                            color = MadarGold,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(3.dp))
                                Text(
                                    text = if (isArabic) "أفضل العروض المختارة من أمازون، نون، وجوميا مصر" else "Hand-picked deals from Amazon, Noon & Jumia Egypt",
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Customer Trust Badges Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Badge 1: 100% Authentic
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.09f),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🛡️", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isArabic) "أصلي 100%" else "Authentic",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Badge 2: Official Stores
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.09f),
                                modifier = Modifier.weight(1.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "🚀", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isArabic) "شحن مباشر وسريع" else "Direct Fast Shipping",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            // Badge 3: EGP Currency
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color.White.copy(alpha = 0.09f),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "💵", fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = if (isArabic) "بالجنيه المصري" else "In Egyptian EGP",
                                        color = MadarGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Live Madar Store Web Sync Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(MadarEmeraldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "موقع مدار (madar-studio-2.ai.studio)" else "Madar Store (madar-studio-2.ai.studio)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = if (isArabic) "منتجات أمازون مصر، جوميا مصر، ونون مصر بالجنيه (EGP)" else "Amazon EG, Jumia EG & Noon EG in EGP",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://madar-studio-2.ai.studio/"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MadarNavy),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isArabic) "الموقع" else "Website", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "أسعار وعروض المتاجر متزامنة ومحدثة تلقائياً" else "Store deals and prices auto-synced",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onSyncPrices() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            if (isPriceSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 1.5.dp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(14.dp), tint = MadarEmeraldPrimary)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isArabic) "تحديث الأسعار الآن" else "Sync Prices",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MadarEmeraldPrimary
                            )
                        }
                    }
                }
            }
        }

        // Shop by Platform Row
        item {
            Text(
                text = if (isArabic) "تصفح حسب المنصة المفضلة" else "Shop by Platform",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionItem(
                    icon = Icons.Default.AddShoppingCart,
                    title = if (isArabic) "أمازون مصر" else "Amazon EG",
                    color = Color(0xFFFF9900),
                    onClick = { onNavigateTab(MainNavTab.PRODUCTS) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    icon = Icons.Default.AddShoppingCart,
                    title = if (isArabic) "نون مصر" else "Noon EG",
                    color = Color(0xFFC7A800),
                    onClick = { onNavigateTab(MainNavTab.PRODUCTS) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    icon = Icons.Default.AddShoppingCart,
                    title = if (isArabic) "جوميا مصر" else "Jumia EG",
                    color = Color(0xFFF68B1E),
                    onClick = { onNavigateTab(MainNavTab.PRODUCTS) },
                    modifier = Modifier.weight(1f)
                )

                QuickActionItem(
                    icon = Icons.Default.Language,
                    title = if (isArabic) "موقع مدار" else "Madar Web",
                    color = MadarEmeraldPrimary,
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://madar-studio-2.ai.studio/"))
                        context.startActivity(intent)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Trending Deals from Madar Store
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) "أقوى عروض اليوم في مصر 🔥" else "Today's Featured Deals 🔥",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isArabic) "عرض كل العروض" else "View All",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.clickable { onNavigateTab(MainNavTab.PRODUCTS) }
                )
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(products.take(6)) { prod ->
                    TrendingProductCard(
                        product = prod,
                        currency = currencyLabel,
                        isArabic = isArabic,
                        onShareClick = { shareDialogProduct = prod }
                    )
                }
            }
        }

        // Guarantees & Why Shop via Madar
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isArabic) "لماذا تشتري عبر مدار؟ 🛡️" else "Why Shop Through Madar? 🛡️",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "🛒", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "شراء مباشر من المتاجر الرسمية" else "Direct Purchase from Official Stores",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isArabic) "يتم توجيهك فوراً لرابط المنتج الرسمي في أمازون أو نون أو جوميا مصر لتتم العملية بأعلى أمان." else "Direct secure redirect to official store pages.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "🛡️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "ضمان الوكيل وخدمات ما بعد البيع" else "Full Official Warranty & Returns",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isArabic) "تتمتع بكامل حقوق الاسترجاع وسياسات الدفع عند الاستلام المعتمدة من المتاجر الكبرى." else "Full warranty and return policies guaranteed.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Row(verticalAlignment = Alignment.Top) {
                        Text(text = "⚡", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (isArabic) "أفضل أسعار محدثة بالجنيه (EGP)" else "Best Real-time EGP Prices",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isArabic) "فريق مدار يختار لك يومياً أفضل التخفيضات والكوبونات الفعالة لتوفير أكبر." else "Best coupons and discounts updated daily.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        }
    }

    // Active Dialogs
    orderDialogProduct?.let { prod ->
        CreateOrderDialog(
            product = prod,
            currency = currencyLabel,
            isArabic = isArabic,
            onDismiss = { orderDialogProduct = null },
            onSubmit = { name, phone, city, addr, qty, price, payment, notes ->
                onCreateOrder(name, phone, city, addr, prod, qty, price, payment, notes)
                orderDialogProduct = null
            }
        )
    }

    calcDialogProduct?.let { prod ->
        ProfitCalculatorDialog(
            product = prod,
            currency = currencyLabel,
            isArabic = isArabic,
            onDismiss = { calcDialogProduct = null }
        )
    }

    shareDialogProduct?.let { prod ->
        ShareAffiliateDialog(
            product = prod,
            currency = currencyLabel,
            partnerCode = "MDR-PARTNER-77",
            isArabic = isArabic,
            onDismiss = { shareDialogProduct = null }
        )
    }
}

@Composable
fun QuickActionItem(
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = color.copy(alpha = 0.1f),
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                maxLines = 1
            )
        }
    }
}

@Composable
fun TrendingProductCard(
    product: Product,
    currency: String,
    isArabic: Boolean,
    onShareClick: () -> Unit
) {
    val context = LocalContext.current
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.width(220.dp)
    ) {
        Column {
            // Product image preview or banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                // Platform badge
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = when (product.platformCode) {
                        "AMAZON_EG" -> Color(0xFF232F3E)
                        "NOON_EG" -> Color(0xFFFEEE00)
                        "JUMIA_EG" -> Color(0xFFF68B1E)
                        else -> MadarEmeraldPrimary
                    },
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.platform,
                        color = if (product.platformCode == "NOON_EG") Color.Black else Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Trending Chip
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MadarEmeraldPrimary,
                    modifier = Modifier
                        .padding(6.dp)
                        .align(Alignment.TopEnd)
                ) {
                    Text(
                        text = if (isArabic) "🔥 عرض مميز" else "🔥 Top Deal",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = if (isArabic) product.titleAr else product.titleEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(6.dp))
                Column {
                    Text(
                        text = if (isArabic) "السعر الرسمي:" else "Official Price:",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "${product.affiliatePrice.toInt()} $currency",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "${(product.affiliatePrice * 1.15).toInt()} $currency",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline,
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.affiliateUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (product.platformCode) {
                                "AMAZON_EG" -> Color(0xFFFF9900)
                                "NOON_EG" -> Color(0xFFE5C300)
                                "JUMIA_EG" -> Color(0xFFF68B1E)
                                else -> MadarEmeraldPrimary
                            }
                        )
                    ) {
                        Text(
                            text = if (isArabic) "شراء من المتجر ↗" else "Buy Now ↗",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (product.platformCode == "NOON_EG") Color.Black else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onShareClick,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardOrderRow(
    order: Order,
    currency: String,
    isArabic: Boolean,
    onAdvanceStatus: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.customerName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Status Chip
                val (statusColor, statusTitle) = when (order.status) {
                    OrderStatus.DELIVERED.code -> StatusDelivered to if (isArabic) "تم التسليم (معتمد)" else "Delivered"
                    OrderStatus.SHIPPED.code -> StatusShipped to if (isArabic) "تم الشحن" else "Shipped"
                    OrderStatus.PREPARING.code -> StatusPreparing to if (isArabic) "قيد التجهيز" else "Preparing"
                    else -> Color(0xFF1976D2) to if (isArabic) "جديد" else "New"
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusTitle,
                        color = statusColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic) order.productTitleAr else order.productTitleEn,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "${if (isArabic) "العمولة:" else "Profit:"} +${order.totalCommission} $currency",
                    fontWeight = FontWeight.Bold,
                    color = MadarEmeraldPrimary,
                    fontSize = 12.sp
                )
            }

            // Quick advance button if not delivered
            if (order.status != OrderStatus.DELIVERED.code && order.status != OrderStatus.CANCELLED.code) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    val nextAction = when (order.status) {
                        OrderStatus.NEW.code -> OrderStatus.PREPARING.code to if (isArabic) "نقل إلى التجهيز بالمستودع" else "Move to Preparing"
                        OrderStatus.PREPARING.code -> OrderStatus.SHIPPED.code to if (isArabic) "شحن مع المندوب" else "Ship with Courier"
                        OrderStatus.SHIPPED.code -> OrderStatus.DELIVERED.code to if (isArabic) "تأكيد التسليم واعتماد العمولة ✓" else "Deliver & Credit Profit ✓"
                        else -> null
                    }
                    nextAction?.let { (nextStatus, label) ->
                        OutlinedButton(
                            onClick = { onAdvanceStatus(nextStatus) },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
