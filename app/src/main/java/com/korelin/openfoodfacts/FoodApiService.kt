package com.korelin.openfoodfacts

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {

    // 1. Получение продукта по штрих-коду
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponse

    // 2. Поиск продуктов по названию
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: String = "1",
        @Query("action") action: String = "process",
        @Query("json") json: String = "1",
        @Query("page_size") pageSize: Int = 10
    ): SearchResponse // SearchResponse импортируется из Product.kt
}