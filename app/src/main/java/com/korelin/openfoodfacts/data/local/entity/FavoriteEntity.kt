package com.korelin.openfoodfacts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val productCode: String,
    val productName: String?,
    val brand: String?,
    val imageUrl: String?,
    val addedAt: Long = System.currentTimeMillis()
)