package com.korelin.openfoodfacts

import android.app.Application
import com.korelin.openfoodfacts.data.api.RetrofitClient
import com.korelin.openfoodfacts.utils.DebugTree
import com.korelin.openfoodfacts.utils.FileLogger
import com.korelin.openfoodfacts.utils.ReleaseTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class OpenFoodFactsApp : Application() {

    companion object {
        // Свой флаг для отладки
        private const val DEBUG = true // Меняйте вручную для релиза
    }

    override fun onCreate() {
        super.onCreate()

        if (DEBUG) {
            Timber.plant(DebugTree())
            Timber.d("🚀 Приложение запущено в DEBUG режиме")
            tryInitializeStetho()
        } else {
            Timber.plant(ReleaseTree())
        }

        try {
            RetrofitClient.testConnection()
            Timber.d("✅ RetrofitClient работает")
        } catch (e: Exception) {
            Timber.e(e, "❌ Ошибка RetrofitClient")
        }
    }

    private fun tryInitializeStetho() {
        try {
            val stethoClass = Class.forName("com.facebook.stetho.Stetho")
            val initMethod = stethoClass.getMethod("initializeWithDefaults", android.content.Context::class.java)
            initMethod.invoke(null, this)
        } catch (e: ClassNotFoundException) {
            // Stetho не подключен - игнорируем
        } catch (e: Exception) {
            Timber.e(e, "Ошибка Stetho")
        }
    }
}