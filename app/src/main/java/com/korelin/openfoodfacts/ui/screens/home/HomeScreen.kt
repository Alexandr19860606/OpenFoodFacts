package com.korelin.openfoodfacts.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.korelin.openfoodfacts.data.repository.HomeDataState
import com.korelin.openfoodfacts.ui.components.ErrorView
import com.korelin.openfoodfacts.ui.components.HomeScreenBackground
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

    val colors = Theme.colors
    val typography = Theme.typography

    val listState = rememberLazyListState()

    LaunchedEffect(Unit) {
        delay(10000)
        if (homeState is HomeDataState.Loading) {
            showTimeout = true
        }
    }

    HomeScreenBackground {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Open Food Facts",
                            style = typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = { navController.navigate("search") }
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Поиск",
                                tint = colors.onPrimaryContainer
                            )
                        }
                        IconButton(
                            onClick = { navController.navigate("notifications") }
                        ) {
                            BadgedBox(
                                badge = {
                                    // Здесь можно добавить бейдж с количеством уведомлений
                                }
                            ) {
                                Icon(
                                    Icons.Outlined.Notifications,
                                    contentDescription = "Уведомления",
                                    tint = colors.onPrimaryContainer
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.primaryContainer,
                        titleContentColor = colors.onPrimaryContainer
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
                            EmptyHomeContent(onRefresh = { viewModel.refresh() })
                        } else {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                item {
                                    WelcomeHeader()
                                }

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

                                item {
                                    CategorySection(
                                        onCategoryClick = { category ->
                                            navController.navigate("search/$category")
                                        }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader() {
    val colors = Theme.colors

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.primary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Добро пожаловать!",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = colors.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Сканируйте продукты и узнавайте их состав",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSurfaceVariant
                )
            }

            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                color = colors.primary
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🥫",
                        fontSize = 32.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHomeContent(onRefresh: () -> Unit) {
    val colors = Theme.colors

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = colors.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "🥫",
                    fontSize = 48.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Нет данных для отображения",
            style = MaterialTheme.typography.headlineSmall,
            color = colors.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Попробуйте обновить или проверьте интернет",
            style = MaterialTheme.typography.bodyLarge,
            color = colors.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRefresh,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colors.primary
            )
        ) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Обновить")
        }
    }
}