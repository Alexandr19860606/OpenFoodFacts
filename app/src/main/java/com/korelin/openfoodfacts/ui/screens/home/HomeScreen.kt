package com.korelin.openfoodfacts.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
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
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val homeState: HomeDataState by viewModel.homeState.collectAsState()
    val favoritesMap by viewModel.favoritesMap.collectAsState()
    var showTimeout by remember { mutableStateOf(false) }

    // Получаем colors на уровне композиции
    val colors = Theme.colors
    val typography = Theme.typography

    // Состояние для скролла
    val listState = rememberLazyListState()

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
                                style = typography.displayLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Нет данных для отображения",
                                style = typography.bodyLarge
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = { viewModel.refresh() }) {
                                Text("Обновить")
                            }
                        }
                    } else {
                        // Основной контент с кастомным скролл-баром
                        Box(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                state = listState,
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(24.dp)
                            ) {
                                // Популярные продукты
                                if (state.popular.isNotEmpty()) {
                                    item {
                                        PopularSection(
                                            products = state.popular,
                                            favoritesMap = favoritesMap,
                                            onProductClick = { product ->
                                                FileLogger.d("HomeScreen", "Клик по популярному продукту: ${product.product_name}")
                                                product.code?.let { barcode ->
                                                    navController.navigate("product/$barcode")
                                                } ?: FileLogger.e("HomeScreen", "Barcode не найден для продукта")
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

                                // Новинки
                                if (state.new.isNotEmpty()) {
                                    item {
                                        NewSection(
                                            products = state.new,
                                            favoritesMap = favoritesMap,
                                            onProductClick = { product ->
                                                FileLogger.d("HomeScreen", "Клик по новинке: ${product.product_name}")
                                                product.code?.let { barcode ->
                                                    navController.navigate("product/$barcode")
                                                } ?: FileLogger.e("HomeScreen", "Barcode не найден для продукта")
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

                                // Категории
                                item {
                                    CategorySection(
                                        onCategoryClick = { category ->
                                            FileLogger.d("HomeScreen", "Клик по категории: $category")
                                            navController.navigate("search/$category")
                                        }
                                    )
                                }
                            }

                            // Кастомный вертикальный скролл-бар
                            CustomVerticalScrollbar(
                                listState = listState,
                                modifier = Modifier
                                    .align(Alignment.CenterEnd)
                                    .fillMaxHeight()
                                    .width(8.dp)
                                    .padding(vertical = 4.dp),
                                color = colors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomVerticalScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    color: Color,
    backgroundColor: Color = color.copy(alpha = 0.2f)
) {
    val totalItems = listState.layoutInfo.totalItemsCount
    val visibleItems = listState.layoutInfo.visibleItemsInfo.size

    if (totalItems <= visibleItems) return // Не показываем скролл-бар, если все помещается

    val firstVisibleIndex = listState.firstVisibleItemIndex
    val scrollOffset = listState.firstVisibleItemScrollOffset

    // Вычисляем позицию и размер скролл-бара
    val scrollableItems = (totalItems - visibleItems).coerceAtLeast(1)
    val scrollProgress = (firstVisibleIndex + scrollOffset.toFloat() / 100f) / scrollableItems

    val barHeight = (visibleItems.toFloat() / totalItems).coerceIn(0.1f, 1f)
    val barOffset = scrollProgress.coerceIn(0f, 1f - barHeight)

    val coroutineScope = rememberCoroutineScope()

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    coroutineScope.launch {
                        // Прокрутка при перетаскивании
                        val dragRatio = dragAmount.y / size.height
                        val targetIndex = (firstVisibleIndex + (scrollableItems * dragRatio)).roundToInt()
                            .coerceIn(0, totalItems - visibleItems)
                        listState.scrollToItem(targetIndex)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    coroutineScope.launch {
                        // Прокрутка при тапе
                        val tapRatio = offset.y / size.height
                        val targetIndex = (tapRatio * totalItems).roundToInt()
                            .coerceIn(0, totalItems - visibleItems)
                        listState.scrollToItem(targetIndex)
                    }
                }
            }
    ) {
        // Фон скролл-бара
        drawRect(
            color = backgroundColor,
            topLeft = Offset.Zero,
            size = size
        )

        // Ползунок скролл-бара
        drawRect(
            color = color,
            topLeft = Offset(0f, size.height * barOffset),
            size = size.copy(height = size.height * barHeight)
        )
    }
}