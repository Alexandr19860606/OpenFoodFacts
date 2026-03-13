package com.korelin.openfoodfacts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.korelin.openfoodfacts.data.local.converter.Converters
import com.korelin.openfoodfacts.data.local.dao.NotificationDao
import com.korelin.openfoodfacts.data.local.dao.ProductDao
import com.korelin.openfoodfacts.data.local.entity.*

@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
        HistoryEntity::class,
        NotificationEntity::class  // Добавляем новую сущность
    ],
    version = 2,  // Увеличиваем версию БД
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun notificationDao(): NotificationDao  // Добавляем DAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "openfoodfacts_database"
                )
                    .fallbackToDestructiveMigration()  // Важно для миграции
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}