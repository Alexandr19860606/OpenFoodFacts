package com.korelin.openfoodfacts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.korelin.openfoodfacts.data.model.Nutriments
import com.korelin.openfoodfacts.data.model.ProductInfo

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey
    val code: String,
    val productName: String?,
    val brand: String?,
    val imageUrl: String?,
    val quantity: String?,
    val categories: String?,
    val ingredientsText: String?,
    val nutrimentsJson: String?,
    val ecoscoreGrade: String?,
    val novaGroup: Int?,
    val nutritionGradeFr: String?,
    val isFavorite: Boolean = false,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toProductInfo(): ProductInfo {
        return ProductInfo(
            code = code,
            product_name = productName,
            brands = brand,
            image_url = imageUrl,
            quantity = quantity,
            categories = categories,
            ingredients_text = ingredientsText,
            nutriments = nutrimentsJson?.let {
                Gson().fromJson(it, Nutriments::class.java)
            },
            ecoscore_grade = ecoscoreGrade,
            nova_group = novaGroup,
            nutrition_grade_fr = nutritionGradeFr
        )
    }

    companion object {
        fun fromProductInfo(product: ProductInfo): ProductEntity {
            return ProductEntity(
                code = product.code ?: "",
                productName = product.product_name,
                brand = product.brands,
                imageUrl = product.image_front_url ?: product.image_url,
                quantity = product.quantity,
                categories = product.categories,
                ingredientsText = product.ingredients_text,
                nutrimentsJson = product.nutriments?.let {
                    Gson().toJson(it)
                },
                ecoscoreGrade = product.ecoscore_grade,
                novaGroup = product.nova_group,
                nutritionGradeFr = product.nutrition_grade_fr
            )
        }
    }
}