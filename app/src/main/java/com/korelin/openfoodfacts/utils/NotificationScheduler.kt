package com.korelin.openfoodfacts.utils

import android.content.Context
import androidx.work.*
import com.korelin.openfoodfacts.data.local.entity.NotificationEntity
import com.korelin.openfoodfacts.workers.NotificationWorker
import java.util.concurrent.TimeUnit

object NotificationScheduler {

    fun scheduleNotification(
        context: Context,
        notification: NotificationEntity,
        delayInMillis: Long
    ) {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    NotificationWorker.NOTIFICATION_ID to notification.id,
                    NotificationWorker.TITLE to notification.title,
                    NotificationWorker.MESSAGE to notification.message,
                    NotificationWorker.PRODUCT_CODE to notification.productCode,
                    NotificationWorker.PRODUCT_NAME to notification.productName
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                1,
                TimeUnit.MINUTES
            )
            .addTag("notification_${notification.id}")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun cancelNotification(context: Context, notificationId: Long) {
        WorkManager.getInstance(context).cancelAllWorkByTag("notification_$notificationId")
    }

    fun cancelAllNotifications(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag("notification")
    }

    fun scheduleReminder(
        context: Context,
        title: String,
        message: String,
        productCode: String?,
        productName: String?,
        delayInMillis: Long
    ): Long {
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .setInputData(
                workDataOf(
                    NotificationWorker.TITLE to title,
                    NotificationWorker.MESSAGE to message,
                    NotificationWorker.PRODUCT_CODE to productCode,
                    NotificationWorker.PRODUCT_NAME to productName
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                    .build()
            )
            .addTag("reminder")
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
        return workRequest.id.hashCode().toLong()
    }
}