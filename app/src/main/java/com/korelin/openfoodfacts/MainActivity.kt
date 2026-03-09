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
import com.korelin.openfoodfacts.ui.screens.product.ProductScreen
import com.korelin.openfoodfacts.ui.screens.scanner.ScannerScreen
import com.korelin.openfoodfacts.ui.screens.settings.SettingsScreen
import com.korelin.openfoodfacts.ui.theme.OpenFoodFactsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OpenFoodFactsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    // Вызываем AppNavigation напрямую
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "scanner",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("scanner") {
                ScannerScreen(
                    onBarcodeScanned = { barcode ->
                        navController.navigate("product/$barcode")
                    },
                    onNavigateToHistory = {
                        navController.navigate("history")
                    },
                    onNavigateToFavorites = {
                        navController.navigate("favorites")
                    },
                    onNavigateToSettings = {
                        navController.navigate("settings")
                    }
                )
            }

            composable("product/{barcode}") { backStackEntry ->
                val barcode = backStackEntry.arguments?.getString("barcode") ?: ""
                ProductScreen(
                    barcode = barcode,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }

            composable("history") {
                HistoryScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onProductClick = { barcode ->
                        navController.navigate("product/$barcode")
                    }
                )
            }

            composable("favorites") {
                FavoritesScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    },
                    onProductClick = { barcode ->
                        navController.navigate("product/$barcode")
                    }
                )
            }

            composable("settings") {
                SettingsScreen(
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}