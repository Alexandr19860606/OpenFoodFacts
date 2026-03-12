package com.korelin.openfoodfacts.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeState: HomeDataState by viewModel.homeState.collectAsState()
    val favoritesMap by viewModel.favoritesMap.collectAsState()
    var showTimeout by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(10000)
        if (homeState is HomeDataState.Loading) {
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
                    ErrorView(
                        message = "Таймаут загрузки. Проверьте интернет.",
                        onRetry = {
                            showTimeout = false
                            viewModel.refresh()
                        }
                    )
                }
                homeState is HomeDataState.Loading -> {
                    LoadingIndicator()
                }
                homeState is HomeDataState.Error -> {
                    ErrorView(
                        message = (homeState as HomeDataState.Error).message,
                        onRetry = { viewModel.refresh() }
                    )
                }
                homeState is HomeDataState.Success -> {
                    val state = homeState as HomeDataState.Success

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
                            // В HomeScreen.kt, внутри LazyColumn:

                            if (state.popular.isNotEmpty()) {
                                item {
                                    PopularSection(
                                        products = state.popular,
                                        favoritesMap = favoritesMap,
                                        onProductClick = { product ->
                                            product.code?.let { barcode ->
                                                navController.navigate("product/$barcode")
                                            }
                                        },
                                        onFavoriteToggle = { product, isFavorite ->
                                            if (isFavorite) {
                                                product.code?.let { viewModel.addToFavorites(product) }
                                            } else {
                                                product.code?.let { viewModel.removeFromFavorites(it) }
                                            }
                                        }
                                    )
                                }
                            }

                            if (state.new.isNotEmpty()) {
                                item {
                                    NewSection(
                                        products = state.new,
                                        favoritesMap = favoritesMap,
                                        onProductClick = { product ->
                                            product.code?.let { barcode ->
                                                navController.navigate("product/$barcode")
                                            }
                                        },
                                        onFavoriteToggle = { product, isFavorite ->
                                            if (isFavorite) {
                                                product.code?.let { viewModel.addToFavorites(product) }
                                            } else {
                                                product.code?.let { viewModel.removeFromFavorites(it) }
                                            }
                                        }
                                    )
                                }
                            }

                            if (state.new.isNotEmpty()) {
                                item {
                                    NewSection(
                                        products = state.new,
                                        favoritesMap = favoritesMap,
                                        onProductClick = { product ->
                                            product.code?.let { barcode ->
                                                navController.navigate("product/$barcode")
                                            }
                                        },
                                        onFavoriteToggle = { product, isFavorite ->
                                            if (isFavorite) {
                                                product.code?.let { viewModel.addToFavorites(product) }
                                            } else {
                                                product.code?.let { viewModel.removeFromFavorites(it) }
                                            }
                                        }
                                    )
                                }
                            }

                            item {
                                CategorySection(
                                    onCategoryClick = { category ->
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