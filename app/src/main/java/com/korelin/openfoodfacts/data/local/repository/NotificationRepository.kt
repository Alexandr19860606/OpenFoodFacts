package com.korelin.openfoodfacts.data.repository

import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.local.entity.NotificationType
import com.korelin.openfoodfacts.data.local.repository.LocalNotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val localNotificationRepository: LocalNotificationRepository
) {

    fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return localNotificationRepository.getAllNotifications()
    }

    fun getUnreadNotifications(): Flow<List<NotificationEntity>> {
        return localNotificationRepository.getUnreadNotifications()
    }

    suspend fun getUnreadCount(): Int {
        return localNotificationRepository.getUnreadCount()
    }

    suspend fun saveNotification(notification: NotificationEntity) {
        localNotificationRepository.insertNotification(notification)
    }

    suspend fun getNotificationById(id: Long): NotificationEntity? {
        return localNotificationRepository.getNotificationById(id)
    }

    suspend fun getNotificationByFirebaseId(notificationId: String): NotificationEntity? {
        return localNotificationRepository.getNotificationByFirebaseId(notificationId)
    }

    suspend fun markAsRead(id: Long) {
        localNotificationRepository.markAsRead(id)
    }

    suspend fun markAllAsRead() {
        localNotificationRepository.markAllAsRead()
    }

    suspend fun deleteNotification(id: Long) {
        localNotificationRepository.deleteNotificationById(id)
    }

    suspend fun setNotificationEnabled(id: Long, enabled: Boolean) {
        localNotificationRepository.setNotificationEnabled(id, enabled)
    }

    suspend fun deleteOldNotifications(days: Int = 30) {
        localNotificationRepository.deleteOldNotifications(days)
    }

    suspend fun getPendingNotifications(): List<NotificationEntity> {
        return localNotificationRepository.getPendingNotifications()
    }

    suspend fun createReminder(
        title: String,
        message: String,
        productCode: String?,
        productName: String?,
        scheduledTime: Long
    ): NotificationEntity {
        val notification = NotificationEntity(
            notificationId = "reminder_${System.currentTimeMillis()}",
            title = title,
            message = message,
            productCode = productCode,
            productName = productName,
            type = NotificationType.REMINDER,
            scheduledTime = scheduledTime,
            isRead = false,
            isEnabled = true
        )
        localNotificationRepository.insertNotification(notification)
        return notification
    }
}