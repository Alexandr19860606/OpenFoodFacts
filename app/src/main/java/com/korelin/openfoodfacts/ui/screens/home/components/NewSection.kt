package com.korelin.openfoodfacts.ui.screens.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun NewSection(
    products: List<ProductInfo>,
    onProductClick: (ProductInfo) -> Unit,
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

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(products) { product ->
                Card(
                    onClick = {
                        onProductClick(product)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = CustomShapes.medium
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CustomShapes.medium,
                            color = Theme.colors.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text("🥫", style = Theme.typography.titleLarge)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = product.product_name ?: "Без названия",
                                style = Theme.typography.bodyLarge
                            )
                            Text(
                                text = product.brands ?: "Неизвестный бренд",
                                style = Theme.typography.bodySmall,
                                color = Theme.colors.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}