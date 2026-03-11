package com.korelin.openfoodfacts.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repository = ProductRepository()

    private val _searchResults = MutableStateFlow<List<ProductInfo>>(emptyList())
    val searchResults: StateFlow<List<ProductInfo>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun searchProducts(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = repository.searchProducts(query)
                _searchResults.value = response.products ?: emptyList()
            } catch (e: Exception) {
                _error.value = "Ошибка сети: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _error.value = null
    }
}