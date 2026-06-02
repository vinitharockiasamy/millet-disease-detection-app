package com.pearlmillet.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userName: String,
    val result: String,
    val confidence: Float,
    val imagePath: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScanResult

        if (id != other.id) return false
        if (userName != other.userName) return false
        if (result != other.result) return false
        if (confidence != other.confidence) return false
        if (imagePath != other.imagePath) return false
        if (timestamp != other.timestamp) return false

        return true
    }

    override fun hashCode(): Int {
        var result1 = id.hashCode()
        result1 = 31 * result1 + userName.hashCode()
        result1 = 31 * result1 + result.hashCode()
        result1 = 31 * result1 + confidence.hashCode()
        result1 = 31 * result1 + imagePath.hashCode()
        result1 = 31 * result1 + timestamp.hashCode()
        return result1
    }
}
