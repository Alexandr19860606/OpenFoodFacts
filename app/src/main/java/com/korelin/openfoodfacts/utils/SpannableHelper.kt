package com.korelin.openfoodfacts.utils

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import com.korelin.openfoodfacts.ui.theme.Theme

object SpannableHelper {

    fun createStyledNutritionText(
        label: String,
        value: String,
        unit: String = ""
    ): androidx.compose.ui.text.AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append(label)
            }
            append(": ")
            append(value)
            if (unit.isNotBlank()) {
                append(" $unit")
            }
        }
    }

    fun createIngredientText(
        ingredients: List<String>,
        allergens: List<String>
    ): androidx.compose.ui.text.AnnotatedString {
        return buildAnnotatedString {
            ingredients.forEachIndexed { index, ingredient ->
                val isAllergen = allergens.any { allergen ->
                    ingredient.contains(allergen.replace("en:", ""), ignoreCase = true)
                }

                if (isAllergen) {
                    withStyle(style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color(0xFFBA1A1A)
                    )) {
                        append(ingredient)
                    }
                } else {
                    append(ingredient)
                }

                if (index < ingredients.size - 1) {
                    append(", ")
                }
            }
        }
    }

    fun createProductTitle(
        name: String,
        brand: String?
    ): androidx.compose.ui.text.AnnotatedString {
        return buildAnnotatedString {
            withStyle(style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )) {
                append(name)
            }
            if (!brand.isNullOrBlank()) {
                append("\n")
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Normal,
                    fontSize = 16.sp,
                    color = androidx.compose.ui.graphics.Color.Gray
                )) {
                    append(brand)
                }
            }
        }
    }
}

// Для совместимости с Android View системой (если понадобится)
fun getStyledText(label: String, value: String): SpannableString {
    val text = "$label: $value"
    val spannableString = SpannableString(text)

    val start = 0
    val end = label.length + 1

    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        start,
        end,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannableString
}