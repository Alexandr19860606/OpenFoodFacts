package com.korelin.openfoodfacts.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.local.repository.LocalRepository
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.repository.HomeDataState
import com.korelin.openfoodfacts.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.Job
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject

private const val LOAD_TIMEOUT = 15000L

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val localRepository: LocalRepository
) : ViewModel() {

    private val _homeState = MutableStateFlow<HomeDataState>(HomeDataState.Loading)
    val homeState: StateFlow<HomeDataState> = _homeState.asStateFlow()

    private val _favoritesMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val favoritesMap: StateFlow<Map<String, Boolean>> = _favoritesMap.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadHomeData()
        loadFavorites()
    }

    fun loadHomeData() {
        if (_isLoading.value) return

        _isLoading.value = true
        _homeState.value = HomeDataState.Loading

        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            try {
                withTimeout(LOAD_TIMEOUT) {
                    // Правильный вызов метода, который возвращает Flow
                    repository.getHomeData().collect { state ->
                        _homeState.value = state
                        if (state !is HomeDataState.Loading) {
                            _isLoading.value = false
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                _homeState.value = HomeDataState.Error("Таймаут загрузки. Проверьте интернет.")
                _isLoading.value = false
            } catch (e: SocketTimeoutException) {
                _homeState.value = HomeDataState.Error("Превышено время ожидания. Проверьте интернет.")
                _isLoading.value = false
            } catch (e: UnknownHostException) {
                _homeState.value = HomeDataState.Error("Нет подключения к интернету")
                _isLoading.value = false
            } catch (e: Exception) {
                _homeState.value = HomeDataState.Error("Ошибка загрузки: ${e.message}")
                _isLoading.value = false
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            localRepository.getFavorites().collect { favorites ->
                val map = favorites.associate { product ->
                    (product.code ?: "") to true
                }
                _favoritesMap.value = map
            }
        }
    }

    fun addToFavorites(product: ProductInfo) {
        viewModelScope.launch {
            localRepository.addToFavorites(product)
        }
    }

    fun removeFromFavorites(productCode: String) {
        viewModelScope.launch {
            localRepository.removeFromFavorites(productCode)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun refresh() {
        loadHomeData()
        loadFavorites()
    }

    fun onTimeout() {
        if (_homeState.value is HomeDataState.Loading) {
            _homeState.value = HomeDataState.Error("Таймаут загрузки. Проверьте интернет.")
            _isLoading.value = false
        }
    }

    override fun onCleared() {
        loadJob?.cancel()
        super.onCleared()
    }
}