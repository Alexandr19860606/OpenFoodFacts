package com.korelin.openfoodfacts.data.api

import com.korelin.openfoodfacts.data.model.ProductResponse
import com.korelin.openfoodfacts.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {

    // Получение продукта по штрих-коду
    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponse

    // ПОИСК продуктов
    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: String = "1",
        @Query("action") action: String = "process",
        @Query("json") json: String = "1",
        @Query("page_size") pageSize: Int = 5,
        @Query("page") page: Int = 1
    ): SearchResponse

    // ПОПУЛЯРНЫЕ ПРОДУКТЫ
    @GET("cgi/search.pl")
    suspend fun getPopularProducts(
        @Query("sort_by") sortBy: String = "popularity",
        @Query("page_size") pageSize: Int = 5,
        @Query("fields") fields: String = "code,product_name,brands,image_url",
        @Query("json") json: String = "1"
    ): SearchResponse

    // ПОДБОРКА: новинки
    @GET("api/v2/search")
    suspend fun getNewProducts(
        @Query("sort_by") sortBy: String = "entry_date",
        @Query("page_size") pageSize: Int = 5,
        @Query("fields") fields: String = "code,product_name,brands,image_url"
    ): SearchResponse

    // Получение продуктов по категории
    @GET("cgi/search.pl")
    suspend fun getProductsByCategory(
        @Query("tagtype_0") tagType: String = "categories",
        @Query("tag_contains_0") contains: String = "contains",
        @Query("tag_0") category: String,
        @Query("page_size") pageSize: Int = 5,
        @Query("json") json: String = "1"
    ): SearchResponse

    // Получение случайных продуктов
    @GET("cgi/search.pl")
    suspend fun getRandomProducts(
        @Query("page_size") pageSize: Int = 5,
        @Query("sort_by") sortBy: String = "random",
        @Query("json") json: String = "1"
    ): SearchResponse
}