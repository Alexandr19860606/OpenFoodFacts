package com.korelin.openfoodfacts

import android.app.Application
import android.util.Log
import com.korelin.openfoodfacts.data.api.RetrofitClient
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class OpenFoodFactsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "🚀 Приложение запущено")

        try {
            RetrofitClient.testConnection()
            Log.d(TAG, "✅ RetrofitClient работает")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка RetrofitClient", e)
        }
    }

    companion object {
        private const val TAG = "OpenFoodFactsApp"
    }
}