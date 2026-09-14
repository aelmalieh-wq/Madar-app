package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MadarEmeraldPrimary
import com.example.ui.theme.MadarGold
import com.example.ui.theme.MadarNavy

object LegalLinks {
    const val BASE_WEB_URL = "https://ais-pre-gfd52btzdlf3pwwxwinrgu-475491529209.europe-west2.run.app"
    const val PRIVACY_URL = "$BASE_WEB_URL/privacy.html"
    const val OWNERSHIP_URL = "$BASE_WEB_URL/ownership.html"
    const val OFFICIAL_WEBSITE_URL = "https://madar-studio-2.ai.studio/"
    const val GITHUB_REPO_URL = "https://github.com/aelmalieh-wq/Madar-app"

    fun openUrl(context: Context, url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "تعذر فتح الرابط: $url", Toast.LENGTH_SHORT).show()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppFooter(
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 12.dp)
            .testTag("app_footer_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // App Brand Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MadarEmeraldPrimary,
                    modifier = Modifier.size(36.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "M",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = if (isArabic) "مدار | MADAR" else "MADAR Deals & Affiliate",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isArabic) "عروض وتخفيضات كبرى المتاجر في مصر" else "Best deals from top stores in Egypt",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Section Label
            Text(
                text = if (isArabic) "الروابط القانونية وإثبات الملكية الرسمية" else "Legal, Ownership & Disclosures",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Prominent Legal Links
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. Privacy Policy Link
                FooterActionChip(
                    icon = Icons.Default.Shield,
                    title = if (isArabic) "سياسة الخصوصية" else "Privacy Policy",
                    subtitle = "/privacy.html",
                    color = MadarEmeraldPrimary,
                    testTag = "footer_privacy_link",
                    onClick = { LegalLinks.openUrl(context, LegalLinks.PRIVACY_URL) }
                )

                // 2. Ownership Verification Link
                FooterActionChip(
                    icon = Icons.Default.VerifiedUser,
                    title = if (isArabic) "إثبات الملكية والإفصاح" else "Proof of Ownership",
                    subtitle = "/ownership.html",
                    color = MadarGold,
                    testTag = "footer_ownership_link",
                    onClick = { LegalLinks.openUrl(context, LegalLinks.OWNERSHIP_URL) }
                )

                // 3. Official Website Link
                FooterActionChip(
                    icon = Icons.Default.Language,
                    title = if (isArabic) "الموقع الإلكتروني" else "Official Website",
                    subtitle = "madar-studio-2",
                    color = Color(0xFF1976D2),
                    testTag = "footer_website_link",
                    onClick = { LegalLinks.openUrl(context, LegalLinks.OFFICIAL_WEBSITE_URL) }
                )

                // 4. GitHub Repository Link
                FooterActionChip(
                    icon = Icons.Default.Description,
                    title = if (isArabic) "المستودع البرمجي" else "GitHub Repo",
                    subtitle = "aelmalieh-wq/Madar",
                    color = MadarNavy,
                    testTag = "footer_github_link",
                    onClick = { LegalLinks.openUrl(context, LegalLinks.GITHUB_REPO_URL) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Copyright & Store verification badge
            Text(
                text = if (isArabic)
                    "معتمد لمتجر Google Play ومطوري Amazon • الإصدار 1.0.0\nجميع الحقوق محفوظة للمطور AElmalieh © 2026"
                else
                    "Verified for Google Play & Amazon Appstore • Version 1.0.0\nAll Rights Reserved © 2026 AElmalieh",
                textAlign = TextAlign.Center,
                fontSize = 10.5.sp,
                lineHeight = 16.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun FooterActionChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = color.copy(alpha = 0.15f),
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Icon(
                imageVector = Icons.Default.OpenInBrowser,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
