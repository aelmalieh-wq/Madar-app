package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.MadarBottomBar
import com.example.ui.components.MadarTopBar
import com.example.ui.components.MainNavTab
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MoreMenuScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PinLockScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AffiliateViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MadarAffiliateApp()
            }
        }
    }
}

@Composable
fun MadarAffiliateApp(
    viewModel: AffiliateViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState()
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val isPriceSyncing by viewModel.isPriceSyncing.collectAsState()
    val isCloudSyncing by viewModel.isCloudSyncing.collectAsState()
    val syncMessage by viewModel.syncMessage.collectAsState()

    val overview by viewModel.financialOverview.collectAsState()
    val products by viewModel.products.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadNotificationsCount.collectAsState()
    val payouts by viewModel.payouts.collectAsState()
    val tickets by viewModel.supportTickets.collectAsState()

    val productQuery by viewModel.productSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val orderStatusFilter by viewModel.orderStatusFilter.collectAsState()

    var currentTab by remember { mutableStateOf(MainNavTab.DASHBOARD) }
    val snackbarHostState = remember { SnackbarHostState() }

    val isArabic = settings.language == "ar"
    val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

    // Show sync / alert snackbar
    LaunchedEffect(syncMessage) {
        syncMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSyncMessage()
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        if (isAppLocked) {
            PinLockScreen(
                isArabic = isArabic,
                onUnlock = { pin -> viewModel.unlockAppWithPin(pin) }
            )
        } else {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    MadarTopBar(
                        isArabic = isArabic,
                        unreadCount = unreadCount,
                        isPriceSyncing = isPriceSyncing,
                        isCloudSyncing = isCloudSyncing,
                        isPinEnabled = settings.isSecurityPinEnabled,
                        currency = settings.currency,
                        onLanguageClick = { viewModel.toggleLanguage() },
                        onNotificationClick = { currentTab = MainNavTab.MORE },
                        onPriceSyncClick = { viewModel.triggerMadarPriceSync() },
                        onLockClick = { viewModel.lockApp() }
                    )
                },
                bottomBar = {
                    MadarBottomBar(
                        currentTab = currentTab,
                        onTabSelected = { currentTab = it },
                        isArabic = isArabic
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentTab) {
                        MainNavTab.DASHBOARD -> {
                            DashboardScreen(
                                overview = overview,
                                products = products,
                                recentOrders = orders,
                                settings = settings,
                                isPriceSyncing = isPriceSyncing,
                                onNavigateTab = { currentTab = it },
                                onSyncPrices = { viewModel.triggerMadarPriceSync() },
                                onTogglePrivacy = { viewModel.toggleBalancePrivacy() },
                                onCreateOrder = { name, phone, city, addr, prod, qty, price, pay, notes ->
                                    viewModel.createCustomerOrder(name, phone, city, addr, prod, qty, price, pay, notes) {
                                        currentTab = MainNavTab.ORDERS
                                    }
                                },
                                onAdvanceOrderStatus = { orderId, newStatus ->
                                    viewModel.advanceOrderStatus(orderId, newStatus)
                                }
                            )
                        }
                        MainNavTab.PRODUCTS -> {
                            ProductsScreen(
                                products = products,
                                settings = settings,
                                searchQuery = productQuery,
                                selectedCategory = selectedCategory,
                                isPriceSyncing = isPriceSyncing,
                                onSearchChange = { viewModel.setProductSearchQuery(it) },
                                onCategoryChange = { viewModel.setSelectedCategory(it) },
                                onSyncPrices = { viewModel.triggerMadarPriceSync() },
                                onUpdatePrice = { id, price -> viewModel.updateAffiliateProductPrice(id, price) },
                                onCreateOrder = { name, phone, city, addr, prod, qty, price, pay, notes ->
                                    viewModel.createCustomerOrder(name, phone, city, addr, prod, qty, price, pay, notes) {
                                        currentTab = MainNavTab.ORDERS
                                    }
                                }
                            )
                        }
                        MainNavTab.ORDERS -> {
                            OrdersScreen(
                                orders = orders,
                                settings = settings,
                                selectedStatus = orderStatusFilter,
                                onStatusFilterChange = { viewModel.setOrderStatusFilter(it) },
                                onAdvanceOrderStatus = { orderId, newStatus ->
                                    viewModel.advanceOrderStatus(orderId, newStatus)
                                }
                            )
                        }
                        MainNavTab.REPORTS -> {
                            ReportsScreen(
                                overview = overview,
                                orders = orders,
                                products = products,
                                settings = settings
                            )
                        }
                        MainNavTab.MORE -> {
                            MoreMenuScreen(
                                overview = overview,
                                payouts = payouts,
                                tickets = tickets,
                                notifications = notifications,
                                settings = settings,
                                isCloudSyncing = isCloudSyncing,
                                onRequestPayout = { amt, method, recipient ->
                                    viewModel.requestPayout(amt, method, recipient) {}
                                },
                                onSubmitTicket = { subj, cat, prio, msg ->
                                    viewModel.submitSupportTicket(subj, cat, prio, msg) {}
                                },
                                onMarkNotificationRead = { id -> viewModel.markNotificationRead(id) },
                                onMarkAllNotificationsRead = { viewModel.markAllNotificationsRead() },
                                onToggleLanguage = { viewModel.toggleLanguage() },
                                onSetCurrency = { curr -> viewModel.setCurrency(curr) },
                                onSetPin = { pin, en -> viewModel.setSecurityPin(pin, en) },
                                onTriggerCloudSync = { viewModel.triggerCloudSync() }
                            )
                        }
                    }
                }
            }
        }
    }
}
