package com.korelin.openfoodfacts.ui.screens.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.components.HistoryScreenBackground
import com.korelin.openfoodfacts.ui.theme.CustomShapes
import com.korelin.openfoodfacts.ui.theme.Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackPressed: () -> Unit,
    onProductClick: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val historyItems = remember {
        listOf(
            Triple("3017620422003", "Nutella", "17:30"),
            Triple("5449000000996", "Coca-Cola Zero", "15:15"),
            Triple("8000500310427", "Pizza Margherita", "12:45"),
            Triple("7340010000005", "Oatly", "Вчера"),
            Triple("4000539475007", "Milka", "Вчера")
        )
    }

    HistoryScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("История") },
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Поиск по истории") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    singleLine = true
                )

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyItems) { (barcode, name, time) ->
                        HistoryItem(
                            name = name,
                            barcode = barcode,
                            time = time,
                            onClick = {
                                onProductClick(barcode)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItem(
    name: String,
    barcode: String,
    time: String,
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
                        text = barcode,
                        style = Theme.typography.bodySmall,
                        color = Theme.colors.onSurfaceVariant
                    )
                }
            }

            Text(
                text = time,
                style = Theme.typography.bodySmall,
                color = Theme.colors.onSurfaceVariant
            )
        }
    }
}