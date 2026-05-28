package io.jadu.strideSync.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.jadu.strideSync.data.local.entity.GpsPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GpsPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<GpsPointEntity>)

    @Query("SELECT * FROM gps_points WHERE activityId = :activityId ORDER BY timestamp")
    fun getByActivityId(activityId: String): Flow<List<GpsPointEntity>>

    @Query("DELETE FROM gps_points WHERE activityId = :activityId")
    suspend fun deleteByActivityId(activityId: String)
}
