package com.korelin.openfoodfacts.di

import com.korelin.openfoodfacts.data.local.repository.LocalNotificationRepository
import com.korelin.openfoodfacts.data.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Provides
    @Singleton
    fun provideLocalNotificationRepository(
        database: com.korelin.openfoodfacts.data.local.AppDatabase
    ): LocalNotificationRepository {
        return LocalNotificationRepository(database)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        localNotificationRepository: LocalNotificationRepository
    ): NotificationRepository {
        return NotificationRepository(localNotificationRepository)
    }
}