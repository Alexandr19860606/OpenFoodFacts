package com.korelin.openfoodfacts.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material You скругления
// Маленькие: 4dp - кнопки, чипсы
// Средние: 8-16dp - карточки
// Большие: 24-32dp - диалоги, большие карточки

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // Маленькие кнопки, индикаторы
    small = RoundedCornerShape(8.dp),        // Чипсы, маленькие карточки
    medium = RoundedCornerShape(16.dp),      // Обычные карточки
    large = RoundedCornerShape(24.dp),       // Диалоги, большие кнопки
    extraLarge = RoundedCornerShape(32.dp)   // Главная карточка, BottomSheet
)

// Дополнительные кастомные скругления (для удобства)
object CustomShapes {
    val none = RoundedCornerShape(0.dp)
    val extraSmall = RoundedCornerShape(4.dp)
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)
    val extraLarge = RoundedCornerShape(20.dp)
    val xxLarge = RoundedCornerShape(24.dp)
    val xxxLarge = RoundedCornerShape(28.dp)
    val full = RoundedCornerShape(50.dp)     // Полностью круглые
}