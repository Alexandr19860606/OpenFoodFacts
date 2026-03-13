package com.korelin.openfoodfacts.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    // Состояние результатов
    private val _searchResults = MutableStateFlow<List<ProductInfo>>(emptyList())
    val searchResults: StateFlow<List<ProductInfo>> = _searchResults.asStateFlow()

    // Состояния загрузки
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isLoadingMore = MutableStateFlow(false)
    val isLoadingMore: StateFlow<Boolean> = _isLoadingMore.asStateFlow()

    // Ошибки
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Пагинация
    private val _currentPage = MutableStateFlow(1)
    private val _totalPages = MutableStateFlow(1)
    private val _hasMorePages = MutableStateFlow(false)

    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()

    private var currentQuery = ""

    fun searchProducts(query: String, reset: Boolean = true) {
        if (query.isBlank()) {
            clearResults()
            return
        }

        if (reset) {
            currentQuery = query
            _currentPage.value = 1
            _searchResults.value = emptyList()
        }

        viewModelScope.launch {
            if (reset) {
                _isLoading.value = true
            } else {
                _isLoadingMore.value = true
            }
            _error.value = null

            try {
                val response = repository.searchProducts(
                    query = query,
                    page = _currentPage.value,
                    pageSize = 20
                )

                val newProducts = response.products ?: emptyList()
                val totalCount = response.count ?: 0
                val pageSize = response.page_size ?: 20

                // Расчет количества страниц
                _totalPages.value = if (totalCount > 0) {
                    (totalCount + pageSize - 1) / pageSize
                } else {
                    1
                }

                _hasMorePages.value = _currentPage.value < _totalPages.value

                if (reset) {
                    _searchResults.value = newProducts
                } else {
                    _searchResults.value = _searchResults.value + newProducts
                }

            } catch (e: Exception) {
                _error.value = "Ошибка сети: ${e.message}"
            } finally {
                _isLoading.value = false
                _isLoadingMore.value = false
            }
        }
    }

    fun loadNextPage() {
        if (!_hasMorePages.value || _isLoadingMore.value || _currentPage.value >= _totalPages.value) {
            return
        }

        _currentPage.value++
        searchProducts(currentQuery, reset = false)
    }

    fun checkAndLoadMore(
        visibleItemCount: Int,
        totalItemCount: Int,
        firstVisibleItemPosition: Int
    ) {
        if (!_hasMorePages.value || _isLoadingMore.value) return

        if (visibleItemCount + firstVisibleItemPosition >= totalItemCount - 5) {
            loadNextPage()
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _error.value = null
        _currentPage.value = 1
        _hasMorePages.value = false
        currentQuery = ""
    }

    fun retry() {
        if (currentQuery.isNotBlank()) {
            searchProducts(currentQuery, reset = true)
        }
    }
}