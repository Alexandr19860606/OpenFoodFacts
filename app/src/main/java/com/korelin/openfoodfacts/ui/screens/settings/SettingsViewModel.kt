package com.korelin.openfoodfacts.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.utils.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : ViewModel() {

    // Тема
    val themeMode: StateFlow<Int> = settingsDataStore.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setThemeMode(mode: Int) {
        viewModelScope.launch {
            settingsDataStore.setThemeMode(mode)
        }
    }

    // Язык
    val language: StateFlow<String> = settingsDataStore.language
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "system")

    fun setLanguage(language: String) {
        viewModelScope.launch {
            settingsDataStore.setLanguage(language)
        }
    }

    // Уведомления
    val notificationsEnabled: StateFlow<Boolean> = settingsDataStore.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val soundEnabled: StateFlow<Boolean> = settingsDataStore.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled: StateFlow<Boolean> = settingsDataStore.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setNotificationsEnabled(enabled)
        }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setSoundEnabled(enabled)
        }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setVibrationEnabled(enabled)
        }
    }

    // Синхронизация
    val autoSync: StateFlow<Boolean> = settingsDataStore.autoSync
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val syncInterval: StateFlow<Int> = settingsDataStore.syncInterval
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 60)

    fun setAutoSync(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAutoSync(enabled)
        }
    }

    fun setSyncInterval(minutes: Int) {
        viewModelScope.launch {
            settingsDataStore.setSyncInterval(minutes)
        }
    }

    // Кэш
    val cacheSize: StateFlow<Int> = settingsDataStore.cacheSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100)

    fun setCacheSize(mb: Int) {
        viewModelScope.launch {
            settingsDataStore.setCacheSize(mb)
        }
    }

    // Главный экран
    val showPopular: StateFlow<Boolean> = settingsDataStore.showPopular
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showNew: StateFlow<Boolean> = settingsDataStore.showNew
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val showCategories: StateFlow<Boolean> = settingsDataStore.showCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setShowPopular(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowPopular(show)
        }
    }

    fun setShowNew(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowNew(show)
        }
    }

    fun setShowCategories(show: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setShowCategories(show)
        }
    }

    // Отображение
    val fontSize: StateFlow<Float> = settingsDataStore.fontSize
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1.0f)

    val compactView: StateFlow<Boolean> = settingsDataStore.compactView
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setFontSize(size: Float) {
        viewModelScope.launch {
            settingsDataStore.setFontSize(size)
        }
    }

    fun setCompactView(compact: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setCompactView(compact)
        }
    }

    // Аналитика
    val analyticsEnabled: StateFlow<Boolean> = settingsDataStore.analyticsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setAnalyticsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            settingsDataStore.setAnalyticsEnabled(enabled)
        }
    }

    // Сброс
    fun resetAllSettings() {
        viewModelScope.launch {
            settingsDataStore.resetAllSettings()
        }
    }

    // Подсчет размера кэша
    fun calculateCacheSize(): String {
        // Здесь можно добавить реальный подсчет размера кэша
        return "45.2 MB"
    }

    fun clearCache() {
        viewModelScope.launch {
            // Здесь можно добавить очистку кэша
        }
    }
}