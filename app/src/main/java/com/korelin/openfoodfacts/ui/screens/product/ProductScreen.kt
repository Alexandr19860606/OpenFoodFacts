package com.korelin.openfoodfacts.ui.screens.product

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.accompanist.permissions.*
import com.korelin.openfoodfacts.data.model.ProductInfo
import com.korelin.openfoodfacts.ui.components.LoadingIndicator
import com.korelin.openfoodfacts.ui.theme.Theme
import com.korelin.openfoodfacts.utils.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class, ExperimentalAnimationApi::class)
@Composable
fun ProductScreen(
    barcode: String,
    onBackPressed: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    val product by viewModel.product.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    val context = LocalContext.current
    val scrollState = rememberLazyListState()
    val isScrolling = remember { derivedStateOf { scrollState.firstVisibleItemIndex > 0 } }

    // Состояния для разрешений
    val storagePermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val calendarPermissionState = rememberPermissionState(
        android.Manifest.permission.WRITE_CALENDAR
    )

    // Анимации
    val fabScale by animateFloatAsState(
        targetValue = if (isScrolling.value) 0.8f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "fab_scale"
    )

    val fabAlpha by animateFloatAsState(
        targetValue = if (isScrolling.value) 0.6f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = "fab_alpha"
    )

    LaunchedEffect(barcode) {
        viewModel.loadProduct(barcode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedContent(
                        targetState = product?.product_name ?: "Детали продукта",
                        transitionSpec = {
                            fadeIn() with fadeOut()
                        },
                        label = "title_animation"
                    ) { title ->
                        Text(
                            text = title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(
                        visible = !isScrolling.value,
                        enter = fadeIn() + scaleIn(),
                        exit = fadeOut() + scaleOut()
                    ) {
                        Row {
                            IconButton(onClick = { viewModel.toggleFavorite() }) {
                                Icon(
                                    if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = if (isFavorite) "Удалить из избранного" else "Добавить в избранное",
                                    tint = if (isFavorite) Theme.colors.primary else Theme.colors.onSurfaceVariant
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Theme.colors.surface.copy(alpha = if (isScrolling.value) 0.9f else 1f)
                )
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = product != null,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut()
            ) {
                FloatingActionButton(
                    onClick = {
                        product?.let {
                            ShareHelper.shareProduct(context, it)
                        }
                    },
                    modifier = Modifier
                        .scale(fabScale)
                        .graphicsLayer(alpha = fabAlpha),
                    shape = CircleShape,
                    containerColor = Theme.colors.primary
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = "Поделиться",
                        tint = Theme.colors.onPrimary
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingIndicator()
                }
                error != null -> {
                    ErrorContent(
                        error = error!!,
                        onRetry = { viewModel.retry() },
                        onBack = onBackPressed
                    )
                }
                product != null -> {
                    ProductDetailContent(
                        product = product!!,
                        scrollState = scrollState,
                        isFavorite = isFavorite,
                        onFavoriteClick = { viewModel.toggleFavorite() },
                        onShareClick = {
                            ShareHelper.shareProduct(context, product!!)
                        },
                        onDownloadImage = {
                            if (ImageDownloader.hasStoragePermission(context)) {
                                product!!.getBestImageUrl()?.let { imageUrl ->
                                    ImageDownloader.downloadImageWithCoil(
                                        context,
                                        imageUrl,
                                        product!!.product_name ?: "product_${product!!.code}"
                                    )
                                }
                            } else {
                                storagePermissionState.launchPermissionRequest()
                            }
                        },
                        onAddToCalendar = {
                            if (calendarPermissionState.status.isGranted) {
                                CalendarHelper.addProductToCalendar(
                                    context,
                                    product!!.product_name,
                                    product!!.code ?: ""
                                )
                            } else {
                                calendarPermissionState.launchPermissionRequest()
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailContent(
    product: ProductInfo,
    scrollState: LazyListState,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onDownloadImage: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    LazyColumn(
        state = scrollState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Изображение с параллакс-эффектом
        item {
            ParallaxImage(
                imageUrl = product.getBestImageUrl(),
                productName = product.product_name,
                scrollState = scrollState
            )
        }

        // Основная информация
        item {
            ProductInfoSection(
                product = product,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick
            )
        }

        // Пищевая ценность
        if (product.nutriments != null) {
            item {
                NutritionSection(product = product)
            }
        }

        // Состав
        if (!product.ingredients_text.isNullOrBlank()) {
            item {
                IngredientsSection(product = product)
            }
        }

        // Аллергены
        if (product.getAllergensList().isNotEmpty()) {
            item {
                AllergensSection(product = product)
            }
        }

        // Дополнительная информация
        item {
            AdditionalInfoSection(product = product)
        }

        // Кнопки действий
        item {
            ActionButtonsRow(
                onShareClick = onShareClick,
                onDownloadImage = onDownloadImage,
                onAddToCalendar = onAddToCalendar,
                isFavorite = isFavorite,
                onFavoriteClick = onFavoriteClick,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ParallaxImage(
    imageUrl: String?,
    productName: String?,
    scrollState: LazyListState
) {
    val scrollOffset = remember { derivedStateOf { scrollState.firstVisibleItemScrollOffset } }
    val parallaxOffset = scrollOffset.value / 2f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .graphicsLayer {
                translationY = -parallaxOffset
            }
    ) {
        if (!imageUrl.isNullOrEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = productName,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Theme.colors.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "🥫",
                        style = MaterialTheme.typography.displayLarge
                    )
                }
            }
        }
    }
}

@Composable
fun ProductInfoSection(
    product: ProductInfo,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок
            Text(
                text = product.product_name ?: "Без названия",
                style = MaterialTheme.typography.headlineMedium,
                color = Theme.colors.onSurface
            )

            // Бренд
            product.brands?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge,
                    color = Theme.colors.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Штрих-код
            Text(
                text = buildAnnotatedString {
                    append("Штрих-код: ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Theme.colors.primary)) {
                        append(product.code ?: "Н/Д")
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )

            // Количество
            product.quantity?.let {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Количество: ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(it)
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Рейтинги
            RatingBadges(product = product)
        }
    }
}

@Composable
fun RatingBadges(product: ProductInfo) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        product.nutrition_grade_fr?.let {
            RatingBadge(
                label = "Nutri-Score",
                value = it.uppercase(),
                color = getNutritionGradeColor(it),
                description = getNutritionGradeDescription(it)
            )
        }

        product.nova_group?.let {
            RatingBadge(
                label = "NOVA",
                value = it.toString(),
                color = getNovaGroupColor(it),
                description = getNovaGroupDescription(it)
            )
        }

        product.ecoscore_grade?.let {
            RatingBadge(
                label = "Eco-Score",
                value = it.uppercase(),
                color = getEcoScoreColor(it),
                description = getEcoScoreDescription(it)
            )
        }
    }
}

@Composable
fun RatingBadge(
    label: String,
    value: String,
    color: Color,
    description: String
) {
    var showTooltip by remember { mutableStateOf(false) }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { showTooltip = !showTooltip },
                shape = CircleShape,
                color = color
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Theme.colors.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (showTooltip) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = 56.dp)
                    .wrapContentSize(),
                shape = RoundedCornerShape(8.dp),
                shadowElevation = 4.dp,
                color = Theme.colors.surface
            ) {
                Text(
                    text = description,
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun NutritionSection(product: ProductInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Пищевая ценность на 100г",
                style = MaterialTheme.typography.titleLarge,
                color = Theme.colors.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            val nutriments = product.nutriments

            nutriments?.let {
                NutritionItem(
                    label = "Энергия",
                    value = it.energy_kcal_100g,
                    unit = "ккал"
                )
                NutritionItem(
                    label = "Жиры",
                    value = it.fat_100g,
                    unit = it.fat_unit ?: "г"
                )
                NutritionItem(
                    label = "Насыщенные жиры",
                    value = it.saturated_fat_100g,
                    unit = it.saturated_fat_unit ?: "г"
                )
                NutritionItem(
                    label = "Углеводы",
                    value = it.carbohydrates_100g,
                    unit = it.carbohydrates_unit ?: "г"
                )
                NutritionItem(
                    label = "Сахара",
                    value = it.sugars_100g,
                    unit = it.sugars_unit ?: "г"
                )
                NutritionItem(
                    label = "Белки",
                    value = it.proteins_100g,
                    unit = it.proteins_unit ?: "г"
                )
                NutritionItem(
                    label = "Соль",
                    value = it.salt_100g,
                    unit = it.salt_unit ?: "г"
                )
            }
        }
    }
}

@Composable
fun NutritionItem(
    label: String,
    value: Double?,
    unit: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.onPrimaryContainer
        )
        Text(
            text = formatNutritionValue(value, unit),
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.onPrimaryContainer,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IngredientsSection(product: ProductInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Состав",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = product.ingredients_text ?: "Информация о составе отсутствует",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AllergensSection(product: ProductInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Theme.colors.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Аллергены",
                style = MaterialTheme.typography.titleLarge,
                color = Theme.colors.onErrorContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            val allergens = product.getAllergensList()
            if (allergens.isNotEmpty()) {
                Text(
                    text = allergens.joinToString(", ") {
                        it.replace("en:", "").replace("fr:", "")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.colors.onErrorContainer,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Информация об аллергенах отсутствует",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Theme.colors.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun AdditionalInfoSection(product: ProductInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Дополнительная информация",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            product.categories?.let {
                InfoRow("Категории", it)
            }

            product.packaging?.let {
                InfoRow("Упаковка", it)
            }

            product.manufacturing_places?.let {
                InfoRow("Место производства", it)
            }

            product.countries?.let {
                InfoRow("Страны продажи", it)
            }

            product.labels?.let {
                InfoRow("Сертификаты", it)
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Theme.colors.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun ActionButtonsRow(
    onShareClick: () -> Unit,
    onDownloadImage: () -> Unit,
    onAddToCalendar: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ActionButton(
            icon = Icons.Default.Share,
            text = "Поделиться",
            onClick = onShareClick,
            color = Theme.colors.primary
        )

        ActionButton(
            icon = Icons.Default.Download,
            text = "Скачать",
            onClick = onDownloadImage,
            color = Theme.colors.secondary
        )

        ActionButton(
            icon = Icons.Default.Event,
            text = "Календарь",
            onClick = onAddToCalendar,
            color = Theme.colors.tertiary
        )

        ActionButton(
            icon = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
            text = if (isFavorite) "В избранном" else "В избранное",
            onClick = onFavoriteClick,
            color = if (isFavorite) Theme.colors.error else Theme.colors.primary
        )
    }
}

@Composable
fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
        ) {
            Icon(
                icon,
                contentDescription = text,
                tint = color
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Theme.colors.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun ErrorContent(
    error: String,
    onRetry: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Theme.colors.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = Theme.colors.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Theme.colors.secondaryContainer
                )
            ) {
                Text("Назад")
            }
            Button(
                onClick = onRetry
            ) {
                Text("Повторить")
            }
        }
    }
}

// Вспомогательные функции
fun ProductInfo.getBestImageUrl(): String? {
    return image_front_url ?: image_front_small_url ?: image_url
}

fun ProductInfo.getAllergensList(): List<String> {
    return allergens_tags ?: emptyList()
}

fun formatNutritionValue(value: Double?, unit: String): String {
    return if (value != null) {
        String.format("%.1f %s", value, unit)
    } else {
        "—"
    }
}

fun getNutritionGradeColor(grade: String): Color {
    return when (grade.lowercase()) {
        "a" -> Color(0xFF4CAF50)
        "b" -> Color(0xFF8BC34A)
        "c" -> Color(0xFFFFC107)
        "d" -> Color(0xFFFF9800)
        "e" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getNutritionGradeDescription(grade: String): String {
    return when (grade.lowercase()) {
        "a" -> "Отличное качество"
        "b" -> "Хорошее качество"
        "c" -> "Среднее качество"
        "d" -> "Низкое качество"
        "e" -> "Очень низкое качество"
        else -> "Неизвестно"
    }
}

fun getNovaGroupColor(group: Int): Color {
    return when (group) {
        1 -> Color(0xFF4CAF50)
        2 -> Color(0xFF8BC34A)
        3 -> Color(0xFFFF9800)
        4 -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getNovaGroupDescription(group: Int): String {
    return when (group) {
        1 -> "Необработанные продукты"
        2 -> "Кулинарные ингредиенты"
        3 -> "Обработанные продукты"
        4 -> "Ультра-обработанные продукты"
        else -> "Неизвестно"
    }
}

fun getEcoScoreColor(grade: String): Color {
    return when (grade.lowercase()) {
        "a" -> Color(0xFF4CAF50)
        "b" -> Color(0xFF8BC34A)
        "c" -> Color(0xFFFFC107)
        "d" -> Color(0xFFFF9800)
        "e" -> Color(0xFFF44336)
        else -> Color.Gray
    }
}

fun getEcoScoreDescription(grade: String): String {
    return when (grade.lowercase()) {
        "a" -> "Экологичный продукт"
        "b" -> "Хороший эко-показатель"
        "c" -> "Средний эко-показатель"
        "d" -> "Низкий эко-показатель"
        "e" -> "Очень низкий эко-показатель"
        else -> "Неизвестно"
    }
}