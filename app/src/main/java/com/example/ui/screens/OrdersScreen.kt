package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentType
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPreparing
import com.example.ui.theme.StatusReturned
import com.example.ui.theme.StatusShipped
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<Order>,
    settings: AppSettings,
    selectedStatus: String?,
    onStatusFilterChange: (String?) -> Unit,
    onAdvanceOrderStatus: (orderId: Long, newStatus: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = settings.language == "ar"
    val currency = if (settings.currency == "EGP" || settings.currency == "SAR") {
        if (isArabic) "ج.م" else "EGP"
    } else {
        settings.getCurrencySymbol()
    }

    var searchQuery by remember { mutableStateOf("") }

    val filteredOrders = orders.filter { ord ->
        val matchesStatus = selectedStatus == null || ord.status == selectedStatus
        val matchesSearch = searchQuery.isBlank() ||
                ord.orderNumber.contains(searchQuery, ignoreCase = true) ||
                ord.customerName.contains(searchQuery, ignoreCase = true) ||
                ord.customerPhone.contains(searchQuery, ignoreCase = true) ||
                ord.customerCity.contains(searchQuery, ignoreCase = true) ||
                ord.productTitleAr.contains(searchQuery, ignoreCase = true)

        matchesStatus && matchesSearch
    }

    val statusFilters = listOf(
        null to if (isArabic) "الكل (${orders.size})" else "All (${orders.size})",
        OrderStatus.NEW.code to if (isArabic) "جديد" else "New",
        OrderStatus.PREPARING.code to if (isArabic) "قيد التجهيز" else "Preparing",
        OrderStatus.SHIPPED.code to if (isArabic) "تم الشحن" else "Shipped",
        OrderStatus.DELIVERED.code to if (isArabic) "تم التسليم" else "Delivered",
        OrderStatus.RETURNED.code to if (isArabic) "مرتجع" else "Returned",
        OrderStatus.CANCELLED.code to if (isArabic) "ملغي" else "Cancelled"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text(if (isArabic) "بحث برقم الطلب، اسم العميل، المدينة..." else "Search order #, customer, city...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .testTag("orders_search_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(statusFilters) { (code, label) ->
                val isSelected = selectedStatus == code
                FilterChip(
                    selected = isSelected,
                    onClick = { onStatusFilterChange(code) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MadarEmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Orders List
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 90.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isArabic) "لا توجد طلبات تطابق معايير البحث" else "No matching orders found",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderManagementCard(
                        order = order,
                        currency = currency,
                        isArabic = isArabic,
                        onAdvanceStatus = { nextStatus ->
                            onAdvanceOrderStatus(order.id, nextStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderManagementCard(
    order: Order,
    currency: String,
    isArabic: Boolean,
    onAdvanceStatus: (String) -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.createdAt))

    val (statusColor, statusTitle) = when (order.status) {
        OrderStatus.DELIVERED.code -> StatusDelivered to if (isArabic) "تم التسليم (معتمد)" else "Delivered"
        OrderStatus.SHIPPED.code -> StatusShipped to if (isArabic) "تم الشحن" else "Shipped"
        OrderStatus.PREPARING.code -> StatusPreparing to if (isArabic) "قيد التجهيز" else "Preparing"
        OrderStatus.RETURNED.code -> StatusReturned to if (isArabic) "مرتجع" else "Returned"
        OrderStatus.CANCELLED.code -> StatusCancelled to if (isArabic) "ملغي" else "Cancelled"
        else -> Color(0xFF1976D2) to if (isArabic) "جديد" else "New"
    }

    val paymentTitle = when (order.paymentType) {
        PaymentType.COD.code -> if (isArabic) "الدفع عند الاستلام" else "Cash on Delivery"
        PaymentType.ONLINE_CARD.code -> if (isArabic) "بطاقة بنكية (مدى/فيزا)" else "Card"
        PaymentType.STC_PAY.code -> "STC Pay"
        PaymentType.INSTAPAY.code -> "InstaPay"
        else -> order.paymentType
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.orderNumber}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formattedDate,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 10.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = statusTitle,
                        color = statusColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Product & Commission Details
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isArabic) order.productTitleAr else order.productTitleEn,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "${if (isArabic) "الكمية:" else "Qty:"} ${order.quantity} × ${order.unitSellingPrice} $currency",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = if (isArabic) "إجمالي المبلغ:" else "Total Amount:",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "${order.totalOrderAmount.toInt()} $currency",
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Customer Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${order.customerName} - ${order.customerCity} (${order.customerAddress})",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${if (isArabic) "هاتف العميل:" else "Phone:"} ${order.customerPhone}",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                Text(
                    text = paymentTitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            if (order.trackingNumber.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (isArabic) "رقم التتبع:" else "Tracking #:"} ${order.trackingNumber}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.outline,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status Advancement Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (order.status) {
                    OrderStatus.NEW.code -> {
                        OutlinedButton(
                            onClick = { onAdvanceStatus(OrderStatus.CANCELLED.code) },
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isArabic) "إلغاء" else "Cancel", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onAdvanceStatus(OrderStatus.PREPARING.code) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusPreparing),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isArabic) "بدء التجهيز بالمستودع" else "Prepare Order", fontSize = 11.sp)
                        }
                    }
                    OrderStatus.PREPARING.code -> {
                        Button(
                            onClick = { onAdvanceStatus(OrderStatus.SHIPPED.code) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusShipped),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isArabic) "تأكيد الشحن مع المندوب" else "Ship with Courier", fontSize = 11.sp)
                        }
                    }
                    OrderStatus.SHIPPED.code -> {
                        OutlinedButton(
                            onClick = { onAdvanceStatus(OrderStatus.RETURNED.code) },
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(text = if (isArabic) "تسجيل كمرتجع" else "Mark Returned", fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = { onAdvanceStatus(OrderStatus.DELIVERED.code) },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusDelivered),
                            modifier = Modifier.height(34.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isArabic) "تم التسليم واعتماد العمولة" else "Deliver & Credit Profit", fontSize = 11.sp)
                        }
                    }
                    OrderStatus.DELIVERED.code -> {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = StatusDelivered.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (isArabic) "✓ تمت إضافة العمولة لمحفظتك المتاحة" else "✓ Commission credited to wallet",
                                color = StatusDelivered,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
