package com.korelin.openfoodfacts.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.korelin.openfoodfacts.MainActivity
import com.korelin.openfoodfacts.R
import com.korelin.openfoodfacts.data.repository.NotificationRepository
import com.korelin.openfoodfacts.services.FirebaseMessagingService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted private val params: WorkerParameters,
    private val notificationRepository: NotificationRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val notificationId = inputData.getLong("notification_id", 0)
                val title = inputData.getString("title") ?: "Напоминание"
                val message = inputData.getString("message") ?: "Пора проверить Open Food Facts"
                val productCode = inputData.getString("product_code")
                val productName = inputData.getString("product_name")

                // Проверяем, включено ли уведомление
                if (notificationId > 0) {
                    val notification = notificationRepository.getNotificationById(notificationId)
                    if (notification == null || !notification.isEnabled) {
                        return@withContext Result.success()
                    }
                }

                showNotification(title, message, productCode, productName, notificationId)
                Result.success()
            } catch (e: Exception) {
                e.printStackTrace()
                Result.failure()
            }
        }
    }

    private fun showNotification(
        title: String,
        message: String,
        productCode: String?,
        productName: String?,
        notificationId: Long
    ) {
        createNotificationChannel()

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            productCode?.let { putExtra("product_code", it) }
            productName?.let { putExtra("product_name", it) }
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(applicationContext, FirebaseMessagingService.CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId.toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FirebaseMessagingService.CHANNEL_ID,
                FirebaseMessagingService.CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Канал для уведомлений Open Food Facts"
            }

            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val NOTIFICATION_ID = "notification_id"
        const val TITLE = "title"
        const val MESSAGE = "message"
        const val PRODUCT_CODE = "product_code"
        const val PRODUCT_NAME = "product_name"
    }
}