package com.korelin.openfoodfacts.ui.screens.notifications

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.local.entity.NotificationType
import com.korelin.openfoodfacts.ui.components.LoadingIndicator
import com.korelin.openfoodfacts.ui.theme.Theme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackPressed: () -> Unit,
    onNotificationClick: (NotificationEntity) -> Unit,
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showClearDialog by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterType by remember { mutableStateOf<NotificationType?>(null) }

    // Фильтруем уведомления по типу
    val filteredNotifications = remember(notifications, filterType) {
        if (filterType == null) {
            notifications
        } else {
            notifications.filter { it.type == filterType }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Уведомления")
                        if (unreadCount > 0) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Badge(
                                containerColor = Theme.colors.primary
                            ) {
                                Text(
                                    text = unreadCount.toString(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Theme.colors.onPrimary
                                )
                            }
                        }
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
                    // Кнопка фильтра
                    IconButton(onClick = { showFilterMenu = true }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Фильтр"
                        )
                    }

                    // Кнопка отметить все как прочитанные
                    if (unreadCount > 0) {
                        IconButton(onClick = { viewModel.markAllAsRead() }) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = "Отметить все"
                            )
                        }
                    }

                    // Кнопка очистить
                    IconButton(onClick = { showClearDialog = true }) {
                        Icon(
                            Icons.Default.DeleteSweep,
                            contentDescription = "Очистить"
                        )
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
            if (isLoading) {
                LoadingIndicator()
            } else if (filteredNotifications.isEmpty()) {
                EmptyNotificationsContent()
            } else {
                NotificationList(
                    notifications = filteredNotifications,
                    onNotificationClick = { notification ->
                        viewModel.markAsRead(notification)
                        onNotificationClick(notification)
                    },
                    onDeleteClick = { notification ->
                        viewModel.deleteNotification(notification)
                    },
                    onToggleEnabled = { notification ->
                        viewModel.toggleNotificationEnabled(notification)
                    }
                )
            }
        }
    }

    // Диалог подтверждения очистки
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить уведомления") },
            text = { Text("Вы уверены, что хотите удалить все уведомления?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        notifications.forEach { viewModel.deleteNotification(it) }
                        showClearDialog = false
                    }
                ) {
                    Text("Очистить", color = Theme.colors.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }

    // Меню фильтрации
    DropdownMenu(
        expanded = showFilterMenu,
        onDismissRequest = { showFilterMenu = false }
    ) {
        DropdownMenuItem(
            text = { Text("Все") },
            onClick = {
                filterType = null
                showFilterMenu = false
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            }
        )

        // Добавляем все типы уведомлений
        DropdownMenuItem(
            text = { Text("Push-уведомления") },
            onClick = {
                filterType = NotificationType.PUSH
                showFilterMenu = false
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Notifications,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Напоминания") },
            onClick = {
                filterType = NotificationType.REMINDER
                showFilterMenu = false
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Alarm,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Акции") },
            onClick = {
                filterType = NotificationType.PROMO
                showFilterMenu = false
            },
            leadingIcon = {
                Icon(
                    Icons.Default.LocalOffer,
                    contentDescription = null
                )
            }
        )

        DropdownMenuItem(
            text = { Text("Обновления") },
            onClick = {
                filterType = NotificationType.UPDATE
                showFilterMenu = false
            },
            leadingIcon = {
                Icon(
                    Icons.Default.SystemUpdate,
                    contentDescription = null
                )
            }
        )
    }
}

@Composable
fun EmptyNotificationsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Theme.colors.primary.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет уведомлений",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Здесь будут появляться уведомления от приложения",
            style = MaterialTheme.typography.bodyMedium,
            color = Theme.colors.onSurfaceVariant
        )
    }
}

@Composable
fun NotificationList(
    notifications: List<NotificationEntity>,
    onNotificationClick: (NotificationEntity) -> Unit,
    onDeleteClick: (NotificationEntity) -> Unit,
    onToggleEnabled: (NotificationEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = notifications,
            key = { it.id }
        ) { notification ->
            NotificationItem(
                notification = notification,
                onClick = { onNotificationClick(notification) },
                onDelete = { onDeleteClick(notification) },
                onToggleEnabled = { onToggleEnabled(notification) }
            )
        }
    }
}

@Composable
fun NotificationItem(
    notification: NotificationEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onToggleEnabled: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                Theme.colors.surface
            else
                Theme.colors.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Иконка типа уведомления
            Icon(
                imageVector = getNotificationIcon(notification.type),
                contentDescription = null,
                tint = if (notification.isEnabled)
                    Theme.colors.primary
                else
                    Theme.colors.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Контент
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (notification.isEnabled)
                        Theme.colors.onSurface
                    else
                        Theme.colors.onSurfaceVariant.copy(alpha = 0.5f)
                )

                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (notification.isEnabled)
                        Theme.colors.onSurfaceVariant
                    else
                        Theme.colors.onSurfaceVariant.copy(alpha = 0.3f),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatNotificationTime(notification.createdAt),
                        style = MaterialTheme.typography.labelSmall,
                        color = Theme.colors.onSurfaceVariant.copy(alpha = 0.7f)
                    )

                    if (!notification.isRead) {
                        Surface(
                            modifier = Modifier.size(8.dp),
                            shape = CircleShape,
                            color = Theme.colors.primary
                        ) {}
                    }
                }
            }

            // Кнопки действий
            Row {
                // Toggle enabled/disabled
                IconButton(
                    onClick = onToggleEnabled,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (notification.isEnabled)
                            Icons.Default.Notifications
                        else
                            Icons.Default.NotificationsOff,
                        contentDescription = if (notification.isEnabled)
                            "Отключить"
                        else
                            "Включить",
                        tint = if (notification.isEnabled)
                            Theme.colors.primary
                        else
                            Theme.colors.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Удалить",
                        tint = Theme.colors.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

// Вспомогательные функции
fun getNotificationIcon(type: NotificationType): ImageVector {
    return when (type) {
        NotificationType.PUSH -> Icons.Default.Notifications
        NotificationType.REMINDER -> Icons.Default.Alarm
        NotificationType.PROMO -> Icons.Default.LocalOffer
        NotificationType.UPDATE -> Icons.Default.SystemUpdate
    }
}

fun getNotificationTypeName(type: NotificationType): String {
    return when (type) {
        NotificationType.PUSH -> "Push-уведомления"
        NotificationType.REMINDER -> "Напоминания"
        NotificationType.PROMO -> "Акции"
        NotificationType.UPDATE -> "Обновления"
    }
}

fun formatNotificationTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60 * 1000 -> "только что"
        diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)} мин назад"
        diff < 24 * 60 * 60 * 1000 -> {
            val hours = diff / (60 * 60 * 1000)
            "$hours ч назад"
        }
        else -> {
            val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}