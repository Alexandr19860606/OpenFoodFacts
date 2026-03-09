package com.korelin.openfoodfacts.navigation

sealed class Screen(val route: String) {
    object Scanner : Screen("scanner")
    object Product : Screen("product/{barcode}") {
        fun passBarcode(barcode: String): String = "product/$barcode"
    }
    object History : Screen("history")
    object Favorites : Screen("favorites")
    object Settings : Screen("settings")
}

// Иконки для Bottom Navigation
sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: Int,
    val selectedIcon: Int
) {
    object Scanner : BottomNavItem(
        route = "scanner",
        title = "Сканер",
        icon = android.R.drawable.ic_menu_camera,
        selectedIcon = android.R.drawable.ic_menu_camera
    )

    object History : BottomNavItem(
        route = "history",
        title = "История",
        icon = android.R.drawable.ic_menu_recent_history,
        selectedIcon = android.R.drawable.ic_menu_recent_history
    )

    object Favorites : BottomNavItem(
        route = "favorites",
        title = "Избранное",
        icon = android.R.drawable.btn_star,
        selectedIcon = android.R.drawable.btn_star_big_on
    )

    object Settings : BottomNavItem(
        route = "settings",
        title = "Настройки",
        icon = android.R.drawable.ic_menu_manage,
        selectedIcon = android.R.drawable.ic_menu_manage
    )
}