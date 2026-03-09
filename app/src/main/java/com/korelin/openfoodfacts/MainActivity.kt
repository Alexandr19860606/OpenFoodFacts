package com.korelin.openfoodfacts

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "API_TEST"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            setContentView(R.layout.activity_main)
        } catch (e: Exception) {
            Log.e(TAG, "Не удалось загрузить макет: ${e.message}")
        }

        testApi()
    }

    private fun testApi() {
        // Создаем логирующий interceptor
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OKHTTP", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        // СОЗДАЕМ TrustManager, который принимает ВСЕ сертификаты
        val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
        })

        // Создаем SSLContext с нашим TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, java.security.SecureRandom())

        // Создаем HostnameVerifier, который принимает ВСЕ имена хостов
        val hostnameVerifier = HostnameVerifier { _, _ -> true }

        // Настраиваем OkHttp клиент с SSL
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .sslSocketFactory(sslContext.socketFactory, trustAllCerts[0] as X509TrustManager)
            .hostnameVerifier(hostnameVerifier)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()

        // Создаем Retrofit instance
        val retrofit = Retrofit.Builder()
            .baseUrl("https://world.openfoodfacts.org/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Создаем сервис
        val apiService = retrofit.create(FoodApiService::class.java)

        // Запускаем тесты
        CoroutineScope(Dispatchers.IO).launch {
            testGetProductByBarcode(apiService)
            testSearchProducts(apiService)
        }
    }

    private suspend fun testGetProductByBarcode(service: FoodApiService) {
        try {
            val barcode = "3017620422003" // Nutella
            Log.d(TAG, "Запрашиваю продукт со штрих-кодом: $barcode")

            val response = service.getProductByBarcode(barcode)

            withContext(Dispatchers.Main) {
                if (response.status_verbose == "product found") {
                    val product = response.product
                    Log.d(TAG, "✅ ПРОДУКТ НАЙДЕН!")
                    Log.d(TAG, "   Название: ${product?.product_name ?: "Н/Д"}")
                    Log.d(TAG, "   Бренд: ${product?.brands ?: "Н/Д"}")
                    Log.d(TAG, "   Категории: ${product?.categories ?: "Н/Д"}")

                    product?.let {
                        Log.d(TAG, "   Ингредиенты: ${it.ingredients_text ?: "Н/Д"}")
                        Log.d(TAG, "   Энергия: ${it.nutriments?.energy_100g ?: "Н/Д"} ккал/100г")
                    }
                } else {
                    Log.e(TAG, "❌ Продукт не найден. Статус: ${response.status_verbose}")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка при запросе продукта: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun testSearchProducts(service: FoodApiService) {
        try {
            val query = "pizza"
            Log.d(TAG, "Ищу продукты по запросу: '$query'")

            val response = service.searchProducts(query = query, pageSize = 5)

            withContext(Dispatchers.Main) {
                if (response.products.isNullOrEmpty()) {
                    Log.e(TAG, "❌ Продукты не найдены")
                } else {
                    Log.d(TAG, "✅ НАЙДЕНО ПРОДУКТОВ: ${response.count} (показано ${response.products.size})")
                    response.products.take(3).forEachIndexed { index, product ->
                        Log.d(TAG, "   ${index + 1}. ${product.product_name ?: "Без названия"}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Ошибка при поиске: ${e.message}")
        }
    }
}