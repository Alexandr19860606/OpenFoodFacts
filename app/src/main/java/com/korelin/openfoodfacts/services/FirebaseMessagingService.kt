package com.korelin.openfoodfacts.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.korelin.openfoodfacts.MainActivity
import com.korelin.openfoodfacts.R
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.data.local.entity.NotificationType
import com.korelin.openfoodfacts.data.repository.NotificationRepository
import com.korelin.openfoodfacts.utils.FileLogger
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var notificationRepository: NotificationRepository

    companion object {
        const val CHANNEL_ID = "openfoodfacts_notifications"
        const val CHANNEL_NAME = "Open Food Facts"
        const val NOTIFICATION_ID = 1001
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        FileLogger.d("FCM", "Новый токен: $token")
        // Отправляем токен на сервер если нужно
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        FileLogger.d("FCM", "Получено сообщение: ${remoteMessage.data}")

        // Проверяем, есть ли данные в сообщении
        remoteMessage.data.let { data ->
            if (data.isNotEmpty()) {
                handleDataMessage(data)
            }
        }

        // Проверяем, есть ли уведомление
        remoteMessage.notification?.let { notification ->
            handleNotificationMessage(notification, remoteMessage.data)
        }
    }

    private fun handleDataMessage(data: Map<String, String>) {
        val title = data["title"] ?: "Open Food Facts"
        val message = data["body"] ?: "Новое уведомление"
        val productCode = data["product_code"]
        val productName = data["product_name"]
        val type = data["type"] ?: "PUSH"
        val notificationId = data["notification_id"] ?: System.currentTimeMillis().toString()

        // Сохраняем уведомление в БД
        CoroutineScope(Dispatchers.IO).launch {
            val notification = NotificationEntity(
                notificationId = notificationId,
                title = title,
                message = message,
                productCode = productCode,
                productName = productName,
                type = NotificationType.valueOf(type),
                createdAt = System.currentTimeMillis(),
                isRead = false,
                isEnabled = true
            )
            notificationRepository.saveNotification(notification)
        }

        // Показываем уведомление
        showNotification(title, message, productCode, productName)
    }

    private fun handleNotificationMessage(
        notification: RemoteMessage.Notification,
        data: Map<String, String>
    ) {
        val title = notification.title ?: "Open Food Facts"
        val message = notification.body ?: "Новое уведомление"
        val productCode = data["product_code"]
        val productName = data["product_name"]

        // Сохраняем в БД
        CoroutineScope(Dispatchers.IO).launch {
            val notif = NotificationEntity(
                notificationId = System.currentTimeMillis().toString(),
                title = title,
                message = message,
                productCode = productCode,
                productName = productName,
                type = NotificationType.PUSH,
                createdAt = System.currentTimeMillis(),
                isRead = false,
                isEnabled = true
            )
            notificationRepository.saveNotification(notif)
        }

        // Показываем уведомление
        showNotification(title, message, productCode, productName)
    }

    private fun showNotification(
        title: String,
        message: String,
        productCode: String?,
        productName: String?
    ) {
        createNotificationChannel()

        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            productCode?.let { putExtra("product_code", it) }
            productName?.let { putExtra("product_name", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для уведомлений Open Food Facts"
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}