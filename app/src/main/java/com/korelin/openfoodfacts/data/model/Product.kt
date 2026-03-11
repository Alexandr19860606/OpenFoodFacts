package com.korelin.openfoodfacts.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.gson.annotations.SerializedName


@Parcelize
data class Nutriments(

    @SerializedName("energy_100g")
    val energy_100g: Double? = null,
    @SerializedName("energy_kcal_100g")
    val energy_kcal_100g: Double? = null,
    @SerializedName("energy_unit")
    val energy_unit: String? = null,
    @SerializedName("fat_100g")
    val fat_100g: Double? = null,
    @SerializedName("fat_unit")
    val fat_unit: String? = null,
    @SerializedName("saturated_fat_100g")
    val saturated_fat_100g: Double? = null,
    @SerializedName("saturated_fat_unit")
    val saturated_fat_unit: String? = null,
    @SerializedName("carbohydrates_100g")
    val carbohydrates_100g: Double? = null,
    @SerializedName("carbohydrates_unit")
    val carbohydrates_unit: String? = null,
    @SerializedName("sugars_100g")
    val sugars_100g: Double? = null,
    @SerializedName("sugars_unit")
    val sugars_unit: String? = null,
    @SerializedName("fiber_100g")
    val fiber_100g: Double? = null,
    @SerializedName("fiber_unit")
    val fiber_unit: String? = null,
    @SerializedName("proteins_100g")
    val proteins_100g: Double? = null,
    @SerializedName("proteins_unit")
    val proteins_unit: String? = null,
    @SerializedName("salt_100g")
    val salt_100g: Double? = null,
    @SerializedName("salt_unit")
    val salt_unit: String? = null,
    @SerializedName("sodium_100g")
    val sodium_100g: Double? = null,
    @SerializedName("sodium_unit")
    val sodium_unit: String? = null
) : Parcelable


@Parcelize
data class NutrientLevels(
    val fat: String? = null,
    @SerializedName("saturated_fat")
    val saturated_fat: String? = null,
    val sugars: String? = null,
    val salt: String? = null
) : Parcelable


@Parcelize
data class ProductInfo(
    @SerializedName("code")
    val code: String? = null,

    @SerializedName("product_name")
    val product_name: String? = null,
    @SerializedName("product_name_fr")
    val product_name_fr: String? = null,
    val brands: String? = null,
    @SerializedName("brands_tags")
    val brands_tags: List<String>? = null,
    val categories: String? = null,
    @SerializedName("categories_tags")
    val categories_tags: List<String>? = null,
    @SerializedName("ingredients_text")
    val ingredients_text: String? = null,
    @SerializedName("ingredients_text_fr")
    val ingredients_text_fr: String? = null,
    @SerializedName("ingredients_tags")
    val ingredients_tags: List<String>? = null,
    val nutriments: Nutriments? = null,
    @SerializedName("image_url")
    val image_url: String? = null,
    @SerializedName("image_small_url")
    val image_small_url: String? = null,
    @SerializedName("image_front_url")
    val image_front_url: String? = null,
    @SerializedName("image_front_small_url")
    val image_front_small_url: String? = null,
    val allergens: String? = null,
    @SerializedName("allergens_tags")
    val allergens_tags: List<String>? = null,
    @SerializedName("allergens_from_ingredients")
    val allergens_from_ingredients: String? = null,
    val traces: String? = null,
    @SerializedName("traces_tags")
    val traces_tags: List<String>? = null,
    val quantity: String? = null,
    val packaging: String? = null,
    @SerializedName("packaging_tags")
    val packaging_tags: List<String>? = null,
    @SerializedName("manufacturing_places")
    val manufacturing_places: String? = null,
    val labels: String? = null,
    @SerializedName("labels_tags")
    val labels_tags: List<String>? = null,
    val origins: String? = null,
    val countries: String? = null,
    @SerializedName("countries_tags")
    val countries_tags: List<String>? = null,
    @SerializedName("nutrient_levels")
    val nutrient_levels: NutrientLevels? = null,
    @SerializedName("ecoscore_grade")
    val ecoscore_grade: String? = null,
    @SerializedName("ecoscore_score")
    val ecoscore_score: Int? = null,
    @SerializedName("nova_group")
    val nova_group: Int? = null,
    @SerializedName("nutrition_grade_fr")
    val nutrition_grade_fr: String? = null
) : Parcelable


// ProductResponse
@Parcelize
data class ProductResponse(
    val code: String? = null,
    val product: ProductInfo? = null,
    @SerializedName("status_verbose")
    val status_verbose: String? = null
) : Parcelable


@Parcelize
data class SearchResponse(
    val count: Int? = null,
    val page: Int? = null,
    @SerializedName("page_count")
    val page_count: Int? = null,
    @SerializedName("page_size")
    val page_size: Int? = null,
    val products: List<ProductInfo>? = null
) : Parcelable


@Parcelize
data class CategoriesResponse(
    val count: Int = 0,
    val tags: List<Category> = emptyList()
) : Parcelable


@Parcelize
data class Category(
    val id: String = "",
    val name: String = "",
    @SerializedName("products_count")
    val products_count: Int = 0,
    val url: String = ""
) : Parcelable