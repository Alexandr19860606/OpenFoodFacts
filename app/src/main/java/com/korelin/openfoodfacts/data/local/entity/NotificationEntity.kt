package com.korelin.openfoodfacts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val notificationId: String, // Уникальный ID из Firebase или наш
    val title: String,
    val message: String,
    val productCode: String?,
    val productName: String?,
    val type: NotificationType,
    val createdAt: Long = System.currentTimeMillis(),
    val scheduledTime: Long? = null,
    val isRead: Boolean = false,
    val isEnabled: Boolean = true,
    val action: String? = null
)

enum class NotificationType {
    PUSH,           // Получено из FCM
    REMINDER,       // Напоминание (пользовательское)
    PROMO,          // Акции/новости
    UPDATE          // Обновления приложения
}