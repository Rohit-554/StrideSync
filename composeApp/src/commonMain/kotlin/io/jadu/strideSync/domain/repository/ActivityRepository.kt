package io.jadu.strideSync.domain.repository

import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.coroutines.flow.Flow

interface ActivityRepository {
    suspend fun createActivity(
        activity: Activity,
        gpsPoints: List<GpsPoint>
    ): Result<Activity>

    suspend fun getActivityById(id: String): Result<Activity>

    suspend fun getMyActivities(page: Int = 0, size: Int = 20): Result<List<Activity>>

    fun getLocalActivities(): Flow<List<Activity>>

    suspend fun syncUnsyncedActivities(): Result<Unit>

    suspend fun deleteActivity(id: String): Result<Unit>
}
