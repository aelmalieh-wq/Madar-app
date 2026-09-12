package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.PaymentType
import com.example.data.model.Product
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold

@Composable
fun CreateOrderDialog(
    product: Product,
    currency: String,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        quantity: Int,
        sellingPrice: Double,
        paymentType: String,
        notes: String
    ) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var customerCity by remember { mutableStateOf("القاهرة") }
    var customerAddress by remember { mutableStateOf("") }
    var quantity by remember { mutableIntStateOf(1) }
    var sellingPrice by remember { mutableDoubleStateOf(product.affiliatePrice) }
    var selectedPayment by remember { mutableStateOf(PaymentType.COD.code) }
    var notes by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    val commissionPerItem = (sellingPrice - product.originalPrice).coerceAtLeast(0.0)
    val totalOrderAmount = sellingPrice * quantity
    val totalProfit = commissionPerItem * quantity

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isArabic) "إنشاء طلب جديد بعمولة" else "Create Commission Order",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isArabic) product.titleAr else product.titleEn,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Commission Preview Banner
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (isArabic) "ربحك المتوقع من هذا الطلب:" else "Your Expected Profit:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$totalProfit $currency",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MadarEmeraldPrimary
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = if (isArabic) "إجمالي قيمة الطلب" else "Total Order Value",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "$totalOrderAmount $currency",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Customer Info Fields
                OutlinedTextField(
                    value = customerName,
                    onValueChange = {
                        customerName = it
                        nameError = it.isBlank()
                    },
                    label = { Text(if (isArabic) "اسم العميل الكامل *" else "Customer Full Name *") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_customer_name_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = {
                        customerPhone = it
                        phoneError = it.isBlank()
                    },
                    label = { Text(if (isArabic) "رقم هاتف العميل (واتساب) *" else "Customer Phone *") },
                    isError = phoneError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("order_customer_phone_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = customerCity,
                        onValueChange = { customerCity = it },
                        label = { Text(if (isArabic) "المدينة" else "City") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = customerAddress,
                        onValueChange = { customerAddress = it },
                        label = { Text(if (isArabic) "الحي / العنوان" else "District / Address") },
                        modifier = Modifier.weight(1.5f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity and Selling Price Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isArabic) "الكمية:" else "Quantity:",
                        fontWeight = FontWeight.Medium
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { if (quantity > 1) quantity-- },
                            enabled = quantity > 1
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease")
                        }
                        Text(
                            text = "$quantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        IconButton(
                            onClick = { if (quantity < product.stockQuantity) quantity++ },
                            enabled = quantity < product.stockQuantity
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Selling Price
                OutlinedTextField(
                    value = sellingPrice.toString(),
                    onValueChange = {
                        it.toDoubleOrNull()?.let { p ->
                            if (p >= product.originalPrice) sellingPrice = p
                        }
                    },
                    label = { Text(if (isArabic) "سعر بيع القطعة للعميل ($currency)" else "Unit Selling Price ($currency)") },
                    supportingText = {
                        Text(
                            text = if (isArabic)
                                "سعر تكلفة مدار: ${product.originalPrice} $currency (العمولة: ${sellingPrice - product.originalPrice} $currency)"
                            else
                                "Madar base price: ${product.originalPrice} $currency (Margin: ${sellingPrice - product.originalPrice} $currency)"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method
                Text(
                    text = if (isArabic) "طريقة دفع العميل:" else "Payment Method:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    PaymentType.values().forEach { pt ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedPayment == pt.code,
                                onClick = { selectedPayment = pt.code }
                            )
                            Text(
                                text = if (isArabic) pt.titleAr else pt.titleEn,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(if (isArabic) "ملاحظات إضافية للمستودع والشحن" else "Notes for Courier / Warehouse") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (customerName.isBlank()) {
                            nameError = true
                            return@Button
                        }
                        if (customerPhone.isBlank()) {
                            phoneError = true
                            return@Button
                        }
                        onSubmit(
                            customerName,
                            customerPhone,
                            customerCity,
                            customerAddress,
                            quantity,
                            sellingPrice,
                            selectedPayment,
                            notes
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("submit_order_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isArabic) "تأكيد الطلب وحجز العمولة ($totalProfit $currency)" else "Confirm Order & Lock Commission ($totalProfit $currency)",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
