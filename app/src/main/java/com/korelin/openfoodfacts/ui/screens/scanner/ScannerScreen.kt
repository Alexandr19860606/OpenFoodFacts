package com.korelin.openfoodfacts.ui.screens.scanner

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.korelin.openfoodfacts.ui.components.ScannerScreenBackground
import com.korelin.openfoodfacts.ui.theme.AppIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerScreen(
    onBarcodeScanned: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToFavorites: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    var isFlashOn by remember { mutableStateOf(false) }
    var showManualInput by remember { mutableStateOf(false) }
    var manualBarcode by remember { mutableStateOf("") }

    val recentScans = remember {
        listOf(
            Triple("3017620422003", "Nutella", "17:30"),
            Triple("5449000000996", "Coca-Cola Zero", "15:15"),
            Triple("8000500310427", "Pizza Margherita", "12:45")
        )
    }

    ScannerScreenBackground {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Сканер продуктов",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { isFlashOn = !isFlashOn }
                        ) {
                            Icon(
                                imageVector = if (isFlashOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = if (isFlashOn) "Выключить вспышку" else "Включить вспышку"
                            )
                        }
                    }
                )
            }
            // 👇 УБИРАЕМ bottomBar ОТСЮДА, ТАК КАК ОН УЖЕ ЕСТЬ В MainActivity
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Placeholder для камеры
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "📷",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Text(
                                text = "Здесь будет камера",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Нажмите для ручного ввода",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                // Ручной ввод
                if (showManualInput) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Введите штрих-код",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = manualBarcode,
                                onValueChange = { manualBarcode = it },
                                label = { Text("Штрих-код (13 цифр)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    if (manualBarcode.length == 13) {
                                        onBarcodeScanned(manualBarcode)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = manualBarcode.length == 13
                            ) {
                                Text("Найти продукт")
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { showManualInput = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text("Ввести штрих-код вручную")
                    }
                }

                // Последние сканирования
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Недавние сканирования",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        recentScans.forEachIndexed { index, scan ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${scan.second} (${scan.first})")
                                Text(scan.third)
                            }
                            if (index < recentScans.size - 1) {
                                HorizontalDivider()
                            }
                        }
                    }
                }
            }
        }
    }
}