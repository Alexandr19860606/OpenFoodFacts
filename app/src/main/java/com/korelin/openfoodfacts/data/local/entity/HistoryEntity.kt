package com.korelin.openfoodfacts.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productCode: String,
    val viewedAt: Long = System.currentTimeMillis()
)