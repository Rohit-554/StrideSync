package io.jadu.strideSync.data.local.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import io.jadu.strideSync.data.local.entity.ActivityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<ActivityEntity>)

    @Query("SELECT * FROM activities ORDER BY createdAt DESC")
    fun getAll(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getById(id: String): ActivityEntity?

    @Query("SELECT * FROM activities WHERE synced = 0")
    fun getUnsynced(): Flow<List<ActivityEntity>>

    @Delete
    suspend fun delete(entity: ActivityEntity)

    @Query("DELETE FROM activities")
    suspend fun deleteAll()
}
