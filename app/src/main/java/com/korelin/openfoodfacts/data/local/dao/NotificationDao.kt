package com.korelin.openfoodfacts.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.local.entity.NotificationType
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllNotifications(notifications: List<NotificationEntity>)

    @Update
    suspend fun updateNotification(notification: NotificationEntity)

    @Delete
    suspend fun deleteNotification(notification: NotificationEntity)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Long)

    @Query("SELECT * FROM notifications ORDER BY createdAt DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY createdAt DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE type = :type ORDER BY createdAt DESC")
    fun getNotificationsByType(type: NotificationType): Flow<List<NotificationEntity>>

    @Query("SELECT * FROM notifications WHERE id = :id")
    suspend fun getNotificationById(id: Long): NotificationEntity?

    @Query("SELECT * FROM notifications WHERE notificationId = :notificationId")
    suspend fun getNotificationByFirebaseId(notificationId: String): NotificationEntity?

    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: Long)

    @Query("UPDATE notifications SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllAsRead()

    @Query("UPDATE notifications SET isEnabled = :enabled WHERE id = :id")
    suspend fun setNotificationEnabled(id: Long, enabled: Boolean)

    @Query("DELETE FROM notifications WHERE createdAt < :cutoffTime")
    suspend fun deleteOldNotifications(cutoffTime: Long)

    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    suspend fun getUnreadCount(): Int

    @Query("SELECT * FROM notifications WHERE scheduledTime IS NOT NULL AND scheduledTime > :currentTime")
    suspend fun getPendingNotifications(currentTime: Long): List<NotificationEntity>
}