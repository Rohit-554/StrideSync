package io.jadu.strideSync.data.local

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import io.jadu.strideSync.data.local.dao.ActivityDao
import io.jadu.strideSync.data.local.dao.FeedDao
import io.jadu.strideSync.data.local.dao.GpsPointDao
import io.jadu.strideSync.data.local.entity.ActivityEntity
import io.jadu.strideSync.data.local.entity.CachedFeedEntity
import io.jadu.strideSync.data.local.entity.GpsPointEntity
import io.jadu.strideSync.data.local.entity.PendingUploadEntity

@Database(
    entities = [
        ActivityEntity::class,
        GpsPointEntity::class,
        CachedFeedEntity::class,
        PendingUploadEntity::class
    ],
    version = 2
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDao(): ActivityDao
    abstract fun gpsPointDao(): GpsPointDao
    abstract fun feedDao(): FeedDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase>
