package com.korelin.openfoodfacts.ui.screens.favorites

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onBackPressed: () -> Unit,
    onProductClick: (String) -> Unit
) {
    // Заглушка для демонстрации
    val favorites = remember {
        listOf(
            Triple("3017620422003", "Nutella", "Ferrero"),
            Triple("5449000000996", "Coca-Cola Zero", "Coca-Cola"),
            Triple("8000500310427", "Pizza Margherita", "Dr. Oetker")
        )
    }

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
                }
            )
        }
    ) { paddingValues ->
        if (favorites.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(favorites) { (barcode, name, brand) ->
                    FavoriteItem(
                        name = name,
                        brand = brand,
                        onClick = {
                            onProductClick(barcode)
                        }
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "⭐",
                        style = Theme.typography.displayLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Нет избранных продуктов",
                        style = Theme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItem(
    name: String,
    brand: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = CustomShapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.secondaryContainer
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
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CustomShapes.small,
                    color = Theme.colors.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🥫", style = Theme.typography.titleLarge)
                    }
                }

                Column {
                    Text(
                        text = name,
                        style = Theme.typography.bodyLarge
                    )
                    Text(
                        text = brand,
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.onSecondaryContainer
                    )
                }
            }

            Icon(
                Icons.Default.Star,
                contentDescription = "В избранном",
                tint = Theme.colors.primary
            )
        }
    }
}