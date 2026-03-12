package com.korelin.openfoodfacts.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.components.ProductCard
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun PopularSection(
    products: List<ProductInfo>,
    favoritesMap: Map<String, Boolean>,
    onProductClick: (ProductInfo) -> Unit,
    onFavoriteToggle: (ProductInfo, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.isEmpty()) return

    Column(modifier = modifier) {
        Text(
            text = "Популярные продукты",
            style = Theme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = products,
                key = { it.code ?: it.product_name ?: "" }
            ) { product ->
                ProductCard(
                    product = product,
                    isFavorite = favoritesMap[product.code] == true,
                    onFavoriteClick = {
                        onFavoriteToggle(product, favoritesMap[product.code] != true)
                    },
                    onClick = {
                        onProductClick(product)
                    }
                )
            }
        }
    }
}