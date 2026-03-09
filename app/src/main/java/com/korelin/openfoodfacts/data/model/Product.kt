package com.korelin.openfoodfacts.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductResponse(
    val code: String?,
    val product: ProductInfo?,
    val status_verbose: String?
) : Parcelable

@Parcelize
data class ProductInfo(
    val product_name: String?,
    val product_name_fr: String?,
    val brands: String?,
    val brands_tags: List<String>?,
    val categories: String?,
    val categories_tags: List<String>?,
    val ingredients_text: String?,
    val ingredients_text_fr: String?,
    val ingredients_tags: List<String>?,
    val nutriments: Nutriments?,
    val image_url: String?,
    val image_small_url: String?,
    val image_front_url: String?,
    val image_front_small_url: String?,
    val allergens: String?,
    val allergens_tags: List<String>?,
    val allergens_from_ingredients: String?,
    val traces: String?,
    val traces_tags: List<String>?,
    val quantity: String?,
    val packaging: String?,
    val packaging_tags: List<String>?,
    val manufacturing_places: String?,
    val labels: String?,
    val labels_tags: List<String>?,
    val origins: String?,
    val countries: String?,
    val countries_tags: List<String>?,
    val nutrient_levels: NutrientLevels?,
    val ecoscore_grade: String?,
    val ecoscore_score: Int?,
    val nova_group: Int?,
    val nutrition_grade_fr: String?
) : Parcelable

@Parcelize
data class Nutriments(
    val energy_100g: Int?,
    val energy_kcal_100g: Double?,
    val energy_unit: String?,
    val fat_100g: Double?,
    val fat_unit: String?,
    val saturated_fat_100g: Double?,
    val saturated_fat_unit: String?,
    val carbohydrates_100g: Double?,
    val carbohydrates_unit: String?,
    val sugars_100g: Double?,
    val sugars_unit: String?,
    val fiber_100g: Double?,
    val fiber_unit: String?,
    val proteins_100g: Double?,
    val proteins_unit: String?,
    val salt_100g: Double?,
    val salt_unit: String?,
    val sodium_100g: Double?,
    val sodium_unit: String?
) : Parcelable

@Parcelize
data class NutrientLevels(
    val fat: String?,
    val saturated_fat: String?,
    val sugars: String?,
    val salt: String?
) : Parcelable

@Parcelize
data class SearchResponse(
    val count: Int?,
    val page: Int?,
    val page_count: Int?,
    val page_size: Int?,
    val products: List<ProductInfo>?
) : Parcelable