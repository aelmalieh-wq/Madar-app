package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.AppSettings
import com.example.data.model.Product
import com.example.ui.components.AppFooter
import com.example.ui.components.CreateOrderDialog
import com.example.ui.components.EditProductPriceDialog
import com.example.ui.components.ProfitCalculatorDialog
import com.example.ui.components.ShareAffiliateDialog
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold
import com.example.ui.theme.MadarNavy

@Composable
fun ProductsScreen(
    products: List<Product>,
    settings: AppSettings,
    searchQuery: String,
    selectedCategory: String?,
    isPriceSyncing: Boolean,
    onSearchChange: (String) -> Unit,
    onCategoryChange: (String?) -> Unit,
    onSyncPrices: () -> Unit,
    onUpdatePrice: (productId: Long, newPrice: Double) -> Unit,
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic = settings.language == "ar"
    val currency = if (settings.currency == "EGP" || settings.currency == "SAR") {
        if (isArabic) "ج.م" else "EGP"
    } else {
        settings.getCurrencySymbol()
    }

    var selectedPlatform by remember { mutableStateOf<String?>(null) }
    var orderDialogProduct by remember { mutableStateOf<Product?>(null) }
    var calcDialogProduct by remember { mutableStateOf<Product?>(null) }
    var shareDialogProduct by remember { mutableStateOf<Product?>(null) }
    var editPriceDialogProduct by remember { mutableStateOf<Product?>(null) }

    // Filter products
    val filteredProducts = products.filter { prod ->
        val matchesSearch = searchQuery.isBlank() ||
                prod.titleAr.contains(searchQuery, ignoreCase = true) ||
                prod.titleEn.contains(searchQuery, ignoreCase = true) ||
                prod.sku.contains(searchQuery, ignoreCase = true) ||
                prod.categoryAr.contains(searchQuery, ignoreCase = true) ||
                prod.platform.contains(searchQuery, ignoreCase = true)

        val matchesCat = selectedCategory == null ||
                prod.categoryAr == selectedCategory ||
                prod.categoryEn == selectedCategory

        val matchesPlatform = selectedPlatform == null ||
                prod.platform.contains(selectedPlatform.orEmpty(), ignoreCase = true) ||
                prod.platformCode == selectedPlatform

        matchesSearch && matchesCat && matchesPlatform
    }

    val platforms = listOf(
        null to if (isArabic) "جميع المنصات" else "All Platforms",
        "أمازون مصر" to if (isArabic) "أمازون مصر 🛒" else "Amazon Egypt",
        "نون مصر" to if (isArabic) "نون مصر 🟡" else "Noon Egypt",
        "جوميا مصر" to if (isArabic) "جوميا مصر 🟠" else "Jumia Egypt"
    )

    val categories = listOf(
        null to if (isArabic) "كل الأقسام" else "All Categories",
        "موبايلات وإلكترونيات" to if (isArabic) "موبايلات" else "Phones",
        "الصوتيات والإلكترونيات" to if (isArabic) "صوتيات" else "Audio",
        "أجهزة المطبخ والمنزل" to if (isArabic) "أجهزة منزلية" else "Home Appliances",
        "الشاشات والتلفزيونات" to if (isArabic) "شاشات" else "Smart TVs",
        "الساعات الذكية" to if (isArabic) "ساعات ذكية" else "Smartwatches",
        "الأزياء والأحذية" to if (isArabic) "أزياء" else "Fashion",
        "ملحقات الكمبيوتر" to if (isArabic) "ملحقات كمبيوتر" else "PC Accessories"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Madar Web Store banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MadarNavy,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MadarEmeraldPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Language,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "موقع مدار الرسمي" else "Madar Official Website",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "madar-studio-2.ai.studio",
                            color = MadarGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://madar-studio-2.ai.studio/"))
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.OpenInBrowser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = if (isArabic) "زيارة الموقع" else "Visit Site", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text(if (isArabic) "ابحث في منتجات أمازون، نون، جوميا، أو كود SKU..." else "Search Amazon, Noon, Jumia or SKU...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("products_search_bar")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Platform Filter Chips: All, Amazon Egypt, Noon Egypt, Jumia Egypt
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(platforms) { (platKey, label) ->
                val isSelected = (selectedPlatform == null && platKey == null) || (selectedPlatform == platKey)
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedPlatform = platKey },
                    label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = when (platKey) {
                            "أمازون مصر" -> Color(0xFF232F3E)
                            "نون مصر" -> Color(0xFFF0D500)
                            "جوميا مصر" -> Color(0xFFF68B1E)
                            else -> MadarEmeraldPrimary
                        },
                        selectedLabelColor = if (platKey == "نون مصر") Color.Black else Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Categories Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(vertical = 2.dp)
        ) {
            items(categories) { (catKey, label) ->
                val isSelected = (selectedCategory == null && catKey == null) || (selectedCategory == catKey)
                FilterChip(
                    selected = isSelected,
                    onClick = { onCategoryChange(catKey) },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Live Store Sync Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isArabic) "${filteredProducts.size} منتجات بالجنيه المصري (EGP)" else "${filteredProducts.size} products in EGP",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline,
                fontWeight = FontWeight.Medium
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onSyncPrices() }
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                if (isPriceSyncing) {
                    CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                } else {
                    Icon(
                        Icons.Default.Sync,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MadarEmeraldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isArabic) "تحديث فوري من مدار" else "Live Madar Sync",
                    fontSize = 11.sp,
                    color = MadarEmeraldPrimary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                ProductCatalogCard(
                    product = product,
                    currency = currency,
                    isArabic = isArabic,
                    onCreateOrder = { orderDialogProduct = product },
                    onCalcProfit = { calcDialogProduct = product },
                    onShare = { shareDialogProduct = product },
                    onEditPrice = { editPriceDialogProduct = product }
                )
            }

            // App Footer with Legal and Ownership links
            item {
                AppFooter(isArabic = isArabic)
            }
        }
    }

    // Dialogs
    orderDialogProduct?.let { prod ->
        CreateOrderDialog(
            product = prod,
            currency = currency,
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
            currency = currency,
            isArabic = isArabic,
            onDismiss = { calcDialogProduct = null }
        )
    }

    shareDialogProduct?.let { prod ->
        ShareAffiliateDialog(
            product = prod,
            currency = currency,
            partnerCode = "MDR-EG-PARTNER",
            isArabic = isArabic,
            onDismiss = { shareDialogProduct = null }
        )
    }

    editPriceDialogProduct?.let { prod ->
        EditProductPriceDialog(
            product = prod,
            currency = currency,
            isArabic = isArabic,
            onDismiss = { editPriceDialogProduct = null },
            onSave = { newPrice ->
                onUpdatePrice(prod.id, newPrice)
            }
        )
    }
}

@Composable
fun ProductCatalogCard(
    product: Product,
    currency: String,
    isArabic: Boolean,
    onCreateOrder: () -> Unit,
    onCalcProfit: () -> Unit,
    onShare: () -> Unit,
    onEditPrice: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    val (platformBg, platformText, platformLabel) = when (product.platformCode) {
        "AMAZON_EG" -> Triple(Color(0xFF232F3E), Color(0xFFFF9900), "أمازون مصر Amazon EG")
        "NOON_EG" -> Triple(Color(0xFFFEEE00), Color(0xFF000000), "نون مصر Noon EG")
        "JUMIA_EG" -> Triple(Color(0xFFF68B1E), Color(0xFFFFFFFF), "جوميا مصر Jumia EG")
        else -> Triple(MadarNavy, Color.White, product.platform)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("product_card_${product.sku}")
    ) {
        Column {
            // Platform Source & SKU Tag Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = platformBg,
                    contentColor = platformText
                ) {
                    Text(
                        text = platformLabel,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = MadarGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${product.rating} (${product.salesCount})",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = product.sku,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Product Image
                Box(
                    modifier = Modifier
                        .size(105.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Info
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isArabic) product.titleAr else product.titleEn,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isArabic) product.descriptionAr else product.descriptionEn,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        maxLines = 2,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Stock badge
                    Text(
                        text = if (isArabic) "المخزون المتوفر في مصر: ${product.stockQuantity} قطعة" else "Stock in Egypt: ${product.stockQuantity} pcs",
                        fontSize = 10.sp,
                        color = if (product.stockQuantity > 20) MadarEmeraldPrimary else Color(0xFFE65100),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Customer Price & Deal Banner
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) "السعر الرسمي:" else "Official Price:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MadarEmeraldPrimary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = if (isArabic) "🔥 عرض خاص" else "Special Deal",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MadarEmeraldPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "${product.affiliatePrice.toInt()} $currency",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${(product.affiliatePrice * 1.15).toInt()} $currency",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.outline,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }

                    // Platform Tag Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = when (product.platformCode) {
                            "AMAZON_EG" -> Color(0xFF232F3E)
                            "NOON_EG" -> Color(0xFFFEEE00)
                            "JUMIA_EG" -> Color(0xFFF68B1E)
                            else -> MadarEmeraldPrimary
                        }
                    ) {
                        Text(
                            text = product.platform,
                            color = if (product.platformCode == "NOON_EG") Color.Black else Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // Customer Action Buttons: Direct Store Purchase & Sharing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Primary: Buy on Amazon / Noon / Jumia
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.affiliateUrl))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when (product.platformCode) {
                            "AMAZON_EG" -> Color(0xFFFF9900)
                            "NOON_EG" -> Color(0xFFE5C300)
                            "JUMIA_EG" -> Color(0xFFF68B1E)
                            else -> MadarEmeraldPrimary
                        }
                    ),
                    modifier = Modifier
                        .weight(1.8f)
                        .height(42.dp)
                        .testTag("product_buy_btn_${product.sku}")
                ) {
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (product.platformCode == "NOON_EG") Color.Black else Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) "شراء من ${product.platform} ↗" else "Buy on ${product.platform} ↗",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (product.platformCode == "NOON_EG") Color.Black else Color.White
                    )
                }

                // Visit Madar Web Page
                OutlinedButton(
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.madarUrl))
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(42.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Language, contentDescription = "Madar Web", modifier = Modifier.size(16.dp))
                }

                // Share Deal
                OutlinedButton(
                    onClick = onShare,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(42.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
