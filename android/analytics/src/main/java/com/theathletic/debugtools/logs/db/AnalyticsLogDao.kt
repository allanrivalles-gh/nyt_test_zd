package com.theathletic.debugtools.logs.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.theathletic.debugtools.logs.AnalyticsLogModel
import kotlinx.coroutines.flow.Flow

@Dao
abstract class AnalyticsLogDao {
    // TT Inserts
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLog(values: AnalyticsLogModel)

    // TT Gets
    @Query("SELECT * FROM analytics_history_log ORDER BY uid DESC")
    abstract fun getAllLogs(): Flow<List<AnalyticsLogModel>>

    @Query("SELECT * FROM analytics_history_log WHERE isNoisy=0 ORDER BY uid DESC")
    abstract fun getNonNoisyLogs(): Flow<List<AnalyticsLogModel>>

    // TT Deletes
    @Query("DELETE FROM analytics_history_log")
    abstract suspend fun clearAllData()
}