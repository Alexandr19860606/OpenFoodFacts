package com.korelin.openfoodfacts

// Product.kt
data class ProductResponse(
    val code: String?,          // Штрих-код продукта
    val product: ProductInfo?,   // Объект с детальной информацией
    val status_verbose: String? // Статус запроса (например, "product found")
)

data class ProductInfo(
    val product_name: String?,          // Название продукта (на языке запроса)
    val brands: String?,                // Бренд
    val categories: String?,            // Категории (строкой через запятую)
    val ingredients_text: String?,      // Список ингредиентов
    val nutriments: Nutriments?,        // Объект с нутриентами (может быть null)
    val image_url: String?              // Ссылка на изображение продукта
)

data class Nutriments(
    val energy_100g: Int?,          // Энергия на 100г (в кДж или ккал, смотрите единицы!)
    val fat_100g: Double?,           // Жиры на 100г
    val carbohydrates_100g: Double?, // Углеводы на 100г
    val proteins_100g: Double?,      // Белки на 100г
    val salt_100g: Double?           // Соль на 100г
)

// Оставляем SearchResponse ТОЛЬКО ЗДЕСЬ
data class SearchResponse(
    val count: Int?, // Общее количество найденных продуктов
    val page: Int?,
    val page_count: Int?,
    val page_size: Int?,
    val products: List<ProductInfo>? // Список найденных продуктов
)