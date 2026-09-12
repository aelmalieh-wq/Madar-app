package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.AppSettings
import com.example.data.model.PayoutRequest
import com.example.data.model.PayoutStatus
import com.example.ui.theme.MadarEmeraldDark
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold
import com.example.ui.theme.MadarNavy
import com.example.ui.theme.StatusDelivered
import com.example.ui.theme.StatusPreparing
import com.example.ui.viewmodel.FinancialOverview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WalletScreen(
    overview: FinancialOverview,
    payouts: List<PayoutRequest>,
    settings: AppSettings,
    onRequestPayout: (amount: Double, method: String, recipient: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = settings.language == "ar"
    val currency = if (settings.currency == "EGP" || settings.currency == "SAR") {
        if (isArabic) "ج.م" else "EGP"
    } else {
        settings.getCurrencySymbol()
    }
    var showPayoutDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Balance Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MadarNavy),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wallet_balance_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(MadarNavy, MadarEmeraldDark)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isArabic) "الرصيد المتاح للسحب الفوري" else "Available Withdrawable Balance",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 13.sp
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MadarGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isArabic) "حماية إلكترونية مشفرة" else "Encrypted & Secure",
                                    fontSize = 11.sp,
                                    color = MadarGold,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${overview.availableBalance} $currency",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isArabic) "إجمالي المسحوبات السابقة" else "Total Withdrawn",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${overview.withdrawnProfit} $currency",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isArabic) "أرباح معلقة قيد التسليم" else "Pending Delivery",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${overview.pendingProfit} $currency",
                                    color = Color(0xFF90CAF9),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = { showPayoutDialog = true },
                            enabled = overview.availableBalance >= 50.0,
                            colors = ButtonDefaults.buttonColors(containerColor = MadarGold),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("request_payout_button")
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = MadarNavy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "طلب سحب الأرباح الآن" else "Request Payout Now",
                                color = MadarNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Payout Methods Supported Banner
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isArabic) "وسائل الدفع والتحويل الإلكتروني المعتمدة:" else "Supported Payment & Transfer Systems:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isArabic)
                            "• تحويل بنكي محلي فوري (IBAN)\n• شبكة المدفوعات اللحظية InstaPay\n• محفظة STC Pay الرقمية\n• المحافظ الإلكترونية المعتمدة (Vodafone Cash وغيرها)"
                        else
                            "• Fast Bank Wire (IBAN)\n• InstaPay Instant Transfer\n• STC Pay Digital Wallet\n• Verified E-Wallets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        // Payout History
        item {
            Text(
                text = if (isArabic) "سجل طلبات سحب العمولات" else "Payout History",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (payouts.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isArabic) "لا توجد طلبات سحب سابقة." else "No payout requests yet.",
                        modifier = Modifier.padding(20.dp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            items(payouts, key = { it.id }) { payout ->
                PayoutHistoryCard(
                    payout = payout,
                    currency = currency,
                    isArabic = isArabic
                )
            }
        }
    }

    if (showPayoutDialog) {
        PayoutRequestDialog(
            availableBalance = overview.availableBalance,
            currency = currency,
            isArabic = isArabic,
            onDismiss = { showPayoutDialog = false },
            onSubmit = { amount, method, recipient ->
                onRequestPayout(amount, method, recipient)
                showPayoutDialog = false
            }
        )
    }
}

@Composable
fun PayoutHistoryCard(
    payout: PayoutRequest,
    currency: String,
    isArabic: Boolean
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy - hh:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(payout.requestedAt))

    val (statusColor, statusTitle, statusIcon) = when (payout.status) {
        PayoutStatus.COMPLETED.name -> Triple(StatusDelivered, if (isArabic) "تم التحويل بنجاح" else "Completed", Icons.Default.CheckCircle)
        PayoutStatus.PROCESSING.name -> Triple(StatusPreparing, if (isArabic) "جاري المعالجة البنكية" else "Processing", Icons.Default.Schedule)
        else -> Triple(Color(0xFF1976D2), if (isArabic) "قيد المراجعة" else "Pending Review", Icons.Default.Schedule)
    }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payout.payoutCode,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Text(
                    text = "${payout.payoutMethod} - ${payout.recipientInfo}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    maxLines = 1,
                    fontSize = 11.sp
                )
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${payout.amount} $currency",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(statusIcon, contentDescription = null, tint = statusColor, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = statusTitle, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun PayoutRequestDialog(
    availableBalance: Double,
    currency: String,
    isArabic: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (amount: Double, method: String, recipient: String) -> Unit
) {
    var amountText by remember { mutableStateOf(availableBalance.toInt().toString()) }
    var selectedMethod by remember { mutableStateOf("InstaPay") }
    var recipientInfo by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf<String?>(null) }

    val methods = listOf(
        "InstaPay" to if (isArabic) "إنستاباي (InstaPay) - تحويل لحظي" else "InstaPay (Instant Transfer)",
        "Vodafone Cash" to if (isArabic) "فودافون كاش (Vodafone Cash)" else "Vodafone Cash",
        "Orange / Etisalat Cash" to if (isArabic) "أورنج كاش / اتصالات كاش" else "Orange / Etisalat Cash",
        "Egyptian Bank Account" to if (isArabic) "حساب بنكي مصري (الأهلي / مصر / CIB)" else "Egyptian Bank Account (IBAN)"
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isArabic) "طلب سحب عمولات مدار" else "Request Commission Payout",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isArabic) "الرصيد المتاح: $availableBalance $currency" else "Available: $availableBalance $currency",
                    color = MadarEmeraldPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(if (isArabic) "المبلغ المطلوب سحبه ($currency)" else "Withdrawal Amount ($currency)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isArabic) "طريقة التحويل الإلكتروني:" else "Transfer Method:",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                methods.forEach { (key, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedMethod == key,
                            onClick = { selectedMethod = key }
                        )
                        Text(text = label, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = recipientInfo,
                    onValueChange = { recipientInfo = it },
                    label = { Text(if (isArabic) "رقم الآيبان / الحساب / الهاتف المستلم" else "IBAN / Account / Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )

                errorText?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull()
                        if (amt == null || amt <= 0) {
                            errorText = if (isArabic) "يرجى إدخال مبلغ صحيح" else "Please enter a valid amount"
                            return@Button
                        }
                        if (amt > availableBalance) {
                            errorText = if (isArabic) "المبلغ أكبر من الرصيد المتاح" else "Amount exceeds available balance"
                            return@Button
                        }
                        if (recipientInfo.isBlank()) {
                            errorText = if (isArabic) "يرجى كتابة تفاصيل الحساب المستلم" else "Please enter recipient details"
                            return@Button
                        }
                        onSubmit(amt, selectedMethod, recipientInfo)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("submit_payout_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = if (isArabic) "تأكيد طلب السحب الفوري" else "Confirm Payout Request")
                }
            }
        }
    }
}
