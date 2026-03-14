package com.korelin.openfoodfacts.ui.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<NotificationEntity>>(emptyList())
    val notifications: StateFlow<List<NotificationEntity>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadNotifications()
        loadUnreadCount()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                notificationRepository.getAllNotifications().collect { notifications ->
                    _notifications.value = notifications
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            _unreadCount.value = notificationRepository.getUnreadCount()
        }
    }

    fun markAsRead(notification: NotificationEntity) {
        viewModelScope.launch {
            if (!notification.isRead) {
                notificationRepository.markAsRead(notification.id)
                loadUnreadCount()
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead()
            loadUnreadCount()
            loadNotifications() // Обновляем список
        }
    }

    fun deleteNotification(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(notification.id)
            loadUnreadCount()
        }
    }

    fun toggleNotificationEnabled(notification: NotificationEntity) {
        viewModelScope.launch {
            notificationRepository.setNotificationEnabled(notification.id, !notification.isEnabled)
            loadNotifications()
        }
    }

    fun createReminder(
        title: String,
        message: String,
        productCode: String?,
        productName: String?,
        scheduledTime: Long
    ) {
        viewModelScope.launch {
            notificationRepository.createReminder(
                title = title,
                message = message,
                productCode = productCode,
                productName = productName,
                scheduledTime = scheduledTime
            )
            loadNotifications()
        }
    }
}