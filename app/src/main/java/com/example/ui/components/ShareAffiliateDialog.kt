package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Product
import com.example.ui.theme.MadarEmeraldPrimary

@Composable
fun ShareAffiliateDialog(
    product: Product,
    currency: String,
    partnerCode: String,
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isCopiedPitch by remember { mutableStateOf(false) }
    var isCopiedAffiliate by remember { mutableStateOf(false) }
    var isCopiedMadar by remember { mutableStateOf(false) }

    val marketingPitch = if (isArabic) {
        """
        🔥 عرض حصري ومميز من موقع مدار!
        📦 ${product.titleAr}
        🏢 المنصة المصدر: ${product.platform}
        ✨ ${product.descriptionAr}
        
        💰 السعر الحالي: ${product.affiliatePrice.toInt()} $currency فقط!
        🚚 التوصيل متاح لجميع محافظات مصر والدفع عند الاستلام.
        
        🔗 للطلب المباشر مع الخصم:
        ${product.affiliateUrl}
        
        🌐 تفاصيل المنتج على موقع مدار:
        ${product.madarUrl}
        """.trimIndent()
    } else {
        """
        🔥 Exclusive Offer via Madar Platform!
        📦 ${product.titleEn}
        🏢 Source Platform: ${product.platform}
        ✨ ${product.descriptionEn}
        
        💰 Price: ${product.affiliatePrice.toInt()} $currency only!
        🚚 Fast shipping across all Egypt governorates with Cash on Delivery.
        
        🔗 Order directly with discount:
        ${product.affiliateUrl}
        
        🌐 View on Madar Store:
        ${product.madarUrl}
        """.trimIndent()
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 16.dp)
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MadarEmeraldPrimary.copy(alpha = 0.15f))
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Share,
                                contentDescription = null,
                                tint = MadarEmeraldPrimary
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (isArabic) "مشاركة روابط وعمولة المنتج" else "Share Affiliate Links",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = product.platform,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Affiliate Tracking Link Box
                Text(
                    text = if (isArabic) "رابط العمولة المباشر (${product.platform}):" else "Direct Affiliate Link (${product.platform}):",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = product.affiliateUrl,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(product.affiliateUrl))
                                isCopiedAffiliate = true
                                Toast.makeText(context, if (isArabic) "تم نسخ رابط العمولة" else "Affiliate link copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isCopiedAffiliate) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = if (isCopiedAffiliate) MadarEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.affiliateUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Madar Website Link Box
                Text(
                    text = if (isArabic) "رابط صفحة المنتج على موقع مدار (madar-studio-2.ai.studio):" else "Product Page on Madar Website:",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = product.madarUrl,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(product.madarUrl))
                                isCopiedMadar = true
                                Toast.makeText(context, if (isArabic) "تم نسخ رابط مدار" else "Madar link copied", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isCopiedMadar) Icons.Default.Check else Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = if (isCopiedMadar) MadarEmeraldPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(product.madarUrl))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = "Open", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = if (isArabic) "رسالة الترويج المقترحة للعملاء (واتساب / تليجرام):" else "Ready-to-Share Pitch for Customers:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = marketingPitch,
                    onValueChange = {},
                    readOnly = true,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(marketingPitch))
                            isCopiedPitch = true
                            Toast.makeText(context, if (isArabic) "تم نسخ الرسالة التسويقية" else "Pitch copied", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_pitch_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            if (isCopiedPitch) Icons.Default.Check else Icons.Default.ContentCopy,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isCopiedPitch) (if (isArabic) "تم النسخ" else "Copied") else (if (isArabic) "نسخ النص" else "Copy"))
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, marketingPitch)
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, if (isArabic) "مشاركة مع العميل" else "Share with Customer")
                            context.startActivity(shareIntent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MadarEmeraldPrimary),
                        modifier = Modifier
                            .weight(1.3f)
                            .testTag("share_whatsapp_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = if (isArabic) "مشاركة فورية" else "Share Direct")
                    }
                }
            }
        }
    }
}
