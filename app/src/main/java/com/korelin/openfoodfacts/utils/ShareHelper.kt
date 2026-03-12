package com.korelin.openfoodfacts.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.korelin.openfoodfacts.data.model.ProductInfo
import java.io.File
import java.io.FileOutputStream

object ShareHelper {

    fun shareProduct(context: Context, product: ProductInfo) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, generateShareText(product))
            putExtra(Intent.EXTRA_SUBJECT, product.product_name ?: "Продукт")
        }
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться продуктом"))
    }

    fun shareProductWithImage(context: Context, product: ProductInfo, imageUri: Uri?) {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/*"
            putExtra(Intent.EXTRA_TEXT, generateShareText(product))
            putExtra(Intent.EXTRA_SUBJECT, product.product_name ?: "Продукт")
            imageUri?.let { putExtra(Intent.EXTRA_STREAM, it) }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(Intent.createChooser(shareIntent, "Поделиться продуктом"))
    }

    private fun generateShareText(product: ProductInfo): String {
        return """
            🥫 ${product.product_name ?: "Продукт"}
            
            Бренд: ${product.brands ?: "Неизвестен"}
            Штрих-код: ${product.code ?: "Н/Д"}
            
            Подробнее: https://world.openfoodfacts.org/product/${product.code ?: ""}
            
            Открыто в Open Food Facts
        """.trimIndent()
    }
}