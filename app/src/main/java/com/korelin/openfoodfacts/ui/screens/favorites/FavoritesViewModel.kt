package com.korelin.openfoodfacts.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.local.repository.LocalRepository
import com.korelin.openfoodfacts.data.model.ProductInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _favorites = MutableStateFlow<List<ProductInfo>>(emptyList())
    val favorites: StateFlow<List<ProductInfo>> = _favorites.asStateFlow()

    private val _history = MutableStateFlow<List<ProductInfo>>(emptyList())
    val history: StateFlow<List<ProductInfo>> = _history.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _showHistory = MutableStateFlow(false)
    val showHistory: StateFlow<Boolean> = _showHistory.asStateFlow()

    init {
        loadFavorites()
        loadHistory()
    }

    fun loadFavorites() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                localRepository.getFavorites().collect { favoritesList ->
                    _favorites.value = favoritesList
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch {
            localRepository.getAllHistory().collect { historyList ->
                _history.value = historyList
            }
        }
    }

    fun removeFromFavorites(productCode: String) {
        viewModelScope.launch {
            localRepository.removeFromFavorites(productCode)
        }
    }

    fun removeFromHistory(productCode: String) {
        viewModelScope.launch {
            localRepository.removeFromHistory(productCode)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            localRepository.clearHistory()
            _history.value = emptyList()
        }
    }

    fun toggleHistorySheet() {
        _showHistory.value = !_showHistory.value
    }
}