package com.korelin.openfoodfacts.data.local.repository

import com.korelin.openfoodfacts.data.local.AppDatabase
import com.korelin.openfoodfacts.data.local.entity.FavoriteEntity
import com.korelin.openfoodfacts.data.local.entity.HistoryEntity
import com.korelin.openfoodfacts.data.local.entity.ProductEntity
import com.korelin.openfoodfacts.data.model.ProductInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalRepository(
    private val db: AppDatabase
) {

    // ===== PRODUCTS =====

    suspend fun saveProduct(product: ProductInfo) {
        val entity = ProductEntity.fromProductInfo(product)
        db.productDao().insertProduct(entity)
    }

    suspend fun saveProducts(products: List<ProductInfo>) {
        val entities = products.map { ProductEntity.fromProductInfo(it) }
        db.productDao().insertAllProducts(entities)
    }

    suspend fun getProduct(code: String): ProductInfo? {
        return db.productDao().getProductByCode(code)?.toProductInfo()
    }

    suspend fun getRecentProducts(limit: Int = 20): List<ProductInfo> {
        return db.productDao().getRecentProducts(limit).map { it.toProductInfo() }
    }

    fun getAllProducts(): Flow<List<ProductInfo>> {
        return db.productDao().getAllProducts().map { entities ->
            entities.map { it.toProductInfo() }
        }
    }

    suspend fun clearOldCache(hours: Long = 24) {
        val cutoffTime = System.currentTimeMillis() - (hours * 60 * 60 * 1000)
        db.productDao().deleteOldEntries(cutoffTime)
    }

    // ===== FAVORITES =====

    suspend fun addToFavorites(productCode: String) {
        db.productDao().addToFavorites(FavoriteEntity(productCode))
    }

    suspend fun removeFromFavorites(productCode: String) {
        db.productDao().removeFromFavorites(FavoriteEntity(productCode))
    }

    fun getFavorites(): Flow<List<String>> {
        return db.productDao().getFavorites().map { favorites ->
            favorites.map { it.productCode }
        }
    }

    suspend fun isProductInFavorites(productCode: String): Boolean {
        return db.productDao().isProductInFavorites(productCode)
    }

    suspend fun getFavoritesCount(): Int {
        return db.productDao().getFavoritesCount()
    }

    // ===== HISTORY =====

    suspend fun addToHistory(productCode: String) {
        db.productDao().addToHistory(HistoryEntity(productCode = productCode))
    }

    suspend fun getRecentHistory(limit: Int = 20): List<String> {
        return db.productDao().getRecentHistory(limit).map { it.productCode }
    }

    fun getAllHistory(): Flow<List<String>> {
        return db.productDao().getAllHistory().map { history ->
            history.map { it.productCode }
        }
    }

    suspend fun clearOldHistory(days: Int = 7) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
        db.productDao().deleteOldHistory(cutoffTime)
    }

    suspend fun clearHistory() {
        db.productDao().clearHistory()
    }

    suspend fun getHistoryCount(): Int {
        return db.productDao().getHistoryCount()
    }
}