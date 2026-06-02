package com.pearlmillet.app.data.database

import androidx.room.ColumnInfo

data class ScanResultListItem(
    val id: Long,
    val result: String,
    val confidence: Float,
    val timestamp: Long
)
