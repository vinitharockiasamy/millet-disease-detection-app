package com.pearlmillet.app.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanResultDao {
    
    @Insert
    suspend fun insertScanResult(scanResult: ScanResult): Long
    
    // Project to ScanResultListItem to avoid loading imageBytes
    @Query("SELECT id, result, confidence, timestamp FROM scan_results ORDER BY timestamp DESC")
    fun getAllScanResults(): Flow<List<ScanResultListItem>>
    
    @Query("SELECT COUNT(*) FROM scan_results")
    suspend fun getScanResultsCount(): Int

    @Query("SELECT id, result, confidence, timestamp FROM scan_results WHERE userName = :userName ORDER BY timestamp DESC")
    fun getScanResultsByUser(userName: String): Flow<List<ScanResultListItem>>

    @Query("SELECT * FROM scan_results WHERE id = :id")
    suspend fun getScanResultById(id: Long): ScanResult?

    @Query("SELECT id, result, confidence, timestamp FROM scan_results WHERE id = :id")
    suspend fun getScanResultMetadataById(id: Long): ScanResultListItem?

    @Query("DELETE FROM scan_results WHERE id = :id")
    suspend fun deleteScanResult(id: Long)

    @Query("DELETE FROM scan_results")
    suspend fun deleteAllScanResults()
}
