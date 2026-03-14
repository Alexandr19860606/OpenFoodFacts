package com.korelin.openfoodfacts.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.components.FavoritesScreenBackground
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
    val history by viewModel.history.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val showHistory by viewModel.showHistory.collectAsState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }

    FavoritesScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Избранное") },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
                            )
                        }
                    },
                    actions = {
                        Box(modifier = Modifier.padding(end = 8.dp)) {
                            IconButton(
                                onClick = { viewModel.toggleHistorySheet() }
                            ) {
                                Icon(
                                    Icons.Default.History,
                                    contentDescription = "История"
                                )
                            }

                            if (history.isNotEmpty()) {
                                Badge(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .offset(x = 8.dp, y = 8.dp),
                                    containerColor = Theme.colors.primary
                                ) {
                                    Text(
                                        text = history.size.toString(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Theme.colors.onPrimary
                                    )
                                }
                            }
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
                if (isLoading && favorites.isEmpty()) {
                    LoadingIndicator()
                } else {
                    if (favorites.isEmpty()) {
                        EmptyFavoritesContent()
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = favorites,
                                key = { it.code ?: it.product_name ?: "" }
                            ) { product ->
                                FavoriteItem(
                                    product = product,
                                    onRemove = {
                                        product.code?.let { code ->
                                            viewModel.removeFromFavorites(code)
                                        }
                                    },
                                    onClick = {
                                        product.code?.let { onProductClick(it) }
                                    }
                                )
                            }
                        }
                    }
                }

                if (showHistory) {
                    HistoryBottomSheet(
                        history = history,
                        onDismiss = { viewModel.toggleHistorySheet() },
                        onProductClick = onProductClick,
                        onRemoveFromHistory = { productCode ->
                            viewModel.removeFromHistory(productCode)
                        },
                        onClearHistory = {
                            showClearHistoryDialog = true
                        }
                    )
                }

                if (showClearHistoryDialog) {
                    AlertDialog(
                        onDismissRequest = { showClearHistoryDialog = false },
                        title = { Text("Очистить историю") },
                        text = { Text("Вы уверены, что хотите очистить всю историю просмотров?") },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    viewModel.clearHistory()
                                    showClearHistoryDialog = false
                                }
                            ) {
                                Text("Очистить", color = Theme.colors.error)
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showClearHistoryDialog = false }) {
                                Text("Отмена")
                            }
                        }
                    )
                }
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
            text = "Добавляйте продукты в избранное, чтобы они появлялись здесь",
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.onSurfaceVariant
        )
    }
}

@Composable
fun FavoriteItem(
    product: ProductInfo,
    onRemove: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = Theme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Миниатюра
            Surface(
                modifier = Modifier.size(56.dp),
                shape = Theme.shapes.small,
                color = Theme.colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🥫", style = Theme.typography.titleLarge)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация о продукте
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

            // Кнопка удаления
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить из избранного",
                    tint = Theme.colors.error
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
    history: List<ProductInfo>,
    onDismiss: () -> Unit,
    onProductClick: (String) -> Unit,
    onRemoveFromHistory: (String) -> Unit,
    onClearHistory: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = Theme.shapes.large,
        containerColor = Theme.colors.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "История просмотров",
                    style = Theme.typography.titleLarge
                )

                if (history.isNotEmpty()) {
                    TextButton(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Theme.colors.error
                        )
                    ) {
                        Text("Очистить всё")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (history.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "История просмотров пуста",
                        style = Theme.typography.bodyMedium,
                        color = Theme.colors.onSurfaceVariant
                    )
                }
            } else {
                // Список истории
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = history,
                        key = { it.code ?: it.product_name ?: "" }
                    ) { product ->
                        HistoryItem(
                            product = product,
                            onClick = {
                                product.code?.let { onProductClick(it) }
                                onDismiss()
                            },
                            onRemove = {
                                product.code?.let { onRemoveFromHistory(it) }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryItem(
    product: ProductInfo,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = Theme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Миниатюра
            Surface(
                modifier = Modifier.size(40.dp),
                shape = Theme.shapes.small,
                color = Theme.colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🥫", style = Theme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Информация
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.product_name ?: "Без названия",
                    style = Theme.typography.bodyMedium,
                    maxLines = 1
                )
                Text(
                    text = product.brands ?: "Неизвестный бренд",
                    style = Theme.typography.bodySmall,
                    color = Theme.colors.onSurfaceVariant,
                    maxLines = 1
                )
            }

            // Кнопка удалить из истории
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Удалить из истории",
                    tint = Theme.colors.onSurfaceVariant
                )
            }
        }
    }
}