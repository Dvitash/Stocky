package com.example.stocky

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist")
data class WatchlistEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val symbol: String,
    val addedAt: Long
)
