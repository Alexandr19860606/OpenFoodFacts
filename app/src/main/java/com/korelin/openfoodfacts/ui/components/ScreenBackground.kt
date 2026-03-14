package com.korelin.openfoodfacts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.korelin.openfoodfacts.ui.theme.Theme


enum class ScreenType {
    HOME, SCANNER, PRODUCT, FAVORITES, HISTORY, SETTINGS, NOTIFICATIONS, SEARCH
}

enum class BackgroundStyle {
    STATIC,      // Статичный градиент
    ANIMATED     // Анимированный градиент
}

@Composable
fun ScreenBackground(
    screenType: ScreenType,
    style: BackgroundStyle = BackgroundStyle.STATIC,
    content: @Composable () -> Unit
) {
    when (style) {
        BackgroundStyle.STATIC -> StaticScreenBackground(screenType, content)
        BackgroundStyle.ANIMATED -> AnimatedScreenBackground(
            style = AnimationStyle.LINEAR_GRADIENT,
            colors = getColorsForScreen(screenType),
            content = content
        )
    }
}

@Composable
private fun StaticScreenBackground(
    screenType: ScreenType,
    content: @Composable () -> Unit
) {
    val colors = Theme.colors
    val gradient = when (screenType) {
        ScreenType.HOME -> Brush.verticalGradient(
            colors = listOf(
                colors.primary.copy(alpha = 0.15f),
                colors.primary.copy(alpha = 0.05f),
                colors.background
            )
        )
        ScreenType.SCANNER -> Brush.linearGradient(
            colors = listOf(
                Color(0xFF1A237E).copy(alpha = 0.1f),
                Color(0xFF0D47A1).copy(alpha = 0.05f),
                colors.background
            ),
            start = Offset.Zero,
            end = Offset.Infinite
        )
        ScreenType.PRODUCT -> Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFF3E0).copy(alpha = 0.3f),
                Color(0xFFFFE0B2).copy(alpha = 0.2f),
                colors.background
            ),
            radius = 1000f
        )
        ScreenType.FAVORITES -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFFFF9C4).copy(alpha = 0.2f),
                Color(0xFFFFF176).copy(alpha = 0.1f),
                colors.background
            )
        )
        ScreenType.HISTORY -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFE1F5FE).copy(alpha = 0.3f),
                Color(0xFFB3E5FC).copy(alpha = 0.2f),
                colors.background
            )
        )
        ScreenType.SETTINGS -> Brush.radialGradient(
            colors = listOf(
                Color(0xFFE0E0E0).copy(alpha = 0.2f),
                Color(0xFFBDBDBD).copy(alpha = 0.1f),
                colors.background
            )
        )
        ScreenType.NOTIFICATIONS -> Brush.linearGradient(
            colors = listOf(
                Color(0xFFF3E5F5).copy(alpha = 0.2f),
                Color(0xFFE1BEE7).copy(alpha = 0.1f),
                colors.background
            ),
            start = Offset(0f, 0f),
            end = Offset(1000f, 1000f)
        )
        ScreenType.SEARCH -> Brush.horizontalGradient(
            colors = listOf(
                Color(0xFFE8EAF6).copy(alpha = 0.2f),
                Color(0xFFC5CAE9).copy(alpha = 0.1f),
                colors.background
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        content()
    }
}

@Composable
private fun getColorsForScreen(screenType: ScreenType): List<Color> {
    val colors = Theme.colors
    return when (screenType) {
        ScreenType.HOME -> listOf(
            colors.primary.copy(alpha = 0.2f),
            colors.primary.copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.SCANNER -> listOf(
            Color(0xFF1A237E).copy(alpha = 0.2f),
            Color(0xFF0D47A1).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.PRODUCT -> listOf(
            Color(0xFFFFF3E0).copy(alpha = 0.2f),
            Color(0xFFFFE0B2).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.FAVORITES -> listOf(
            Color(0xFFFFF9C4).copy(alpha = 0.2f),
            Color(0xFFFFF176).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.HISTORY -> listOf(
            Color(0xFFE1F5FE).copy(alpha = 0.2f),
            Color(0xFFB3E5FC).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.SETTINGS -> listOf(
            Color(0xFFE0E0E0).copy(alpha = 0.2f),
            Color(0xFFBDBDBD).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.NOTIFICATIONS -> listOf(
            Color(0xFFF3E5F5).copy(alpha = 0.2f),
            Color(0xFFE1BEE7).copy(alpha = 0.1f),
            colors.background
        )
        ScreenType.SEARCH -> listOf(
            Color(0xFFE8EAF6).copy(alpha = 0.2f),
            Color(0xFFC5CAE9).copy(alpha = 0.1f),
            colors.background
        )
    }
}

// Упрощенные функции для использования в экранах
@Composable
fun HomeScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.HOME,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun ScannerScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.SCANNER,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun ProductScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.PRODUCT,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun FavoritesScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.FAVORITES,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun HistoryScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.HISTORY,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun SettingsScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.SETTINGS,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun NotificationsScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.NOTIFICATIONS,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}

@Composable
fun SearchScreenBackground(
    animated: Boolean = false,
    content: @Composable () -> Unit
) {
    ScreenBackground(
        ScreenType.SEARCH,
        style = if (animated) BackgroundStyle.ANIMATED else BackgroundStyle.STATIC,
        content = content
    )
}