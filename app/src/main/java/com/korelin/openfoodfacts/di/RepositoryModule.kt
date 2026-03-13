package com.korelin.openfoodfacts.di

import com.korelin.openfoodfacts.data.local.repository.LocalRepository
import com.korelin.openfoodfacts.data.repository.ProductRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideProductRepository(): ProductRepository {
        return ProductRepository()
    }

    @Provides
    @Singleton
    fun provideLocalRepository(
        database: com.korelin.openfoodfacts.data.local.AppDatabase
    ): LocalRepository {
        return LocalRepository(database)
    }
}