package com.korelin.openfoodfacts.utils

import com.korelin.openfoodfacts.data.model.ProductInfo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// ========== РАБОТА С ПРОДУКТАМИ ==========

/**
 * Безопасное получение штрих-кода продукта
 */
fun ProductInfo?.safeGetBarcode(): String? {
    return this?.code
}

/**
 * Безопасное получение названия продукта с дефолтным значением
 */
fun ProductInfo?.safeGetName(default: String = "Без названия"): String {
    return this?.product_name ?: default
}

/**
 * Безопасное получение бренда продукта с дефолтным значением
 */
fun ProductInfo?.safeGetBrand(default: String = "Неизвестный бренд"): String {
    return this?.brands ?: default
}

/**
 * Получение URL изображения продукта (приоритет: фронтальное > маленькое > обычное)
 */
fun ProductInfo?.getBestImageUrl(): String? {
    return this?.image_front_url ?: this?.image_front_small_url ?: this?.image_url
}

/**
 * Проверка, есть ли у продукта изображение
 */
fun ProductInfo?.hasImage(): Boolean {
    return !getBestImageUrl().isNullOrBlank()
}

/**
 * Форматирование пищевой ценности для отображения
 */
fun ProductInfo?.formatNutritionValue(quantity: Double?, unit: String?): String {
    return if (quantity != null) {
        if (unit.isNullOrBlank()) {
            String.format("%.1f", quantity)
        } else {
            String.format("%.1f %s", quantity, unit)
        }
    } else {
        "—"
    }
}

/**
 * Получение цвета для nutrition grade (A, B, C, D, E)
 */
fun ProductInfo?.getNutritionGradeColor(): Long {
    return when (this?.nutrition_grade_fr?.lowercase()) {
        "a" -> 0xFF4CAF50  // Зеленый
        "b" -> 0xFF8BC34A  // Светло-зеленый
        "c" -> 0xFFFFC107  // Желтый
        "d" -> 0xFFFF9800  // Оранжевый
        "e" -> 0xFFF44336  // Красный
        else -> 0xFF9E9E9E // Серый
    }
}

/**
 * Получение текстового описания для Nova группы (степень обработки)
 */
fun ProductInfo?.getNovaGroupDescription(): String {
    return when (this?.nova_group) {
        1 -> "Необработанные или минимально обработанные продукты"
        2 -> "Кулинарные ингредиенты"
        3 -> "Продукты, готовые к употреблению"
        4 -> "Ультра-обработанные продукты"
        else -> "Неизвестно"
    }
}

/**
 * Получение эмодзи для Eco-Score
 */
fun ProductInfo?.getEcoScoreEmoji(): String {
    return when (this?.ecoscore_grade?.lowercase()) {
        "a" -> "🟢"
        "b" -> "🟢"
        "c" -> "🟡"
        "d" -> "🟠"
        "e" -> "🔴"
        else -> "⚪"
    }
}

// ========== ФОРМАТИРОВАНИЕ ДАТ ==========

private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
private val dayFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

/**
 * Форматирование времени в зависимости от давности
 */
fun Long.formatTimestamp(): String {
    val now = System.currentTimeMillis()
    val diff = now - this

    return when {
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} мин назад"
        diff < 24 * 60 * 60 * 1000 -> timeFormat.format(Date(this))
        diff < 7 * 24 * 60 * 60 * 1000 -> dayFormat.format(Date(this))
        else -> dateFormat.format(Date(this))
    }
}

/**
 * Форматирование даты для истории
 */
fun Long.formatHistoryDate(): String {
    return dateFormat.format(Date(this))
}

// ========== ВАЛИДАЦИЯ ШТРИХ-КОДОВ ==========

/**
 * Проверка валидности штрих-кода (EAN-13)
 */
fun String.isValidBarcode(): Boolean {
    // EAN-13 должен содержать 13 цифр
    if (!matches(Regex("^\\d{13}$"))) return false

    // Проверка контрольной суммы
    val digits = map { it.toString().toInt() }
    val sum = digits.slice(0..11).mapIndexed { index, digit ->
        if (index % 2 == 0) digit else digit * 3
    }.sum()

    val checkDigit = (10 - (sum % 10)) % 10
    return checkDigit == digits[12]
}

/**
 * Форматирование штрих-кода для отображения (XXX-XXX-XXX-XXXX)
 */
fun String.formatBarcode(): String {
    return when (length) {
        13 -> "${substring(0..2)}-${substring(3..5)}-${substring(6..8)}-${substring(9..12)}"
        8 -> "${substring(0..3)}-${substring(4..7)}"
        else -> this
    }
}

// ========== РАБОТА СО СПИСКАМИ ==========

/**
 * Безопасное получение первого элемента списка
 */
fun <T> List<T>?.safeFirst(): T? {
    return this?.firstOrNull()
}

/**
 * Разделение списка на части по размеру
 */
fun <T> List<T>.chunkedBySize(chunkSize: Int): List<List<T>> {
    return if (isEmpty()) emptyList() else (0 until size step chunkSize)
        .map { subList(it, (it + chunkSize).coerceAtMost(size)) }
}

// ========== РАБОТА СО СТРОКАМИ ==========

/**
 * Ограничение длины строки с многоточием
 */
fun String?.limitLength(maxLength: Int, ellipsis: String = "..."): String {
    if (this == null) return ""
    return if (length <= maxLength) this else substring(0, maxLength - ellipsis.length) + ellipsis
}

/**
 * Капитализация первого символа
 */
fun String.capitalizeFirst(): String {
    return if (isEmpty()) this else replaceFirstChar { it.uppercase() }
}

// ========== РАБОТА С ЧИСЛАМИ ==========

/**
 * Форматирование числа с единицей измерения
 */
fun Double?.formatWithUnit(unit: String? = null): String {
    val value = this ?: return "—"
    val formatted = if (value % 1 == 0.0) {
        value.toInt().toString()
    } else {
        String.format("%.1f", value)
    }
    return if (unit.isNullOrBlank()) formatted else "$formatted $unit"
}

