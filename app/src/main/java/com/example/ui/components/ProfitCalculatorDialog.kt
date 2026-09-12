package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Product
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold

@Composable
fun ProfitCalculatorDialog(
    product: Product,
    currency: String,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    var quantity by remember { mutableIntStateOf(5) }
    var targetSellingPrice by remember { mutableDoubleStateOf(product.affiliatePrice) }

    val baseCost = product.originalPrice
    val unitProfit = (targetSellingPrice - baseCost).coerceAtLeast(0.0)
    val totalRevenue = targetSellingPrice * quantity
    val totalCost = baseCost * quantity
    val totalProfit = unitProfit * quantity
    val marginPercentage = if (targetSellingPrice > 0) (unitProfit / targetSellingPrice) * 100 else 0.0

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MadarEmeraldPrimary.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MadarEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isArabic) "حاسبة الأرباح الذكية" else "Smart Profit Calculator",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isArabic) product.titleAr else product.titleEn,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Profit Display Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "إجمالي الربح الصافي المتوقع:" else "Net Expected Profit:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${kotlin.math.round(totalProfit * 10) / 10} $currency",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MadarEmeraldPrimary
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isArabic) "نسبة هامش الربح" else "Profit Margin",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "${kotlin.math.round(marginPercentage * 10) / 10}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MadarGold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Selling Price Input
                OutlinedTextField(
                    value = targetSellingPrice.toString(),
                    onValueChange = {
                        it.toDoubleOrNull()?.let { p ->
                            if (p >= baseCost) targetSellingPrice = p
                        }
                    },
                    label = { Text(if (isArabic) "سعر بيع القطعة المقترح ($currency)" else "Suggested Selling Price ($currency)") },
                    supportingText = {
                        Text(
                            text = if (isArabic)
                                "سعر التكلفة من متجر مدار: $baseCost $currency"
                            else
                                "Madar base store cost: $baseCost $currency"
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("calc_selling_price_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quantity Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isArabic) "عدد القطع المتوقع بيعها:" else "Target Sales Quantity:")
                    Text(text = "$quantity قطعة", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }

                Slider(
                    value = quantity.toFloat(),
                    onValueChange = { quantity = it.toInt() },
                    valueRange = 1f..100f,
                    steps = 99,
                    modifier = Modifier.fillMaxWidth()
                )

                // Breakdown breakdown
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isArabic) "ربح القطعة" else "Per Unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text(text = "$unitProfit $currency", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isArabic) "إجمالي المبيعات" else "Total Revenue", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text(text = "$totalRevenue $currency", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isArabic) "تكلفة مدار" else "Base Cost", fontSize = 11.sp, color = MaterialTheme.colorScheme.outline)
                        Text(text = "$totalCost $currency", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "تم وحفظ الحسابات" else "Done")
                }
            }
        }
    }
}
