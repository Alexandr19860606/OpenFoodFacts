package com.korelin.openfoodfacts.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Scanner : Screen("scanner")
    object Product : Screen("product/{barcode}") {
        fun passBarcode(barcode: String) = "product/$barcode"
    }
    object Search : Screen("search/{query}") {
        fun passQuery(query: String) = "search/$query"
    }
    object History : Screen("history")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}