package com.korelin.openfoodfacts.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.korelin.openfoodfacts.ui.theme.AppIcons
import com.korelin.openfoodfacts.ui.theme.Theme

data class BottomNavItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem(
        route = "scanner",
        title = "Сканер",
        selectedIcon = AppIcons.Scanner,
        unselectedIcon = AppIcons.ScannerOutlined
    ),
    BottomNavItem(
        route = "history",
        title = "История",
        selectedIcon = AppIcons.History,
        unselectedIcon = AppIcons.HistoryOutlined
    ),
    BottomNavItem(
        route = "favorites",
        title = "Избранное",
        selectedIcon = AppIcons.Favorites,
        unselectedIcon = AppIcons.FavoritesOutlined
    ),
    BottomNavItem(
        route = "notifications",
        title = "Уведомления",
        selectedIcon = Icons.Default.Notifications,
        unselectedIcon = Icons.Default.NotificationsNone
    ),
    BottomNavItem(
        route = "settings",
        title = "Настройки",
        selectedIcon = AppIcons.Settings,
        unselectedIcon = AppIcons.SettingsOutlined
    )
)
@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        modifier = modifier,
        containerColor = Theme.colors.surface,
        tonalElevation = 8.dp,
        contentColor = Theme.colors.onSurface
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = Theme.typography.labelMedium
                    )
                },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Theme.colors.primary,
                    selectedTextColor = Theme.colors.primary,
                    unselectedIconColor = Theme.colors.onSurfaceVariant,
                    unselectedTextColor = Theme.colors.onSurfaceVariant,
                    indicatorColor = Theme.colors.secondaryContainer
                )
            )
        }
    }
}