package com.korelin.openfoodfacts.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.components.LoadingIndicator
import com.korelin.openfoodfacts.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackPressed: () -> Unit,
    onProductClick: (String) -> Unit,
    viewModel: FavoritesViewModel = hiltViewModel()
) {
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                LoadingIndicator()
            } else if (favorites.isEmpty()) {
                EmptyFavoritesContent()
            } else {
                // Стабильные ключи для предотвращения мигания
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = favorites,
                        key = { product ->
                            // Стабильный ключ: code или комбинация имени
                            product.code ?: "${product.product_name}_${product.brands}"
                        }
                    ) { product ->
                        FavoriteItem(
                            product = product,
                            onRemove = {
                                product.code?.let { viewModel.removeFromFavorites(it) }
                            },
                            onClick = {
                                product.code?.let { onProductClick(it) }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    product: ProductInfo,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    // Кэшируем значения для предотвращения рекомпозиции
    val displayName = remember(product.product_name) {
        product.product_name ?: "Без названия"
    }

    val displayBrand = remember(product.brands) {
        product.brands ?: "Неизвестный бренд"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.small,
                color = Theme.colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🥫", style = MaterialTheme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1
                )
                Text(
                    text = displayBrand,
                    style = MaterialTheme.typography.bodySmall,
                    color = Theme.colors.onSurfaceVariant,
                    maxLines = 1
                )
            }

            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = Theme.colors.error
                )
            }
        }
    }
}

@Composable
fun EmptyFavoritesContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.StarBorder,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Theme.colors.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет избранных продуктов",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Добавляйте продукты в избранное",
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.onSurfaceVariant
        )
    }
}