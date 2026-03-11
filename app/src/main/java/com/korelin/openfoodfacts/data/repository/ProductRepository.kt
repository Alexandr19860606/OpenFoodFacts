package com.korelin.openfoodfacts.data.repository

import com.korelin.openfoodfacts.data.api.RetrofitClient
import com.korelin.openfoodfacts.data.model.NutrientLevels
import com.korelin.openfoodfacts.data.model.Nutriments
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProductRepository {

    private val api = RetrofitClient.apiService

    // --- ТЕСТОВЫЕ ДАННЫЕ (вынесены в константы) ---
    private val mockPopularProducts = listOf(
        ProductInfo( /* ... Nutella ... */ ),
        ProductInfo( /* ... Coca-Cola ... */ ),
        ProductInfo( /* ... Pizza ... */ )
    )
    private val mockNewProducts = listOf(
        ProductInfo( /* ... Другой продукт ... */ ),
        ProductInfo( /* ... Еще один ... */ ),
        ProductInfo( /* ... И еще ... */ )
    )
    // ----------------------------------------------

    private suspend fun <T> retryIO(
        times: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { i ->
            try {
                return block()
            } catch (e: Exception) {
                when {
                    // Ошибка шлюза или сервиса
                    e is HttpException && (e.code() == 504 || e.code() == 503) -> {
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                    // Ошибка парсинга (сервер вернул не JSON)
                    e is HttpException && e.code() == 200 -> {
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                    // Таймаут чтения/подключения
                    e is SocketTimeoutException -> {
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                    else -> throw e
                }
            }
        }
        return block()
    }

    suspend fun getProductByBarcode(barcode: String) = api.getProductByBarcode(barcode)

    suspend fun searchProducts(query: String, page: Int = 1): SearchResponse =
        retryIO(times = 3) {
            api.searchProducts(query = query, page = page)
        }

    suspend fun getPopularProducts(): List<ProductInfo> = try {
        retryIO(times = 2) {
            val response = api.getPopularProducts()
            if (response.products.isNullOrEmpty()) {
                api.searchProducts(query = "popular", pageSize = 10).products ?: emptyList()
            } else {
                response.products
            }
        }
    } catch (_: Exception) {
        mockPopularProducts
    }

    suspend fun getNewProducts(): List<ProductInfo> = try {
        retryIO(times = 2) {
            val response = api.getNewProducts()
            if (response.products.isNullOrEmpty()) {
                api.searchProducts(query = "new", pageSize = 10).products ?: emptyList()
            } else {
                response.products
            }
        }
    } catch (_: Exception) {
        mockNewProducts.shuffled().take(3)
    }

    fun getHomeData(): Flow<HomeDataState> = flow {
        emit(HomeDataState.Loading)

        val homeData = try {
            val popular = getPopularProducts()
            val new = getNewProducts()

            if (popular.isEmpty() && new.isEmpty()) {
                HomeDataState.Success(popular = mockPopularProducts, new = mockNewProducts.shuffled().take(3))
            } else {
                HomeDataState.Success(popular = popular, new = new)
            }
        } catch (e: UnknownHostException) {
            HomeDataState.Error("Нет подключения к интернету")
        } catch (e: HttpException) {
            HomeDataState.Error("Ошибка сервера: ${e.code()}")
        } catch (e: Exception) {
            HomeDataState.Error("Ошибка загрузки")
        }

        emit(homeData)
    }
}

sealed class HomeDataState {
    object Loading : HomeDataState()
    data class Success(val popular: List<ProductInfo>, val new: List<ProductInfo>) : HomeDataState()
    data class Error(val message: String) : HomeDataState()
}