package com.korelin.openfoodfacts.data.local.repository

import com.korelin.openfoodfacts.data.local.AppDatabase
import com.korelin.openfoodfacts.data.local.entity.FavoriteEntity
import com.korelin.openfoodfacts.data.local.entity.HistoryEntity
import com.korelin.openfoodfacts.data.model.ProductInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

class LocalRepository(
    private val db: AppDatabase
) {
    @Singleton
    class LocalRepository @Inject constructor(
        private val db: AppDatabase
    )

    // ===== PRODUCTS =====
    suspend fun saveProduct(product: ProductInfo) {
        val entity = com.korelin.openfoodfacts.data.local.entity.ProductEntity.fromProductInfo(product)
        db.productDao().insertProduct(entity)
    }

    suspend fun saveProducts(products: List<ProductInfo>) {
        val entities = products.map { com.korelin.openfoodfacts.data.local.entity.ProductEntity.fromProductInfo(it) }
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
    suspend fun addToFavorites(product: ProductInfo) {
        val favorite = FavoriteEntity(
            productCode = product.code ?: return,
            productName = product.product_name,
            brand = product.brands,
            imageUrl = product.image_front_url ?: product.image_url,
            addedAt = System.currentTimeMillis()
        )
        db.productDao().addToFavorites(favorite)

        // Также сохраняем полную информацию о продукте
        saveProduct(product)
    }

    suspend fun removeFromFavorites(productCode: String) {
        db.productDao().removeFromFavoritesByCode(productCode)
    }

    fun getFavorites(): Flow<List<ProductInfo>> {
        return db.productDao().getFavorites().map { favorites ->
            favorites.mapNotNull { favorite ->
                // Пытаемся получить полную информацию о продукте из кэша
                runCatching {
                    db.productDao().getProductByCode(favorite.productCode)?.toProductInfo()
                }.getOrNull() ?: ProductInfo(
                    code = favorite.productCode,
                    product_name = favorite.productName,
                    brands = favorite.brand,
                    image_url = favorite.imageUrl
                )
            }
        }
    }

    suspend fun isProductInFavorites(productCode: String): Boolean {
        return db.productDao().isProductInFavorites(productCode)
    }

    suspend fun getFavoritesCount(): Int {
        return db.productDao().getFavoritesCount()
    }

    // ===== HISTORY =====
    suspend fun addToHistory(product: ProductInfo) {
        val historyItem = HistoryEntity(
            productCode = product.code ?: return,
            productName = product.product_name,
            brand = product.brands,
            imageUrl = product.image_front_url ?: product.image_url,
            viewedAt = System.currentTimeMillis()
        )
        db.productDao().addToHistory(historyItem)

        // Сохраняем продукт в кэш
        saveProduct(product)
    }

    suspend fun getRecentHistory(limit: Int = 20): List<ProductInfo> {
        return db.productDao().getRecentHistory(limit).mapNotNull { history ->
            runCatching {
                db.productDao().getProductByCode(history.productCode)?.toProductInfo()
            }.getOrNull() ?: ProductInfo(
                code = history.productCode,
                product_name = history.productName,
                brands = history.brand,
                image_url = history.imageUrl
            )
        }
    }

    fun getAllHistory(): Flow<List<ProductInfo>> {
        return db.productDao().getAllHistory().map { historyList ->
            historyList.mapNotNull { history ->
                runCatching {
                    db.productDao().getProductByCode(history.productCode)?.toProductInfo()
                }.getOrNull() ?: ProductInfo(
                    code = history.productCode,
                    product_name = history.productName,
                    brands = history.brand,
                    image_url = history.imageUrl
                )
            }
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

    suspend fun removeFromHistory(productCode: String) {
        db.productDao().removeFromHistory(productCode)
    }
}