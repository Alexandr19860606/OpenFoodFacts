package com.korelin.openfoodfacts.data.repository

import com.korelin.openfoodfacts.data.model.ProductInfo

sealed class HomeDataState {
    object Loading : HomeDataState()
    data class Success(
        val popular: List<ProductInfo>,
        val new: List<ProductInfo>
    ) : HomeDataState()
    data class Error(val message: String) : HomeDataState()
}