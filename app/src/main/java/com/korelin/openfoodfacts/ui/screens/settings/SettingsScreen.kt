package com.korelin.openfoodfacts.ui.screens.settings

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korelin.openfoodfacts.ui.theme.Theme
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackPressed: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val themeMode by viewModel.themeMode.collectAsState()
    val language by viewModel.language.collectAsState()
    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val autoSync by viewModel.autoSync.collectAsState()
    val syncInterval by viewModel.syncInterval.collectAsState()
    val cacheSize by viewModel.cacheSize.collectAsState()
    val showPopular by viewModel.showPopular.collectAsState()
    val showNew by viewModel.showNew.collectAsState()
    val showCategories by viewModel.showCategories.collectAsState()
    val fontSize by viewModel.fontSize.collectAsState()
    val compactView by viewModel.compactView.collectAsState()
    val analyticsEnabled by viewModel.analyticsEnabled.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var showSyncIntervalDialog by remember { mutableStateOf(false) }
    var showCacheDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val actualCacheSize = remember { viewModel.calculateCacheSize() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Настройки") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showResetDialog = true }) {
                        Icon(
                            Icons.Default.Restore,
                            contentDescription = "Сбросить настройки"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Внешний вид
            item {
                SettingsCategory(
                    title = "Внешний вид",
                    icon = Icons.Default.Palette
                ) {
                    // Тема
                    SettingsRadioGroup(
                        title = "Тема оформления",
                        options = listOf("Системная", "Светлая", "Темная"),
                        selectedIndex = themeMode,
                        onOptionSelected = { viewModel.setThemeMode(it) }
                    )

                    HorizontalDivider()

                    // Размер шрифта
                    SettingsSlider(
                        title = "Размер шрифта",
                        value = fontSize,
                        onValueChange = { viewModel.setFontSize(it) },
                        valueRange = 0.8f..1.5f,
                        steps = 7,
                        valueFormatter = { "${(it * 100).toInt()}%" }
                    )

                    HorizontalDivider()

                    // Компактный вид
                    SettingsSwitch(
                        title = "Компактный вид",
                        subtitle = "Уменьшить отступы и размеры элементов",
                        checked = compactView,
                        onCheckedChange = { viewModel.setCompactView(it) },
                        icon = Icons.Default.ViewCompact
                    )
                }
            }

            // Язык
            item {
                SettingsCategory(
                    title = "Язык и регион",
                    icon = Icons.Default.Language
                ) {
                    SettingsItem(
                        title = "Язык приложения",
                        subtitle = getLanguageName(language),
                        onClick = { showLanguageDialog = true },
                        icon = Icons.Default.Translate
                    )
                }
            }

            // Уведомления
            item {
                SettingsCategory(
                    title = "Уведомления",
                    icon = Icons.Default.Notifications
                ) {
                    SettingsSwitch(
                        title = "Push-уведомления",
                        subtitle = "Получать уведомления от приложения",
                        checked = notificationsEnabled,
                        onCheckedChange = { viewModel.setNotificationsEnabled(it) },
                        icon = Icons.Default.Notifications
                    )

                    AnimatedVisibility(
                        visible = notificationsEnabled,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            HorizontalDivider()
                            SettingsSwitch(
                                title = "Звук",
                                subtitle = "Воспроизводить звук при уведомлении",
                                checked = soundEnabled,
                                onCheckedChange = { viewModel.setSoundEnabled(it) },
                                icon = Icons.Default.VolumeUp
                            )
                            SettingsSwitch(
                                title = "Вибрация",
                                subtitle = "Вибрировать при уведомлении",
                                checked = vibrationEnabled,
                                onCheckedChange = { viewModel.setVibrationEnabled(it) },
                                icon = Icons.Default.Vibration
                            )
                        }
                    }
                }
            }

            // Главный экран
            item {
                SettingsCategory(
                    title = "Главный экран",
                    icon = Icons.Default.Home
                ) {
                    SettingsSwitch(
                        title = "Популярные продукты",
                        subtitle = "Показывать блок популярных продуктов",
                        checked = showPopular,
                        onCheckedChange = { viewModel.setShowPopular(it) },
                        icon = Icons.Default.TrendingUp
                    )

                    SettingsSwitch(
                        title = "Новинки",
                        subtitle = "Показывать блок новинок",
                        checked = showNew,
                        onCheckedChange = { viewModel.setShowNew(it) },
                        icon = Icons.Default.FiberNew
                    )

                    SettingsSwitch(
                        title = "Категории",
                        subtitle = "Показывать блок категорий",
                        checked = showCategories,
                        onCheckedChange = { viewModel.setShowCategories(it) },
                        icon = Icons.Default.Category
                    )
                }
            }

            // Синхронизация и кэш
            item {
                SettingsCategory(
                    title = "Данные и синхронизация",
                    icon = Icons.Default.Sync
                ) {
                    SettingsSwitch(
                        title = "Автосинхронизация",
                        subtitle = "Автоматически обновлять данные",
                        checked = autoSync,
                        onCheckedChange = { viewModel.setAutoSync(it) },
                        icon = Icons.Default.Sync
                    )

                    AnimatedVisibility(
                        visible = autoSync,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        Column {
                            HorizontalDivider()
                            SettingsItem(
                                title = "Интервал синхронизации",
                                subtitle = "Каждые $syncInterval минут",
                                onClick = { showSyncIntervalDialog = true },
                                icon = Icons.Default.Timer
                            )
                        }
                    }

                    HorizontalDivider()

                    SettingsItem(
                        title = "Размер кэша",
                        subtitle = actualCacheSize,
                        onClick = { showCacheDialog = true },
                        icon = Icons.Default.Storage
                    )

                    SettingsItem(
                        title = "Очистить кэш",
                        subtitle = "Освободить место на устройстве",
                        onClick = {
                            scope.launch {
                                viewModel.clearCache()
                                // Показать Snackbar
                            }
                        },
                        icon = Icons.Default.DeleteSweep,
                        isDestructive = true
                    )
                }
            }

            // Приватность
            item {
                SettingsCategory(
                    title = "Приватность",
                    icon = Icons.Default.PrivacyTip
                ) {
                    SettingsSwitch(
                        title = "Аналитика",
                        subtitle = "Помочь улучшить приложение",
                        checked = analyticsEnabled,
                        onCheckedChange = { viewModel.setAnalyticsEnabled(it) },
                        icon = Icons.Default.Analytics
                    )
                }
            }

            // О приложении
            item {
                SettingsCategory(
                    title = "О приложении",
                    icon = Icons.Default.Info
                ) {
                    SettingsItem(
                        title = "Версия",
                        subtitle = "1.0.0 (Build 1)",
                        icon = Icons.Default.Info,
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Политика конфиденциальности",
                        icon = Icons.Default.PrivacyTip,
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Условия использования",
                        icon = Icons.Default.Gavel,
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Открытый исходный код",
                        subtitle = "github.com/...",
                        icon = Icons.Default.Code,
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Оценить приложение",
                        icon = Icons.Default.Star,
                        onClick = {}
                    )

                    SettingsItem(
                        title = "Сообщить об ошибке",
                        icon = Icons.Default.BugReport,
                        onClick = {}
                    )
                }
            }
        }
    }

    // Диалог сброса настроек
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Сбросить настройки") },
            text = { Text("Вы уверены, что хотите сбросить все настройки к значениям по умолчанию?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllSettings()
                        showResetDialog = false
                    }
                ) {
                    Text("Сбросить", color = Theme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Диалог выбора языка
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = language,
            onLanguageSelected = { selectedLanguage ->
                viewModel.setLanguage(selectedLanguage)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    // Диалог интервала синхронизации
    if (showSyncIntervalDialog) {
        SyncIntervalDialog(
            currentInterval = syncInterval,
            onIntervalSelected = { interval ->
                viewModel.setSyncInterval(interval)
                showSyncIntervalDialog = false
            },
            onDismiss = { showSyncIntervalDialog = false }
        )
    }

    // Диалог кэша
    if (showCacheDialog) {
        CacheDialog(
            currentSize = cacheSize,
            onSizeSelected = { size ->
                viewModel.setCacheSize(size)
                showCacheDialog = false
            },
            onDismiss = { showCacheDialog = false }
        )
    }
}

@Composable
fun SettingsCategory(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Theme.colors.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            content()
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDestructive: Boolean = false
) {
    ListItem(
        headlineContent = {
            Text(
                text = title,
                color = if (isDestructive) Theme.colors.error else Theme.colors.onSurface
            )
        },
        supportingContent = subtitle?.let {
            { Text(text = it) }
        },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (isDestructive) Theme.colors.error else Theme.colors.primary
            )
        },
        trailingContent = {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Theme.colors.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable { onClick() }
    )
}

@Composable
fun SettingsSwitch(
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    ListItem(
        headlineContent = { Text(text = title) },
        supportingContent = subtitle?.let { { Text(text = it) } },
        leadingContent = {
            Icon(
                icon,
                contentDescription = null,
                tint = if (checked) Theme.colors.primary else Theme.colors.onSurfaceVariant
            )
        },
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        }
    )
}

@Composable
fun SettingsRadioGroup(
    title: String,
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        options.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = selectedIndex == index,
                        onClick = { onOptionSelected(index) }
                    )
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedIndex == index,
                    onClick = { onOptionSelected(index) }
                )
                Text(
                    text = option,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun SettingsSlider(
    title: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
    steps: Int,
    valueFormatter: (Float) -> String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = valueFormatter(value),
                style = MaterialTheme.typography.bodyMedium,
                color = Theme.colors.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            steps = steps,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languages = listOf(
        "system" to "Системный",
        "ru" to "Русский",
        "en" to "English",
        "fr" to "Français",
        "de" to "Deutsch",
        "es" to "Español"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Выберите язык") },
        text = {
            LazyColumn {
                items(languages) { (code, name) ->
                    ListItem(
                        headlineContent = { Text(name) },
                        leadingContent = {
                            RadioButton(
                                selected = currentLanguage == code,
                                onClick = { onLanguageSelected(code) }
                            )
                        },
                        modifier = Modifier.clickable { onLanguageSelected(code) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun SyncIntervalDialog(
    currentInterval: Int,
    onIntervalSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val intervals = listOf(15, 30, 60, 120, 360, 720, 1440)
    val intervalNames = mapOf(
        15 to "15 минут",
        30 to "30 минут",
        60 to "1 час",
        120 to "2 часа",
        360 to "6 часов",
        720 to "12 часов",
        1440 to "24 часа"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Интервал синхронизации") },
        text = {
            LazyColumn {
                items(intervals) { interval ->
                    ListItem(
                        headlineContent = { Text(intervalNames[interval] ?: "$interval мин") },
                        leadingContent = {
                            RadioButton(
                                selected = currentInterval == interval,
                                onClick = { onIntervalSelected(interval) }
                            )
                        },
                        modifier = Modifier.clickable { onIntervalSelected(interval) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@Composable
fun CacheDialog(
    currentSize: Int,
    onSizeSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sizes = listOf(50, 100, 200, 500, 1024)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Максимальный размер кэша") },
        text = {
            LazyColumn {
                items(sizes) { size ->
                    ListItem(
                        headlineContent = { Text("${size} MB") },
                        leadingContent = {
                            RadioButton(
                                selected = currentSize == size,
                                onClick = { onSizeSelected(size) }
                            )
                        },
                        modifier = Modifier.clickable { onSizeSelected(size) }
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

fun getLanguageName(code: String): String {
    return when (code) {
        "system" -> "Системный"
        "ru" -> "Русский"
        "en" -> "English"
        "fr" -> "Français"
        "de" -> "Deutsch"
        "es" -> "Español"
        else -> "Системный"
    }
}