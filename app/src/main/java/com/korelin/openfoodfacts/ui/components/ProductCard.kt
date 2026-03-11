package com.korelin.openfoodfacts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun ProductCard(
    product: ProductInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        shape = CustomShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Placeholder для изображения
            Surface(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                shape = CustomShapes.medium,
                color = Theme.colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🥫",
                        style = Theme.typography.displaySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.product_name ?: "Без названия",
                style = Theme.typography.bodyLarge,
                maxLines = 2
            )

            Text(
                text = product.brands ?: "Неизвестный бренд",
                style = Theme.typography.bodySmall,
                color = Theme.colors.onSurfaceVariant
            )
        }
    }
}