package io.jadu.strideSync.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.jadu.strideSync.data.local.entity.CachedFeedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<CachedFeedEntity>)

    @Query("SELECT * FROM cached_feed ORDER BY cachedAt DESC")
    fun getPage(): Flow<List<CachedFeedEntity>>

    @Query("DELETE FROM cached_feed WHERE cachedAt < :timestamp")
    suspend fun clearOlderThan(timestamp: Long)

    @Query("DELETE FROM cached_feed")
    suspend fun clearAll()
}
