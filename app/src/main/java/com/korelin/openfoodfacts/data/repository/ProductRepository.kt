package com.korelin.openfoodfacts.data.repository

import com.korelin.openfoodfacts.data.api.RetrofitClient
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.model.SearchResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor() {

    private val api = RetrofitClient.apiService

    // --- ТЕСТОВЫЕ ДАННЫЕ ---
    private val mockPopularProducts = listOf(
        ProductInfo(
            code = "3017620422003",
            product_name = "Nutella",
            brands = "Ferrero",
            quantity = "400g",
            image_url = "https://static.openfoodfacts.org/images/products/301/762/042/2003/front_fr.275.400.jpg",
            nutriments = null
        ),
        ProductInfo(
            code = "5449000000996",
            product_name = "Coca-Cola Original",
            brands = "Coca-Cola",
            quantity = "330ml",
            image_url = "https://static.openfoodfacts.org/images/products/544/900/000/0996/front_en.406.400.jpg",
            nutriments = null
        ),
        ProductInfo(
            code = "8000500310427",
            product_name = "Pizza Margherita",
            brands = "Dr. Oetker",
            quantity = "350g",
            image_url = "https://static.openfoodfacts.org/images/products/800/050/031/0427/front_en.29.400.jpg",
            nutriments = null
        )
    )

    private val mockNewProducts = listOf(
        ProductInfo(
            code = "3017620422003",
            product_name = "Nutella New",
            brands = "Ferrero",
            quantity = "400g",
            image_url = "https://static.openfoodfacts.org/images/products/301/762/042/2003/front_fr.275.400.jpg",
            nutriments = null
        ),
        ProductInfo(
            code = "5053827199568",
            product_name = "Tresor",
            brands = "Kellogg's",
            quantity = "410g",
            image_url = "https://static.openfoodfacts.org/images/products/505/382/719/9568/front_de.44.400.jpg",
            nutriments = null
        ),
        ProductInfo(
            code = "4011676000313",
            product_name = "Cherry cake",
            brands = "HG",
            quantity = "200g",
            image_url = null,
            nutriments = null
        )
    )

    // ===== RETRY LOGIC =====

    private suspend fun <T> retryIO(
        times: Int = 3,
        initialDelay: Long = 1000,
        maxDelay: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { _ ->
            try {
                return block()
            } catch (e: Exception) {
                when {
                    e is HttpException && (e.code() == 504 || e.code() == 503) -> {
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
                    e is HttpException && e.code() == 200 -> {
                        delay(currentDelay)
                        currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
                    }
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

    // ===== API METHODS =====

    suspend fun getProductByBarcode(barcode: String) =
        retryIO(times = 2) {
            api.getProductByBarcode(barcode)
        }

    suspend fun searchProducts(
        query: String,
        page: Int = 1,
        pageSize: Int = 20
    ): SearchResponse = retryIO(times = 3) {
        api.searchProducts(query = query, page = page, pageSize = pageSize)
    }

    suspend fun getPopularProducts(): List<ProductInfo> = try {
        retryIO(times = 2) {
            val response = api.getPopularProducts()
            response.products ?: emptyList()
        }
    } catch (_: Exception) {
        mockPopularProducts
    }

    suspend fun getNewProducts(): List<ProductInfo> = try {
        retryIO(times = 2) {
            val response = api.getNewProducts()
            response.products ?: emptyList()
        }
    } catch (_: Exception) {
        mockNewProducts.shuffled().take(3)
    }

    // ===== HOME DATA =====

    fun getHomeData(): Flow<HomeDataState> = flow {
        emit(HomeDataState.Loading)

        val homeData = try {
            val popular = getPopularProducts()
            val new = getNewProducts()

            if (popular.isEmpty() && new.isEmpty()) {
                HomeDataState.Success(
                    popular = mockPopularProducts,
                    new = mockNewProducts.shuffled().take(3)
                )
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