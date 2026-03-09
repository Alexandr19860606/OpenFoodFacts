package com.korelin.openfoodfacts.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

// Централизованное хранилище всех иконок приложения
object AppIcons {

    // ==================== НАВИГАЦИЯ ====================
    val Scanner = Icons.Filled.QrCodeScanner
    val ScannerOutlined = Icons.Outlined.QrCodeScanner


    val History = Icons.Filled.Schedule
    val HistoryOutlined = Icons.Outlined.Schedule


    val Favorites = Icons.Filled.Star
    val FavoritesOutlined = Icons.Outlined.Star
    val FavoritesBorder = Icons.Filled.StarBorder

    val Settings = Icons.Filled.Settings
    val SettingsOutlined = Icons.Outlined.Settings

    val Back = Icons.AutoMirrored.Filled.ArrowBack

    // ==================== ДЕЙСТВИЯ ====================
    val Search = Icons.Filled.Search
    val SearchOutlined = Icons.Outlined.Search

    val Add = Icons.Filled.Add
    val AddOutlined = Icons.Outlined.Add

    val Close = Icons.Filled.Close
    val CloseOutlined = Icons.Outlined.Close

    val Delete = Icons.Filled.Delete
    val DeleteOutlined = Icons.Outlined.Delete

    val Edit = Icons.Filled.Edit
    val EditOutlined = Icons.Outlined.Edit

    val Share = Icons.Filled.Share
    val ShareOutlined = Icons.Outlined.Share

    val More = Icons.Filled.MoreVert
    val MoreOutlined = Icons.Outlined.MoreVert

    val Check = Icons.Filled.Check
    val CheckOutlined = Icons.Outlined.Check

    val Error = Icons.Filled.Error
    val ErrorOutlined = Icons.Outlined.Error

    val Info = Icons.Filled.Info
    val InfoOutlined = Icons.Outlined.Info

    val Warning = Icons.Filled.Warning
    val WarningOutlined = Icons.Outlined.Warning

    // ==================== ПРОДУКТЫ ====================
    val Product = Icons.Filled.ShoppingBasket
    val ProductOutlined = Icons.Outlined.ShoppingBasket

    val Category = Icons.Filled.Category
    val CategoryOutlined = Icons.Outlined.Category

    val Brand = Icons.Filled.Business
    val BrandOutlined = Icons.Outlined.Business

    val Barcode = Icons.Filled.QrCode
    val BarcodeOutlined = Icons.Outlined.QrCode

    val Ingredients = Icons.Filled.ListAlt
    val IngredientsOutlined = Icons.Outlined.ListAlt

    val Nutrition = Icons.Filled.FoodBank
    val NutritionOutlined = Icons.Outlined.FoodBank

    val Allergens = Icons.Filled.WarningAmber
    val AllergensOutlined = Icons.Outlined.WarningAmber

    // ==================== КАМЕРА ====================
    val Camera = Icons.Filled.Camera
    val CameraOutlined = Icons.Outlined.Camera

    val FlashOn = Icons.Filled.FlashOn
    val FlashOff = Icons.Filled.FlashOff
    val FlashAuto = Icons.Filled.FlashAuto

    // ==================== UI ЭЛЕМЕНТЫ ====================
    val DarkMode = Icons.Filled.DarkMode
    val DarkModeOutlined = Icons.Outlined.DarkMode

    val LightMode = Icons.Filled.LightMode
    val LightModeOutlined = Icons.Outlined.LightMode

    val Home = Icons.Filled.Home
    val HomeOutlined = Icons.Outlined.Home

    val Person = Icons.Filled.Person
    val PersonOutlined = Icons.Outlined.Person

    val Refresh = Icons.Filled.Refresh
    val RefreshOutlined = Icons.Outlined.Refresh
}

// Карта иконок для Bottom Navigation
data class NavBarIconSet(
    val selected: ImageVector,
    val unselected: ImageVector,
    val title: String
)

val BottomNavItems = listOf(
    NavBarIconSet(
        selected = AppIcons.Scanner,
        unselected = AppIcons.ScannerOutlined,
        title = "Сканер"
    ),
    NavBarIconSet(
        selected = AppIcons.History,
        unselected = AppIcons.HistoryOutlined,
        title = "История"
    ),
    NavBarIconSet(
        selected = AppIcons.Favorites,
        unselected = AppIcons.FavoritesOutlined,
        title = "Избранное"
    ),
    NavBarIconSet(
        selected = AppIcons.Settings,
        unselected = AppIcons.SettingsOutlined,
        title = "Настройки"
    )
)