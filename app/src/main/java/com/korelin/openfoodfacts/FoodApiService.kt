package com.korelin.openfoodfacts.data.api

import com.korelin.openfoodfacts.data.model.ProductResponse
import com.korelin.openfoodfacts.data.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApiService {

    @GET("api/v0/product/{barcode}.json")
    suspend fun getProductByBarcode(
        @Path("barcode") barcode: String
    ): ProductResponse

    @GET("cgi/search.pl")
    suspend fun searchProducts(
        @Query("search_terms") query: String,
        @Query("search_simple") searchSimple: String = "1",
        @Query("action") action: String = "process",
        @Query("json") json: String = "1",
        @Query("page_size") pageSize: Int = 20
    ): SearchResponse
}
