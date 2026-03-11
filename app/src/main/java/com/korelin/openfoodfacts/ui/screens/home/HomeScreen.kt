package com.korelin.openfoodfacts.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.korelin.openfoodfacts.data.repository.HomeDataState
import com.korelin.openfoodfacts.ui.components.ErrorView
import com.korelin.openfoodfacts.ui.components.LoadingIndicator
import com.korelin.openfoodfacts.ui.screens.home.components.CategorySection
import com.korelin.openfoodfacts.ui.screens.home.components.NewSection
import com.korelin.openfoodfacts.ui.screens.home.components.PopularSection
import com.korelin.openfoodfacts.ui.theme.Theme
import com.korelin.openfoodfacts.utils.FileLogger
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val homeState: HomeDataState by viewModel.homeState.collectAsState()
    var showTimeout by remember { mutableStateOf(false) }

    FileLogger.d("HomeScreen", "HomeScreen загружен, state: $homeState")

    LaunchedEffect(Unit) {
        delay(10000)
        if (homeState is HomeDataState.Loading) {
            FileLogger.e("HomeScreen", "Таймаут загрузки - 10 секунд")
            showTimeout = true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Open Food Facts") },
                actions = {
                    IconButton(
                        onClick = {
                            FileLogger.d("HomeScreen", "Нажата кнопка поиска")
                            navController.navigate("search")
                        }
                    ) {
                        Icon(Icons.Default.Search, contentDescription = "Поиск")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colors.primaryContainer,
                    titleContentColor = Theme.colors.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                showTimeout -> {
                    FileLogger.e("HomeScreen", "Показываем ошибку таймаута")
                    ErrorView(
                        message = "Таймаут загрузки. Проверьте интернет.",
                        onRetry = {
                            showTimeout = false
                            viewModel.refresh()
                        }
                    )
                }
                homeState is HomeDataState.Loading -> {
                    FileLogger.d("HomeScreen", "Показываем загрузку")
                    LoadingIndicator()
                }
                homeState is HomeDataState.Error -> {
                    FileLogger.e("HomeScreen", "Ошибка: ${(homeState as HomeDataState.Error).message}")
                    ErrorView(
                        message = (homeState as HomeDataState.Error).message,
                        onRetry = { viewModel.refresh() }
                    )
                }
                homeState is HomeDataState.Success -> {
                    val state = homeState as HomeDataState.Success
                    FileLogger.d("HomeScreen", "Успешно загружено: популярных=${state.popular.size}, новинок=${state.new.size}")

                    if (state.popular.isEmpty() && state.new.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "😕",
                                style = MaterialTheme.typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Нет данных для отображения",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Обновить")
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            if (state.popular.isNotEmpty()) {
                                item {
                                    PopularSection(
                                        products = state.popular,
                                        onProductClick = { product ->
                                            FileLogger.d("HomeScreen", "Клик по популярному продукту: ${product.product_name}")
                                            val barcodeValue = product.code
                                            if (barcodeValue != null) {
                                                navController.navigate("product/$barcodeValue")
                                            } else {
                                                FileLogger.e("HomeScreen", "Barcode не найден для продукта")
                                            }
                                        }
                                    )
                                }
                            }

                            if (state.new.isNotEmpty()) {
                                item {
                                    NewSection(  // ← Используем NewSection, а не NewSectionItem
                                        products = state.new,
                                        onProductClick = { product ->
                                            FileLogger.d("HomeScreen", "Клик по новинке: ${product.product_name}")
                                            val barcodeValue = product.code
                                            if (barcodeValue != null) {
                                                navController.navigate("product/$barcodeValue")
                                            } else {
                                                FileLogger.e("HomeScreen", "Barcode не найден для продукта")
                                            }
                                        }
                                    )
                                }
                            }

                            item {
                                CategorySection(
                                    onCategoryClick = { category ->
                                        FileLogger.d("HomeScreen", "Клик по категории: $category")
                                        navController.navigate("search/$category")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}