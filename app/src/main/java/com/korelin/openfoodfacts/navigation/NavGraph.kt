package com.korelin.openfoodfacts.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.korelin.openfoodfacts.ui.screens.favorites.FavoritesScreen
import com.korelin.openfoodfacts.ui.screens.history.HistoryScreen
import com.korelin.openfoodfacts.ui.screens.home.HomeScreen
import com.korelin.openfoodfacts.ui.screens.notifications.NotificationScreen
import com.korelin.openfoodfacts.ui.screens.product.ProductScreen
import com.korelin.openfoodfacts.ui.screens.scanner.ScannerScreen
import com.korelin.openfoodfacts.ui.screens.search.SearchScreen
import com.korelin.openfoodfacts.ui.screens.settings.SettingsScreen
import com.korelin.openfoodfacts.navigation.Screen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // Главный экран
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        // Сканер
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onBarcodeScanned = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                },
                onNavigateToHistory = { navController.navigate(Screen.History.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
            )
        }

        // Детали продукта
        composable(Screen.Product.route) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductScreen(
                barcode = barcode,
                onBackPressed = { navController.popBackStack() }
            )
        }

        // Поиск
        composable(Screen.Search.route) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchScreen(
                query = query,
                onBackPressed = { navController.popBackStack() },
                onProductClick = { product ->
                    val barcodeValue = product.code
                    if (barcodeValue != null) {
                        navController.navigate(Screen.Product.passBarcode(barcodeValue))
                    }
                }
            )
        }

        // История
        composable(Screen.History.route) {
            HistoryScreen(
                onBackPressed = { navController.popBackStack() },
                onProductClick = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                }
            )
        }

        // Избранное
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackPressed = { navController.popBackStack() },
                onProductClick = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                }
            )
        }

        // Уведомления - 👈 ОСТАВЛЯЕМ ТОЛЬКО ЭТОТ ОДИН БЛОК
        composable(Screen.Notifications.route) {
            NotificationScreen(
                onBackPressed = { navController.popBackStack() },
                onNotificationClick = { notification ->
                    notification.productCode?.let { productCode ->
                        navController.navigate(Screen.Product.passBarcode(productCode))
                    }
                }
            )
        }

        // Настройки
        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackPressed = { navController.popBackStack() }
            )
        }
    }
}