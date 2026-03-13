package com.korelin.openfoodfacts.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.local.repository.LocalRepository
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _product = MutableStateFlow<ProductInfo?>(null)
    val product: StateFlow<ProductInfo?> = _product.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite.asStateFlow()

    private var currentBarcode: String? = null

    fun loadProduct(barcode: String, forceRefresh: Boolean = false) {
        if (barcode == currentBarcode && !forceRefresh) return

        currentBarcode = barcode

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // 1. Проверяем, в избранном ли продукт
                _isFavorite.value = localRepository.isProductInFavorites(barcode)

                // 2. Пробуем загрузить из локального кэша
                val cachedProduct = localRepository.getProduct(barcode)

                if (cachedProduct != null && !forceRefresh) {
                    _product.value = cachedProduct
                    _isLoading.value = false

                    // Добавляем в историю просмотров
                    localRepository.addToHistory(cachedProduct)
                }

                // 3. Загружаем свежие данные из API
                val response = repository.getProductByBarcode(barcode)

                response.product?.let { freshProduct ->
                    _product.value = freshProduct

                    // Сохраняем в кэш
                    localRepository.saveProduct(freshProduct)

                    // Добавляем в историю просмотров
                    localRepository.addToHistory(freshProduct)

                    // Обновляем статус избранного
                    _isFavorite.value = localRepository.isProductInFavorites(barcode)
                }

            } catch (e: Exception) {
                _error.value = "Ошибка загрузки: ${e.message}"

                // Если нет свежих данных и нет кэша, показываем ошибку
                if (_product.value == null) {
                    _error.value = "Продукт не найден"
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val product = _product.value ?: return@launch
            val barcode = product.code ?: return@launch

            if (_isFavorite.value) {
                localRepository.removeFromFavorites(barcode)
                _isFavorite.value = false
            } else {
                localRepository.addToFavorites(product)
                _isFavorite.value = true
            }
        }
    }

    fun retry() {
        currentBarcode?.let { barcode ->
            loadProduct(barcode, forceRefresh = true)
        }
    }

    fun clearError() {
        _error.value = null
    }
}