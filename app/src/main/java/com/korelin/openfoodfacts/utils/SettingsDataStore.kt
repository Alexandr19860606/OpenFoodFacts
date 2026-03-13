package com.korelin.openfoodfacts.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        // Ключи для настроек
        val THEME_MODE = intPreferencesKey("theme_mode") // 0 - system, 1 - light, 2 - dark
        val LANGUAGE = stringPreferencesKey("language")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val AUTO_SYNC = booleanPreferencesKey("auto_sync")
        val SYNC_INTERVAL = intPreferencesKey("sync_interval") // в минутах
        val CACHE_SIZE = intPreferencesKey("cache_size") // в MB
        val SHOW_POPULAR = booleanPreferencesKey("show_popular")
        val SHOW_NEW = booleanPreferencesKey("show_new")
        val SHOW_CATEGORIES = booleanPreferencesKey("show_categories")
        val FONT_SIZE = floatPreferencesKey("font_size") // 1.0 - normal, 1.2 - large, etc.
        val COMPACT_VIEW = booleanPreferencesKey("compact_view")
        val ANALYTICS_ENABLED = booleanPreferencesKey("analytics_enabled")
    }

    // Тема
    suspend fun setThemeMode(mode: Int) {
        context.dataStore.edit { preferences ->
            preferences[THEME_MODE] = mode
        }
    }

    val themeMode: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_MODE] ?: 0 // По умолчанию системная
        }

    // Язык
    suspend fun setLanguage(language: String) {
        context.dataStore.edit { preferences ->
            preferences[LANGUAGE] = language
        }
    }

    val language: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[LANGUAGE] ?: "system"
        }

    // Уведомления
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }

    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SOUND_ENABLED] = enabled
        }
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SOUND_ENABLED] ?: true
        }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[VIBRATION_ENABLED] = enabled
        }
    }

    val vibrationEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[VIBRATION_ENABLED] ?: true
        }

    // Синхронизация
    suspend fun setAutoSync(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_SYNC] = enabled
        }
    }

    val autoSync: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_SYNC] ?: true
        }

    suspend fun setSyncInterval(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[SYNC_INTERVAL] = minutes
        }
    }

    val syncInterval: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[SYNC_INTERVAL] ?: 60 // По умолчанию 60 минут
        }

    // Кэш
    suspend fun setCacheSize(mb: Int) {
        context.dataStore.edit { preferences ->
            preferences[CACHE_SIZE] = mb
        }
    }

    val cacheSize: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[CACHE_SIZE] ?: 100 // По умолчанию 100 MB
        }

    // Главный экран
    suspend fun setShowPopular(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_POPULAR] = show
        }
    }

    val showPopular: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_POPULAR] ?: true
        }

    suspend fun setShowNew(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_NEW] = show
        }
    }

    val showNew: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_NEW] ?: true
        }

    suspend fun setShowCategories(show: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SHOW_CATEGORIES] = show
        }
    }

    val showCategories: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[SHOW_CATEGORIES] ?: true
        }

    // Отображение
    suspend fun setFontSize(size: Float) {
        context.dataStore.edit { preferences ->
            preferences[FONT_SIZE] = size
        }
    }

    val fontSize: Flow<Float> = context.dataStore.data
        .map { preferences ->
            preferences[FONT_SIZE] ?: 1.0f
        }

    suspend fun setCompactView(compact: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[COMPACT_VIEW] = compact
        }
    }

    val compactView: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[COMPACT_VIEW] ?: false
        }

    // Аналитика
    suspend fun setAnalyticsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ANALYTICS_ENABLED] = enabled
        }
    }

    val analyticsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[ANALYTICS_ENABLED] ?: true
        }

    // Сброс настроек
    suspend fun resetAllSettings() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}