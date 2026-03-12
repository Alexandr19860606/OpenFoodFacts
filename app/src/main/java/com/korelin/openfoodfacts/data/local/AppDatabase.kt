package com.korelin.openfoodfacts.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.korelin.openfoodfacts.data.local.converter.Converters
import com.korelin.openfoodfacts.data.local.dao.ProductDao
import com.korelin.openfoodfacts.data.local.entity.ProductEntity
import com.korelin.openfoodfacts.data.local.entity.FavoriteEntity
import com.korelin.openfoodfacts.data.local.entity.HistoryEntity

@Database(
    entities = [
        ProductEntity::class,
        FavoriteEntity::class,
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

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
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}