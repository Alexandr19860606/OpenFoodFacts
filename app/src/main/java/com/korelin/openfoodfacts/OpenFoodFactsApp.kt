package com.korelin.openfoodfacts

import android.app.Application
import android.util.Log
import com.korelin.openfoodfacts.data.api.RetrofitClient

private const val TAG = "OpenFoodFactsApp"

class OpenFoodFactsApp : Application() {

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "🚀 Приложение запущено")

        // Тест Retrofit
        try {
            RetrofitClient.testConnection()
            Log.d(TAG, "✅ RetrofitClient работает")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка RetrofitClient", e)
        }
    }
}