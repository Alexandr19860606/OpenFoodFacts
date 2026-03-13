package com.korelin.openfoodfacts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun ProductCard(
    product: ProductInfo,
    isFavorite: Boolean = false,
    onFavoriteClick: (() -> Unit)? = null,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Получаем context и colors на уровне композиции
    val context = LocalContext.current
    val colors = Theme.colors

    // Кэшируем вычисления названия
    val displayName = remember(product.product_name) {
        product.product_name?.takeIf { it.isNotBlank() } ?: "Без названия"
    }

    // Кэшируем бренд
    val displayBrand = remember(product.brands) {
        product.brands?.takeIf { it.isNotBlank() } ?: "Неизвестный бренд"
    }

    // Кэшируем URL изображения
    val imageUrl = remember(product) {
        product.image_front_small_url
            ?: product.image_front_url
            ?: product.image_url
    }

    // Используем colors, полученный выше
    val favoriteTint = remember(isFavorite, colors) {
        if (isFavorite) colors.primary else colors.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        shape = CustomShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Изображение продукта
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CustomShapes.medium)
                ) {
                    if (!imageUrl.isNullOrEmpty()) {
                        val imageRequest = remember(imageUrl, context) {
                            ImageRequest.Builder(context)
                                .data(imageUrl)
                                .crossfade(true)
                                .build()
                        }

                        AsyncImage(
                            model = imageRequest,
                            contentDescription = displayName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            color = colors.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "🥫",
                                    style = Theme.typography.displaySmall
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = displayName,
                    style = Theme.typography.bodyLarge,
                    maxLines = 2,
                    minLines = 2
                )

                Text(
                    text = displayBrand,
                    style = Theme.typography.bodySmall,
                    color = colors.onSurfaceVariant
                )
            }

            // Иконка избранного
            if (onFavoriteClick != null) {
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                        tint = favoriteTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}