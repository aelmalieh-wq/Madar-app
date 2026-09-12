package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MadarEmeraldDark
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold

@Composable
fun MadarTopBar(
    isArabic: Boolean,
    unreadCount: Int,
    isPriceSyncing: Boolean,
    isCloudSyncing: Boolean,
    isPinEnabled: Boolean,
    currency: String = "EGP",
    onLanguageClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onPriceSyncClick: () -> Unit,
    onLockClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_spin")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        ),
        label = "rotation"
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Brand Title & Live Sync Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.madar_logo),
                    contentDescription = "Madar Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MADAR | مدار",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Text(
                        text = if (isArabic) "عروض أمازون، نون، وجوميا مصر" else "Amazon, Noon & Jumia Deals",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            // Action Icons
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Price Sync button with rotation
                IconButton(
                    onClick = onPriceSyncClick,
                    modifier = Modifier.testTag("sync_prices_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = if (isArabic) "مزامنة الأسعار" else "Sync Prices",
                        tint = if (isPriceSyncing) MadarEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = if (isPriceSyncing) Modifier.rotate(angle) else Modifier
                    )
                }

                // PIN Lock button if enabled
                if (isPinEnabled) {
                    IconButton(
                        onClick = onLockClick,
                        modifier = Modifier.testTag("lock_app_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = if (isArabic) "قفل التطبيق" else "Lock App",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Notifications with Badge
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notifications_button")
                ) {
                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.error,
                                    contentColor = Color.White
                                ) {
                                    Text(text = "$unreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = if (isArabic) "التنبيهات" else "Notifications",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Currency Badge (Egyptian Pound EGP)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MadarEmeraldPrimary.copy(alpha = 0.12f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .testTag("currency_badge_topbar")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currency == "EGP") (if (isArabic) "🇪🇬 ج.م" else "🇪🇬 EGP") else currency,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MadarEmeraldDark
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Language Switch
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLanguageClick() }
                        .testTag("toggle_language_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "EN" else "عربي",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
