package com.korelin.openfoodfacts.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@Composable
fun ProductCard(
    name: String,
    brand: String,
    barcode: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface,
            contentColor = Theme.colors.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Иконка-заглушка (позже заменим на изображение)
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CustomShapes.medium,
                    color = Theme.colors.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "🥫",
                            style = Theme.typography.titleLarge
                        )
                    }
                }

                Column {
                    Text(
                        text = name,
                        style = Theme.typography.bodyLarge,
                        color = Theme.colors.onSurface
                    )
                    Text(
                        text = brand,
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.onSurfaceVariant
                    )
                    Text(
                        text = barcode,
                        style = Theme.typography.labelSmall,
                        color = Theme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            trailingContent?.invoke()
        }
    }
}

@Composable
fun NutritionCard(
    energy: String,
    fat: String,
    carbs: String,
    protein: String,
    salt: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.xxLarge,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Пищевая ценность на 100г",
                style = Theme.typography.titleMedium,
                color = Theme.colors.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(16.dp))

            NutritionBar(
                label = "Энергия",
                value = energy,
                percentage = 1f,
                color = Theme.colors.primary
            )
            NutritionBar(
                label = "Жиры",
                value = fat,
                percentage = 0.65f,
                color = Theme.colors.secondary
            )
            NutritionBar(
                label = "Углеводы",
                value = carbs,
                percentage = 1f,
                color = Theme.colors.tertiary
            )
            NutritionBar(
                label = "Белки",
                value = protein,
                percentage = 0.45f,
                color = Theme.colors.primary
            )
            NutritionBar(
                label = "Соль",
                value = salt,
                percentage = 0.1f,
                color = Theme.colors.error
            )
        }
    }
}

@Composable
fun NutritionBar(
    label: String,
    value: String,
    percentage: Float,
    color: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = Theme.typography.bodyMedium,
                color = Theme.colors.onPrimaryContainer
            )
            Text(
                text = value,
                style = Theme.typography.bodyMedium,
                color = Theme.colors.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = { percentage },
            modifier = Modifier.fillMaxWidth().height(8.dp),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun InfoCard(
    title: String,
    items: List<Pair<String, String>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = CustomShapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = Theme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            items.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = key,
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.onSurfaceVariant
                    )
                    Text(
                        text = value,
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.onSurface
                    )
                }
            }
        }
    }
}