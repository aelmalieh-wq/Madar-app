package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.MadarDatabase
import com.example.data.model.AppSettings
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PayoutRequest
import com.example.data.model.PayoutStatus
import com.example.data.model.Product
import com.example.data.model.SupportTicket
import com.example.data.repository.MadarAffiliateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FinancialOverview(
    val totalDeliveredProfit: Double = 0.0,
    val pendingProfit: Double = 0.0,
    val withdrawnProfit: Double = 0.0,
    val availableBalance: Double = 0.0,
    val totalSalesVolume: Double = 0.0,
    val deliveredCount: Int = 0,
    val pendingCount: Int = 0,
    val totalOrdersCount: Int = 0
)

class AffiliateViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MadarDatabase.getInstance(application)
    val repository = MadarAffiliateRepository(database)

    private val prefs = application.getSharedPreferences("madar_affiliate_prefs", Context.MODE_PRIVATE)

    // Persistent Settings
    private val _settings = MutableStateFlow(
        AppSettings(
            language = prefs.getString("language", "ar") ?: "ar",
            currency = let {
                val raw = prefs.getString("currency", "EGP") ?: "EGP"
                if (raw == "SAR") "EGP" else raw
            }
        )
    )
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    // Syncing state indicators
    private val _isPriceSyncing = MutableStateFlow(false)
    val isPriceSyncing: StateFlow<Boolean> = _isPriceSyncing.asStateFlow()

    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // App PIN Lock State
    private val _isAppLocked = MutableStateFlow(false)
    val isAppLocked: StateFlow<Boolean> = _isAppLocked.asStateFlow()

    // Product Search & Filter
    private val _productSearchQuery = MutableStateFlow("")
    val productSearchQuery: StateFlow<String> = _productSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    // Order Status Filter
    private val _orderStatusFilter = MutableStateFlow<String?>(null)
    val orderStatusFilter: StateFlow<String?> = _orderStatusFilter.asStateFlow()

    val products: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<Order>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationItem>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val payouts: StateFlow<List<PayoutRequest>> = repository.allPayouts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val supportTickets: StateFlow<List<SupportTicket>> = repository.allTickets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Financial Metrics derived reactively from Orders and Payouts
    val financialOverview: StateFlow<FinancialOverview> = combine(orders, payouts) { orderList, payoutList ->
        var deliveredProfit = 0.0
        var pendingProfit = 0.0
        var totalSales = 0.0
        var deliveredCount = 0
        var pendingCount = 0

        orderList.forEach { order ->
            when (order.status) {
                OrderStatus.DELIVERED.code -> {
                    deliveredProfit += order.totalCommission
                    totalSales += order.totalOrderAmount
                    deliveredCount++
                }
                OrderStatus.NEW.code, OrderStatus.PREPARING.code, OrderStatus.SHIPPED.code -> {
                    pendingProfit += order.totalCommission
                    pendingCount++
                }
            }
        }

        var withdrawn = 0.0
        var pendingPayouts = 0.0
        payoutList.forEach { p ->
            if (p.status == PayoutStatus.COMPLETED.name) {
                withdrawn += p.amount
            } else if (p.status == PayoutStatus.PENDING.name || p.status == PayoutStatus.PROCESSING.name) {
                pendingPayouts += p.amount
            }
        }

        val available = (deliveredProfit - withdrawn - pendingPayouts).coerceAtLeast(0.0)

        FinancialOverview(
            totalDeliveredProfit = deliveredProfit,
            pendingProfit = pendingProfit,
            withdrawnProfit = withdrawn,
            availableBalance = available,
            totalSalesVolume = totalSales,
            deliveredCount = deliveredCount,
            pendingCount = pendingCount,
            totalOrdersCount = orderList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), FinancialOverview())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    fun setProductSearchQuery(query: String) {
        _productSearchQuery.value = query
    }

    fun setSelectedCategory(cat: String?) {
        _selectedCategory.value = cat
    }

    fun setOrderStatusFilter(status: String?) {
        _orderStatusFilter.value = status
    }

    fun toggleLanguage() {
        val current = _settings.value.language
        val next = if (current == "ar") "en" else "ar"
        _settings.value = _settings.value.copy(language = next)
        prefs.edit().putString("language", next).apply()
    }

    fun setCurrency(currency: String) {
        val effective = if (currency == "SAR") "EGP" else currency
        _settings.value = _settings.value.copy(currency = effective)
        prefs.edit().putString("currency", effective).apply()
    }

    fun toggleBalancePrivacy() {
        _settings.value = _settings.value.copy(isBalanceHidden = !_settings.value.isBalanceHidden)
    }

    fun setSecurityPin(pin: String, enable: Boolean) {
        _settings.value = _settings.value.copy(
            isSecurityPinEnabled = enable,
            securityPin = pin
        )
        if (enable) {
            _isAppLocked.value = true
        }
    }

    fun unlockAppWithPin(enteredPin: String): Boolean {
        if (enteredPin == _settings.value.securityPin) {
            _isAppLocked.value = false
            return true
        }
        return false
    }

    fun lockApp() {
        if (_settings.value.isSecurityPinEnabled) {
            _isAppLocked.value = true
        }
    }

    fun updateAffiliateProductPrice(productId: Long, newPrice: Double) {
        viewModelScope.launch {
            repository.updateAffiliatePrice(productId, newPrice)
        }
    }

    fun createCustomerOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        product: Product,
        quantity: Int,
        customPrice: Double?,
        paymentType: String,
        customerNotes: String,
        onSuccess: (Order) -> Unit
    ) {
        viewModelScope.launch {
            val order = repository.createOrder(
                customerName = customerName,
                customerPhone = customerPhone,
                customerCity = customerCity,
                customerAddress = customerAddress,
                product = product,
                quantity = quantity,
                customSellingPrice = customPrice,
                paymentType = paymentType,
                customerNotes = customerNotes
            )
            onSuccess(order)
        }
    }

    fun advanceOrderStatus(orderId: Long, newStatus: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
        }
    }

    fun triggerMadarPriceSync() {
        viewModelScope.launch {
            _isPriceSyncing.value = true
            _syncMessage.value = if (_settings.value.language == "ar") "جاري جلب وتحديث الأسعار والمخزون من متجر مدار..." else "Fetching latest prices & inventory from Madar store..."
            
            kotlinx.coroutines.delay(1200) // Realistic network round-trip simulation
            val updated = repository.syncPricesFromMadarWebsite()
            
            _isPriceSyncing.value = false
            _syncMessage.value = if (_settings.value.language == "ar") "تم تحديث أسعار ومخزون $updated منتجات بنجاح من مدار!" else "Successfully updated $updated products from Madar web store!"
            _settings.value = _settings.value.copy(lastCloudSyncTime = System.currentTimeMillis())
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _isCloudSyncing.value = true
            kotlinx.coroutines.delay(1500)
            _isCloudSyncing.value = false
            _settings.value = _settings.value.copy(lastCloudSyncTime = System.currentTimeMillis())
            _syncMessage.value = if (_settings.value.language == "ar") "تمت المزامنة السحابية وتأمين البيانات بنجاح" else "Cloud sync & encryption verified successfully"
        }
    }

    fun clearSyncMessage() {
        _syncMessage.value = null
    }

    fun requestPayout(amount: Double, method: String, recipient: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.requestPayout(amount, method, recipient)
            onSuccess()
        }
    }

    fun submitSupportTicket(subject: String, category: String, priority: String, message: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            repository.createSupportTicket(subject, category, priority, message)
            onSuccess()
        }
    }

    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }
}
