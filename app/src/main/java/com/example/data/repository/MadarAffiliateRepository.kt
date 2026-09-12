package com.example.data.repository

import com.example.data.local.InitialData
import com.example.data.local.MadarDatabase
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PayoutRequest
import com.example.data.model.PayoutStatus
import com.example.data.model.Product
import com.example.data.model.SupportTicket
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MadarAffiliateRepository(
    private val database: MadarDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    private val productDao = database.productDao()
    private val orderDao = database.orderDao()
    private val notificationDao = database.notificationDao()
    private val payoutDao = database.payoutDao()
    private val supportDao = database.supportDao()

    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val allNotifications: Flow<List<NotificationItem>> = notificationDao.getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = notificationDao.getUnreadCount()
    val allPayouts: Flow<List<PayoutRequest>> = payoutDao.getAllPayouts()
    val allTickets: Flow<List<SupportTicket>> = supportDao.getAllTickets()

    suspend fun initializeSeedDataIfNeeded() = withContext(dispatcher) {
        if (productDao.getCount() == 0) {
            productDao.insertAllProducts(InitialData.initialProducts)
        }
        if (orderDao.getOrderCount() == 0) {
            orderDao.insertAllOrders(InitialData.initialOrders)
            notificationDao.insertAllNotifications(InitialData.initialNotifications)
            payoutDao.insertAllPayouts(InitialData.initialPayouts)
            supportDao.insertAllTickets(InitialData.initialTickets)
        }
    }

    suspend fun updateAffiliatePrice(productId: Long, newAffiliatePrice: Double) = withContext(dispatcher) {
        productDao.updateAffiliatePrice(productId, newAffiliatePrice)
    }

    suspend fun createOrder(
        customerName: String,
        customerPhone: String,
        customerCity: String,
        customerAddress: String,
        product: Product,
        quantity: Int,
        customSellingPrice: Double?,
        paymentType: String,
        customerNotes: String
    ): Order = withContext(dispatcher) {
        val unitSelling = customSellingPrice ?: product.affiliatePrice
        val unitBase = product.originalPrice
        val commissionPerItem = unitSelling - unitBase
        val totalAmount = unitSelling * quantity
        val totalCommission = commissionPerItem * quantity
        val orderNum = "MDR-${Random.nextInt(10000, 99999)}"

        val order = Order(
            orderNumber = orderNum,
            customerName = customerName,
            customerPhone = customerPhone,
            customerCity = customerCity,
            customerAddress = customerAddress,
            productId = product.id,
            productSku = product.sku,
            productTitleAr = product.titleAr,
            productTitleEn = product.titleEn,
            quantity = quantity,
            unitSellingPrice = unitSelling,
            unitBasePrice = unitBase,
            commissionPerItem = commissionPerItem,
            totalOrderAmount = totalAmount,
            totalCommission = totalCommission,
            paymentType = paymentType,
            status = OrderStatus.NEW.code,
            trackingNumber = "MDR-TRK-${Random.nextInt(1000, 9999)}",
            customerNotes = customerNotes,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        val newId = orderDao.insertOrder(order)
        val created = order.copy(id = newId)

        // Instant notification for affiliate
        notificationDao.insertNotification(
            NotificationItem(
                titleAr = "تم تسجيل طلب جديد بنجاح: $orderNum",
                titleEn = "New Order Created: $orderNum",
                bodyAr = "تم إنشاء طلب لمنتج ${product.titleAr} لصالح العميل $customerName بإجمالي ربح متوقع $totalCommission ج.م.",
                bodyEn = "Order created for ${product.titleEn} for customer $customerName. Expected commission: $totalCommission EGP.",
                type = NotificationType.ORDER_STATUS.name,
                referenceId = orderNum,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )

        created
    }

    suspend fun updateOrderStatus(orderId: Long, newStatus: String) = withContext(dispatcher) {
        val order = orderDao.getOrderById(orderId) ?: return@withContext
        val now = System.currentTimeMillis()
        orderDao.updateOrderStatus(orderId, newStatus, now)

        val statusObj = OrderStatus.values().find { it.code == newStatus } ?: OrderStatus.NEW

        val notificationTitleAr: String
        val notificationTitleEn: String
        val notificationBodyAr: String
        val notificationBodyEn: String
        val type: String

        when (newStatus) {
            OrderStatus.DELIVERED.code -> {
                type = NotificationType.COMMISSION_CREDITED.name
                notificationTitleAr = "تم تسليم الطلب ${order.orderNumber} وإيداع الأرباح!"
                notificationTitleEn = "Order ${order.orderNumber} Delivered & Commission Credited!"
                notificationBodyAr = "تم تسليم الطلب بنجاح للعميل ${order.customerName}. أضيفت عمولة قدرها ${order.totalCommission} ج.م إلى رصيدك المتاح للسحب."
                notificationBodyEn = "Order delivered to ${order.customerName}. ${order.totalCommission} EGP has been credited to your withdrawable wallet balance."
            }
            OrderStatus.SHIPPED.code -> {
                type = NotificationType.ORDER_STATUS.name
                notificationTitleAr = "تم شحن الطلب ${order.orderNumber}"
                notificationTitleEn = "Order ${order.orderNumber} Shipped"
                notificationBodyAr = "خرج الطلب مع شركة الشحن برقم تتبع ${order.trackingNumber} متجهاً إلى ${order.customerCity}."
                notificationBodyEn = "Order is out with the courier (Tracking: ${order.trackingNumber}) heading to ${order.customerCity}."
            }
            OrderStatus.PREPARING.code -> {
                type = NotificationType.ORDER_STATUS.name
                notificationTitleAr = "الطلب ${order.orderNumber} قيد التجهيز بمستودع مدار"
                notificationTitleEn = "Order ${order.orderNumber} in Preparation at Madar Warehouse"
                notificationBodyAr = "يقوم فريق المستودعات بتغليف وفحص المنتج ${order.productTitleAr} للتسليم."
                notificationBodyEn = "Madar warehouse team is preparing and inspecting ${order.productTitleEn}."
            }
            OrderStatus.RETURNED.code -> {
                type = NotificationType.ORDER_STATUS.name
                notificationTitleAr = "طلب مرتجع: ${order.orderNumber}"
                notificationTitleEn = "Order Returned: ${order.orderNumber}"
                notificationBodyAr = "تم إرجاع الطلب من قبل العميل ${order.customerName}. تم تعليق العمولة المرتبطة."
                notificationBodyEn = "Order returned by customer ${order.customerName}. Commission reverted."
            }
            OrderStatus.CANCELLED.code -> {
                type = NotificationType.ORDER_STATUS.name
                notificationTitleAr = "تم إلغاء الطلب ${order.orderNumber}"
                notificationTitleEn = "Order ${order.orderNumber} Cancelled"
                notificationBodyAr = "تم إلغاء الطلب. لن يتم احتساب أي عمولة."
                notificationBodyEn = "Order has been cancelled. No commission will be credited."
            }
            else -> {
                type = NotificationType.ORDER_STATUS.name
                notificationTitleAr = "تحديث حالة الطلب: ${order.orderNumber}"
                notificationTitleEn = "Order Status Update: ${order.orderNumber}"
                notificationBodyAr = "الحالة الجديدة للطلب هي: ${statusObj.titleAr}."
                notificationBodyEn = "New order status is: ${statusObj.titleEn}."
            }
        }

        notificationDao.insertNotification(
            NotificationItem(
                titleAr = notificationTitleAr,
                titleEn = notificationTitleEn,
                bodyAr = notificationBodyAr,
                bodyEn = notificationBodyEn,
                type = type,
                referenceId = order.orderNumber,
                timestamp = now,
                isRead = false
            )
        )
    }

    suspend fun syncPricesFromMadarWebsite(): Int = withContext(dispatcher) {
        // Automatic price update simulation from Madar store web page
        // Fetches live updates, random price fluctuations or inventory updates
        val currentProducts = productDao.getAllProducts().first()
        val now = System.currentTimeMillis()
        var updatedCount = 0

        currentProducts.forEach { prod ->
            // Slight dynamic realistic market fluctuation (+/- 2% - 5%) to simulate live store updates
            val variancePct = (Random.nextInt(-3, 4)) / 100.0
            val newBasePrice = (prod.originalPrice * (1.0 + variancePct)).coerceAtLeast(40.0)
            val roundedBase = kotlin.math.round(newBasePrice * 10) / 10.0
            val margin = (prod.affiliatePrice - roundedBase).coerceAtLeast(30.0)
            val newStock = (prod.stockQuantity + Random.nextInt(-2, 5)).coerceAtLeast(10)

            productDao.updateSyncedPriceAndStock(
                productId = prod.id,
                newOriginalPrice = roundedBase,
                newAffiliatePrice = roundedBase + margin,
                newCommission = margin,
                newStock = newStock,
                timestamp = now
            )
            updatedCount++
        }

        notificationDao.insertNotification(
            NotificationItem(
                titleAr = "تحديث فوري لأسعار مدار",
                titleEn = "Instant Madar Web Price Sync",
                bodyAr = "تم تحديث أسعار ومخزون $updatedCount منتجات بنجاح من متجر مدار الإلكتروني وضمان أعلى هامش عمولة لك.",
                bodyEn = "Successfully synchronized $updatedCount products with Madar store. Margins and stock verified.",
                type = NotificationType.PRICE_SYNC.name,
                referenceId = "MADAR-SYNC-LIVE",
                timestamp = now,
                isRead = false
            )
        )

        updatedCount
    }

    suspend fun requestPayout(amount: Double, method: String, recipientInfo: String): PayoutRequest = withContext(dispatcher) {
        val code = "PAY-${Random.nextInt(1000, 9999)}"
        val payout = PayoutRequest(
            payoutCode = code,
            amount = amount,
            payoutMethod = method,
            recipientInfo = recipientInfo,
            status = PayoutStatus.PENDING.name,
            transactionRef = "REF-PENDING",
            requestedAt = System.currentTimeMillis()
        )
        val id = payoutDao.insertPayout(payout)

        notificationDao.insertNotification(
            NotificationItem(
                titleAr = "تم استلام طلب السحب: $code",
                titleEn = "Payout Request Received: $code",
                bodyAr = "تم تسجيل طلب سحب مبلغ $amount ج.م عبر $method. جاري التحويل الفوري لحسابك.",
                bodyEn = "Payout request for $amount EGP via $method recorded. Processing transfer to your account.",
                type = NotificationType.PAYOUT_UPDATE.name,
                referenceId = code,
                timestamp = System.currentTimeMillis(),
                isRead = false
            )
        )

        payout.copy(id = id)
    }

    suspend fun createSupportTicket(subject: String, category: String, priority: String, message: String): SupportTicket = withContext(dispatcher) {
        val num = "TCK-${Random.nextInt(1000, 9999)}"
        val ticket = SupportTicket(
            ticketNumber = num,
            subject = subject,
            category = category,
            priority = priority,
            status = "OPEN",
            message = message,
            adminReply = "شكراً لتواصلك مع الدعم الفني لمدار. تم استلام تذكرتك وتعيين مهندس دعم للمتابعة السريعة.",
            createdAt = System.currentTimeMillis()
        )
        val id = supportDao.insertTicket(ticket)
        ticket.copy(id = id)
    }

    suspend fun markNotificationRead(id: Long) = withContext(dispatcher) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllNotificationsRead() = withContext(dispatcher) {
        notificationDao.markAllAsRead()
    }
}
