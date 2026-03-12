package com.korelin.openfoodfacts.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun NewSection(
    products: List<ProductInfo>,
    favoritesMap: Map<String, Boolean>,
    onProductClick: (ProductInfo) -> Unit,
    onFavoriteToggle: (ProductInfo, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "Новинки",
            style = Theme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            products.forEach { product ->
                NewSectionItem(
                    product = product,
                    isFavorite = favoritesMap[product.code] == true,
                    onFavoriteClick = {
                        onFavoriteToggle(product, favoritesMap[product.code] != true)
                    },
                    onClick = { onProductClick(product) }
                )
            }
        }
    }
}

@Composable
fun NewSectionItem(
    product: ProductInfo,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CustomShapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Изображение
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CustomShapes.medium)
            ) {
                val imageUrl = product.image_small_url
                    ?: product.image_front_small_url
                    ?: product.image_url

                if (!imageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = product.product_name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Theme.colors.primaryContainer
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("🥫", style = Theme.typography.titleLarge)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.product_name ?: "Без названия",
                    style = Theme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = product.brands ?: "Неизвестный бренд",
                    style = Theme.typography.bodySmall,
                    color = Theme.colors.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Иконка избранного
            IconButton(
                onClick = onFavoriteClick,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite)
                        androidx.compose.material.icons.Icons.Default.Favorite
                    else
                        androidx.compose.material.icons.Icons.Default.FavoriteBorder,
                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                    tint = if (isFavorite) Theme.colors.primary else Theme.colors.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}