package com.korelin.openfoodfacts.data.local.repository

import com.korelin.openfoodfacts.data.local.AppDatabase
import com.korelin.openfoodfacts.data.local.dao.NotificationDao
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.local.entity.NotificationType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalNotificationRepository @Inject constructor(
    private val db: AppDatabase
) {

    private val notificationDao: NotificationDao = db.notificationDao()

    suspend fun insertNotification(notification: NotificationEntity) {
        notificationDao.insertNotification(notification)
    }

    suspend fun insertAllNotifications(notifications: List<NotificationEntity>) {
        notificationDao.insertAllNotifications(notifications)
    }

    suspend fun updateNotification(notification: NotificationEntity) {
        notificationDao.updateNotification(notification)
    }

    suspend fun deleteNotification(notification: NotificationEntity) {
        notificationDao.deleteNotification(notification)
    }

    suspend fun deleteNotificationById(id: Long) {
        notificationDao.deleteNotificationById(id)
    }

    fun getAllNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getAllNotifications()
    }

    fun getUnreadNotifications(): Flow<List<NotificationEntity>> {
        return notificationDao.getUnreadNotifications()
    }

    fun getNotificationsByType(type: NotificationType): Flow<List<NotificationEntity>> {
        return notificationDao.getNotificationsByType(type)
    }

    suspend fun getNotificationById(id: Long): NotificationEntity? {
        return notificationDao.getNotificationById(id)
    }

    suspend fun getNotificationByFirebaseId(notificationId: String): NotificationEntity? {
        return notificationDao.getNotificationByFirebaseId(notificationId)
    }

    suspend fun markAsRead(id: Long) {
        notificationDao.markAsRead(id)
    }

    suspend fun markAllAsRead() {
        notificationDao.markAllAsRead()
    }

    suspend fun setNotificationEnabled(id: Long, enabled: Boolean) {
        notificationDao.setNotificationEnabled(id, enabled)
    }

    suspend fun deleteOldNotifications(days: Int = 30) {
        val cutoffTime = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000)
        notificationDao.deleteOldNotifications(cutoffTime)
    }

    suspend fun getUnreadCount(): Int {
        return notificationDao.getUnreadCount()
    }

    suspend fun getPendingNotifications(): List<NotificationEntity> {
        return notificationDao.getPendingNotifications(System.currentTimeMillis())
    }
}