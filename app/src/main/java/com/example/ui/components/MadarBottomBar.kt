package com.example.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

enum class MainNavTab(val code: String, val titleAr: String, val titleEn: String) {
    DASHBOARD("dashboard", "الرئيسية", "Home"),
    PRODUCTS("products", "المتجر والعروض", "Deals"),
    ORDERS("orders", "طلباتي", "My Orders"),
    REPORTS("reports", "دليل التوفير", "Savings Guide"),
    MORE("more", "المزيد", "More")
}

@Composable
fun MadarBottomBar(
    currentTab: MainNavTab,
    onTabSelected: (MainNavTab) -> Unit,
    isArabic: Boolean,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        NavigationBarItem(
            selected = currentTab == MainNavTab.DASHBOARD,
            onClick = { onTabSelected(MainNavTab.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
            label = { Text(if (isArabic) MainNavTab.DASHBOARD.titleAr else MainNavTab.DASHBOARD.titleEn) },
            modifier = Modifier.testTag("nav_tab_dashboard")
        )
        NavigationBarItem(
            selected = currentTab == MainNavTab.PRODUCTS,
            onClick = { onTabSelected(MainNavTab.PRODUCTS) },
            icon = { Icon(Icons.Default.ShoppingBag, contentDescription = null) },
            label = { Text(if (isArabic) MainNavTab.PRODUCTS.titleAr else MainNavTab.PRODUCTS.titleEn) },
            modifier = Modifier.testTag("nav_tab_products")
        )
        NavigationBarItem(
            selected = currentTab == MainNavTab.ORDERS,
            onClick = { onTabSelected(MainNavTab.ORDERS) },
            icon = { Icon(Icons.AutoMirrored.Filled.ListAlt, contentDescription = null) },
            label = { Text(if (isArabic) MainNavTab.ORDERS.titleAr else MainNavTab.ORDERS.titleEn) },
            modifier = Modifier.testTag("nav_tab_orders")
        )
        NavigationBarItem(
            selected = currentTab == MainNavTab.REPORTS,
            onClick = { onTabSelected(MainNavTab.REPORTS) },
            icon = { Icon(Icons.Default.Assessment, contentDescription = null) },
            label = { Text(if (isArabic) MainNavTab.REPORTS.titleAr else MainNavTab.REPORTS.titleEn) },
            modifier = Modifier.testTag("nav_tab_reports")
        )
        NavigationBarItem(
            selected = currentTab == MainNavTab.MORE,
            onClick = { onTabSelected(MainNavTab.MORE) },
            icon = { Icon(Icons.Default.MoreHoriz, contentDescription = null) },
            label = { Text(if (isArabic) MainNavTab.MORE.titleAr else MainNavTab.MORE.titleEn) },
            modifier = Modifier.testTag("nav_tab_more")
        )
    }
}
