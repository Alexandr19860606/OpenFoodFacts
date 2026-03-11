package com.korelin.openfoodfacts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.korelin.openfoodfacts.data.local.entity.ProductEntity
import com.korelin.openfoodfacts.data.local.entity.FavoriteEntity
import com.korelin.openfoodfacts.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    // ===== PRODUCTS =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllProducts(products: List<ProductEntity>)

    @Query("SELECT * FROM products WHERE code = :code")
    suspend fun getProductByCode(code: String): ProductEntity?

    @Query("SELECT * FROM products ORDER BY lastUpdated DESC LIMIT :limit")
    suspend fun getRecentProducts(limit: Int = 20): List<ProductEntity>

    @Query("SELECT * FROM products ORDER BY lastUpdated DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("DELETE FROM products WHERE lastUpdated < :cutoffTime")
    suspend fun deleteOldEntries(cutoffTime: Long)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    // ===== FAVORITES =====
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFromFavorites(favorite: FavoriteEntity)

    @Query("SELECT * FROM favorites ORDER BY addedAt DESC")
    fun getFavorites(): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE productCode = :productCode)")
    suspend fun isProductInFavorites(productCode: String): Boolean

    @Query("SELECT COUNT(*) FROM favorites")
    suspend fun getFavoritesCount(): Int

    // ===== HISTORY =====
    @Insert
    suspend fun addToHistory(history: HistoryEntity)

    @Query("SELECT * FROM history ORDER BY viewedAt DESC LIMIT :limit")
    suspend fun getRecentHistory(limit: Int = 20): List<HistoryEntity>

    @Query("SELECT * FROM history ORDER BY viewedAt DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE viewedAt < :cutoffTime")
    suspend fun deleteOldHistory(cutoffTime: Long)

    @Query("DELETE FROM history")
    suspend fun clearHistory()

    @Query("SELECT COUNT(*) FROM history")
    suspend fun getHistoryCount(): Int
}