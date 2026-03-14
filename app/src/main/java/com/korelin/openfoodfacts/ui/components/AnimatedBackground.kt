package com.korelin.openfoodfacts.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Анимированный градиентный фон
 * @param colors список цветов для градиента (минимум 2)
 * @param animationDuration длительность полного цикла анимации в миллисекундах
 * @param content контент поверх фона
 */
@Composable
fun AnimatedGradientBackground(
    colors: List<Color>,
    animationDuration: Int = 10000,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()

    // Анимируем позиции градиента
    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing)
        )
    )

    val offset2 by infiniteTransition.animateFloat(
        initialValue = 1000f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = LinearEasing)
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = colors,
                    start = Offset(offset1, offset1),
                    end = Offset(offset2, offset2)
                )
            )
    ) {
        content()
    }
}

/**
 * Анимированный радиальный градиент
 */
@Composable
fun AnimatedRadialGradientBackground(
    colors: List<Color>,
    animationDuration: Int = 8000,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.toFloat()

    val radius by infiniteTransition.animateFloat(
        initialValue = 100f,
        targetValue = screenWidth * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val centerX by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = screenWidth + 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(animationDuration, easing = FastOutLinearInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.radialGradient(
                    colors = colors,
                    center = Offset(centerX, 0f),
                    radius = radius
                )
            )
    ) {
        content()
    }
}

/**
 * Упрощенный анимированный фон (без волн, которые вызывали предупреждения)
 */
enum class AnimationStyle {
    LINEAR_GRADIENT,
    RADIAL_GRADIENT
}

@Composable
fun AnimatedScreenBackground(
    style: AnimationStyle = AnimationStyle.LINEAR_GRADIENT,
    colors: List<Color> = listOf(
        Color(0xFF4A90E2).copy(alpha = 0.3f),
        Color(0xFF9013FE).copy(alpha = 0.2f),
        Color(0xFFF5A623).copy(alpha = 0.1f)
    ),
    content: @Composable () -> Unit
) {
    when (style) {
        AnimationStyle.LINEAR_GRADIENT -> {
            AnimatedGradientBackground(colors = colors) {
                content()
            }
        }
        AnimationStyle.RADIAL_GRADIENT -> {
            AnimatedRadialGradientBackground(colors = colors) {
                content()
            }
        }
    }
}