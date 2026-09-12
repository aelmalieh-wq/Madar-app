package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY isTrending DESC, id ASC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE sku = :sku LIMIT 1")
    suspend fun getProductBySku(sku: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Query("UPDATE products SET affiliatePrice = :newPrice, commissionAmount = (:newPrice - originalPrice) WHERE id = :productId")
    suspend fun updateAffiliatePrice(productId: Long, newPrice: Double)

    @Query("UPDATE products SET originalPrice = :newOriginalPrice, affiliatePrice = :newAffiliatePrice, commissionAmount = :newCommission, stockQuantity = :newStock, lastSyncTimestamp = :timestamp WHERE id = :productId")
    suspend fun updateSyncedPriceAndStock(productId: Long, newOriginalPrice: Double, newAffiliatePrice: Double, newCommission: Double, newStock: Int, timestamp: Long)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProductById(id: Long)

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}
