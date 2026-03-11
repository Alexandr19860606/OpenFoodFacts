package com.korelin.openfoodfacts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.korelin.openfoodfacts.ui.components.BottomNavigationBar
import com.korelin.openfoodfacts.ui.screens.favorites.FavoritesScreen
import com.korelin.openfoodfacts.ui.screens.history.HistoryScreen
import com.korelin.openfoodfacts.ui.screens.home.HomeScreen
import com.korelin.openfoodfacts.ui.screens.product.ProductScreen
import com.korelin.openfoodfacts.ui.screens.scanner.ScannerScreen
import com.korelin.openfoodfacts.ui.screens.search.SearchScreen
import com.korelin.openfoodfacts.ui.screens.settings.SettingsScreen
import com.korelin.openfoodfacts.ui.theme.OpenFoodFactsTheme
import com.korelin.openfoodfacts.utils.FileLogger

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FileLogger.d("MainActivity", "onCreate вызван")

        setContent {
            OpenFoodFactsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    FileLogger.d("AppNavigation", "Навигация инициализирована")
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            // Главный экран
            composable("home") {
                FileLogger.d("Nav", "Открыт экран Home")
                HomeScreen(navController = navController)
            }

            // Сканер
            composable("scanner") {
                FileLogger.d("Nav", "Открыт экран Scanner")
                ScannerScreen(
                    onBarcodeScanned = { barcode: String ->
                        FileLogger.d("Nav", "Сканирован штрих-код: $barcode")
                        navController.navigate("product/$barcode")
                    },
                    onNavigateToHistory = {
                        FileLogger.d("Nav", "Навигация на History")
                        navController.navigate("history")
                    },
                    onNavigateToFavorites = {
                        FileLogger.d("Nav", "Навигация на Favorites")
                        navController.navigate("favorites")
                    },
                    onNavigateToSettings = {
                        FileLogger.d("Nav", "Навигация на Settings")
                        navController.navigate("settings")
                    }
                )
            }

            // Детали продукта
            composable("product/{barcode}") { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                FileLogger.d("Nav", "Открыт экран Product с barcode: $barcode")
                ProductScreen(
                    barcode = barcode,
                    onBackPressed = {
                        FileLogger.d("Nav", "Назад с Product")
                        navController.popBackStack()
                    }
                )
            }

            // ПОИСК - ИСПРАВЛЕНО: добавлен composable для search без параметра
            composable("search") {
                FileLogger.d("Nav", "Открыт экран Search")
                SearchScreen(
                    query = "",
                    onBackPressed = {
                        FileLogger.d("Nav", "Назад с Search")
                        navController.popBackStack()
                    },
                    onProductClick = { product ->
                        FileLogger.d("Nav", "Выбран продукт из поиска: ${product.product_name}")
                        val barcodeValue = product.code
                        if (barcodeValue != null) {
                            FileLogger.d("Nav", "Навигация на продукт с barcode: $barcodeValue")
                            navController.navigate("product/$barcodeValue")
                        } else {
                            FileLogger.e("Nav", "Barcode не найден для продукта: ${product.product_name}")
                        }
                    }
                )
            }

            // ПОИСК с параметром (опционально)
            composable("search/{query}") { backStackEntry ->
                val query = backStackEntry.arguments?.getString("query") ?: ""
                FileLogger.d("Nav", "Открыт экран Search с query: $query")
                SearchScreen(
                    query = query,
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onProductClick = { product ->
                        val barcodeValue = product.code
                        if (barcodeValue != null) {
                            navController.navigate("product/$barcodeValue")
                        }
                    }
                )
            }

            // История
            composable("history") {
                FileLogger.d("Nav", "Открыт экран History")
                HistoryScreen(
                    onBackPressed = {
                        FileLogger.d("Nav", "Назад с History")
                        navController.popBackStack()
                    },
                    onProductClick = { barcode: String ->
                        FileLogger.d("Nav", "Выбран продукт из истории: $barcode")
                        navController.navigate("product/$barcode")
                    }
                )
            }

            // Избранное
            composable("favorites") {
                FileLogger.d("Nav", "Открыт экран Favorites")
                FavoritesScreen(
                    onBackPressed = {
                        FileLogger.d("Nav", "Назад с Favorites")
                        navController.popBackStack()
                    },
                    onProductClick = { barcode: String ->
                        FileLogger.d("Nav", "Выбран продукт из избранного: $barcode")
                        navController.navigate("product/$barcode")
                    }
                )
            }

            // Настройки
            composable("settings") {
                FileLogger.d("Nav", "Открыт экран Settings")
                SettingsScreen(
                    onBackPressed = {
                        FileLogger.d("Nav", "Назад с Settings")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}