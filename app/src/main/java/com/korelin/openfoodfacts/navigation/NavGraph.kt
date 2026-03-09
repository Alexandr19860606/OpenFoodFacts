package com.korelin.openfoodfacts.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.korelin.openfoodfacts.ui.screens.favorites.FavoritesScreen
import com.korelin.openfoodfacts.ui.screens.history.HistoryScreen
import com.korelin.openfoodfacts.ui.screens.product.ProductScreen
import com.korelin.openfoodfacts.ui.screens.scanner.ScannerScreen
import com.korelin.openfoodfacts.ui.screens.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scanner.route,
        modifier = modifier
    ) {
        composable(Screen.Scanner.route) {
            ScannerScreen(
                onBarcodeScanned = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToFavorites = {
                    navController.navigate(Screen.Favorites.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Product.route) { backStackEntry ->
            val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
            ProductScreen(
                barcode = barcode,
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onProductClick = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                }
            )
        }

        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackPressed = {
                    navController.popBackStack()
                },
                onProductClick = { barcode ->
                    navController.navigate(Screen.Product.passBarcode(barcode))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
    }
}