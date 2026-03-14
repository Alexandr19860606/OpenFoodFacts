package com.korelin.openfoodfacts.di

import com.korelin.openfoodfacts.data.api.FoodApiService
import com.korelin.openfoodfacts.data.api.RetrofitClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideFoodApiService(): FoodApiService {
        return RetrofitClient.apiService
    }
}