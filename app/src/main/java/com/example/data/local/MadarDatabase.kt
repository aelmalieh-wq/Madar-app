package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.NotificationItem
import com.example.data.model.Order
import com.example.data.model.PayoutRequest
import com.example.data.model.Product
import com.example.data.model.SupportTicket

@Database(
    entities = [
        Product::class,
        Order::class,
        NotificationItem::class,
        PayoutRequest::class,
        SupportTicket::class
    ],
    version = 2,
    exportSchema = false
)
abstract class MadarDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun notificationDao(): NotificationDao
    abstract fun payoutDao(): PayoutDao
    abstract fun supportDao(): SupportDao

    companion object {
        @Volatile
        private var INSTANCE: MadarDatabase? = null

        fun getInstance(context: Context): MadarDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MadarDatabase::class.java,
                    "madar_affiliate_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
