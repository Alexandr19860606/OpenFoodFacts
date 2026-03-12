package com.korelin.openfoodfacts.utils

import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.data.model.Nutriments

// Расширения специфичные для продукта
fun ProductInfo.getNutritionTable(): Map<String, String> {
    val nutriments = this.nutriments ?: return emptyMap()

    return mutableMapOf<String, String>().apply {
        put("Энергия", nutriments.energy_kcal_100g?.formatWithUnit("ккал") ?: "—")
        put("Жиры", nutriments.fat_100g?.formatWithUnit(nutriments.fat_unit) ?: "—")
        put("Насыщенные жиры", nutriments.saturated_fat_100g?.formatWithUnit(nutriments.saturated_fat_unit) ?: "—")
        put("Углеводы", nutriments.carbohydrates_100g?.formatWithUnit(nutriments.carbohydrates_unit) ?: "—")
        put("Сахара", nutriments.sugars_100g?.formatWithUnit(nutriments.sugars_unit) ?: "—")
        put("Клетчатка", nutriments.fiber_100g?.formatWithUnit(nutriments.fiber_unit) ?: "—")
        put("Белки", nutriments.proteins_100g?.formatWithUnit(nutriments.proteins_unit) ?: "—")
        put("Соль", nutriments.salt_100g?.formatWithUnit(nutriments.salt_unit) ?: "—")
        put("Натрий", nutriments.sodium_100g?.formatWithUnit(nutriments.sodium_unit) ?: "—")
    }
}

fun ProductInfo.getAllergensList(): List<String> {
    return allergens_tags ?: emptyList()
}

fun ProductInfo.getIngredientsList(): List<String> {
    return ingredients_tags ?: emptyList()
}

fun ProductInfo.hasAllergens(): Boolean {
    return !getAllergensList().isEmpty()
}

fun ProductInfo.getProductUrl(): String {
    return "https://world.openfoodfacts.org/product/${code ?: ""}"
}