package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppSettings
import com.example.data.model.NotificationItem
import com.example.data.model.PayoutRequest
import com.example.data.model.SupportTicket
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.viewmodel.FinancialOverview

enum class MoreSubTab(val code: String, val titleAr: String, val titleEn: String, val icon: ImageVector) {
    SUPPORT("support", "خدمة العملاء 24/7", "Support 24/7", Icons.Default.Headphones),
    NOTIFICATIONS("notifications", "التنبيهات والعروض", "Alerts & Deals", Icons.Default.Notifications),
    SECURITY("security", "إعدادات التطبيق", "App Settings", Icons.Default.Security),
    WALLET("wallet", "أرباح الإدارة (PIN)", "Admin Wallet (PIN)", Icons.Default.AccountBalanceWallet)
}

@Composable
fun MoreMenuScreen(
    overview: FinancialOverview,
    payouts: List<PayoutRequest>,
    tickets: List<SupportTicket>,
    notifications: List<NotificationItem>,
    settings: AppSettings,
    isCloudSyncing: Boolean,
    onRequestPayout: (Double, String, String) -> Unit,
    onSubmitTicket: (String, String, String, String) -> Unit,
    onMarkNotificationRead: (Long) -> Unit,
    onMarkAllNotificationsRead: () -> Unit,
    onToggleLanguage: () -> Unit,
    onSetCurrency: (String) -> Unit,
    onSetPin: (String, Boolean) -> Unit,
    onTriggerCloudSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = settings.language == "ar"
    var activeSubTab by remember { mutableStateOf(MoreSubTab.SUPPORT) }
    var enteredPin by remember { mutableStateOf("") }
    var isWalletUnlocked by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        // Sub-tabs row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(MoreSubTab.values()) { tab ->
                val isSelected = activeSubTab == tab
                FilterChip(
                    selected = isSelected,
                    onClick = { activeSubTab = tab },
                    leadingIcon = {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White else MadarEmeraldPrimary
                        )
                    },
                    label = {
                        Text(
                            text = if (isArabic) tab.titleAr else tab.titleEn,
                            fontSize = 12.sp
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MadarEmeraldPrimary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Screen content based on active sub tab
        when (activeSubTab) {
            MoreSubTab.SUPPORT -> {
                SupportScreen(
                    tickets = tickets,
                    settings = settings,
                    onSubmitTicket = onSubmitTicket
                )
            }
            MoreSubTab.NOTIFICATIONS -> {
                NotificationsScreen(
                    notifications = notifications,
                    settings = settings,
                    onMarkRead = onMarkNotificationRead,
                    onMarkAllRead = onMarkAllNotificationsRead
                )
            }
            MoreSubTab.SECURITY -> {
                SecuritySettingsScreen(
                    settings = settings,
                    isCloudSyncing = isCloudSyncing,
                    onToggleLanguage = onToggleLanguage,
                    onSetCurrency = onSetCurrency,
                    onSetPin = onSetPin,
                    onTriggerCloudSync = onTriggerCloudSync
                )
            }
            MoreSubTab.WALLET -> {
                if (settings.isSecurityPinEnabled && !isWalletUnlocked) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = MadarEmeraldPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Text(
                                    text = if (isArabic) "قسم الإدارة والأرباح محمي" else "Admin Portal Protected",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isArabic) "بيانات الأرباح والعمولات مخفية عن العميل ومحمية برمز PIN" else "Earnings are hidden from customers & PIN protected",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = enteredPin,
                                    onValueChange = {
                                        enteredPin = it
                                        pinError = false
                                    },
                                    placeholder = { Text(if (isArabic) "أدخل رمز PIN" else "Enter PIN") },
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                    isError = pinError,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (pinError) {
                                    Text(
                                        text = if (isArabic) "رمز PIN غير صحيح" else "Incorrect PIN",
                                        color = MaterialTheme.colorScheme.error,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = {
                                        if (enteredPin == settings.securityPin || settings.securityPin.isEmpty()) {
                                            isWalletUnlocked = true
                                        } else {
                                            pinError = true
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(text = if (isArabic) "فتح بوابة الإدارة" else "Unlock Admin Portal")
                                }
                            }
                        }
                    }
                } else {
                    WalletScreen(
                        overview = overview,
                        payouts = payouts,
                        settings = settings,
                        onRequestPayout = onRequestPayout
                    )
                }
            }
        }
    }
}

