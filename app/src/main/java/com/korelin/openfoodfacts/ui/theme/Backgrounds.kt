package com.korelin.openfoodfacts.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

object Backgrounds {

    // Градиент для главного экрана (продуктовый)
    val homeGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE8F5E9).copy(alpha = 0.5f),
            Color(0xFFC8E6C9).copy(alpha = 0.3f),
            Color.Transparent
        )
    )

    // Градиент для сканера (технологичный)
    val scannerGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A237E).copy(alpha = 0.1f),
            Color(0xFF0D47A1).copy(alpha = 0.05f),
            Color.Transparent
        ),
        start = Offset.Zero,
        end = Offset.Infinite
    )

    // Градиент для деталей продукта (информационный)
    val productGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFF3E0).copy(alpha = 0.3f),
            Color(0xFFFFE0B2).copy(alpha = 0.2f),
            Color.Transparent
        ),
        radius = 1000f
    )

    // Градиент для избранного (звездный)
    val favoritesGradient = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFF9C4).copy(alpha = 0.2f),
            Color(0xFFFFF176).copy(alpha = 0.1f),
            Color.Transparent
        )
    )

    // Градиент для истории (часы/время)
    val historyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFE1F5FE).copy(alpha = 0.3f),
            Color(0xFFB3E5FC).copy(alpha = 0.2f),
            Color.Transparent
        )
    )

    // Градиент для настроек (шестеренки)
    val settingsGradient = Brush.radialGradient(
        colors = listOf(
            Color(0xFFE0E0E0).copy(alpha = 0.2f),
            Color(0xFFBDBDBD).copy(alpha = 0.1f),
            Color.Transparent
        )
    )

    // Градиент для уведомлений (колокольчики)
    val notificationsGradient = Brush.linearGradient(
        colors = listOf(
            Color(0xFFF3E5F5).copy(alpha = 0.2f),
            Color(0xFFE1BEE7).copy(alpha = 0.1f),
            Color.Transparent
        ),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f)
    )

    // Упрощенный FoodPattern без неиспользуемых переменных
    @Composable
    fun FoodPattern(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFF8E1).copy(alpha = 0.3f),
                            Color(0xFFFFECB3).copy(alpha = 0.2f)
                        )
                    )
                )
        )
    }

    // Упрощенный StarPattern
    @Composable
    fun StarPattern(modifier: Modifier = Modifier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF9C4).copy(alpha = 0.2f),
                            Color(0xFFFFF176).copy(alpha = 0.1f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}