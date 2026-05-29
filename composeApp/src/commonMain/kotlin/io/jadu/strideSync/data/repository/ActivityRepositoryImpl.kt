package io.jadu.strideSync.data.repository

import io.jadu.strideSync.data.local.dao.ActivityDao
import io.jadu.strideSync.data.local.dao.GpsPointDao
import io.jadu.strideSync.data.local.entity.ActivityEntity
import io.jadu.strideSync.data.local.entity.GpsPointEntity
import io.jadu.strideSync.data.remote.api.ActivityApi
import io.jadu.strideSync.data.remote.dto.ActivityResponse
import io.jadu.strideSync.data.remote.dto.CreateActivityRequest
import io.jadu.strideSync.data.remote.dto.GpsPointDto
import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.domain.model.GpsPoint
import io.jadu.strideSync.domain.model.SportType
import io.jadu.strideSync.domain.repository.ActivityRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ActivityRepositoryImpl(
    private val activityApi: ActivityApi,
    private val activityDao: ActivityDao,
    private val gpsPointDao: GpsPointDao
) : ActivityRepository {

    override suspend fun createActivity(
        activity: Activity,
        gpsPoints: List<GpsPoint>
    ): Result<Activity> = runCatching {
        val localActivity = if (activity.id.isBlank()) {
            activity.copy(id = "local-${kotlin.time.Clock.System.now().toEpochMilliseconds()}")
        } else {
            activity
        }
        activityDao.insert(localActivity.toEntity(synced = false))
        gpsPointDao.insertAll(gpsPoints.map { it.toEntity(localActivity.id) })

        val request = CreateActivityRequest(
            sportType = activity.sportType.name,
            title = activity.title,
            gpsPoints = gpsPoints.map { it.toDto() },
            startedAt = activity.startedAt
        )
        val remoteResult = runCatching { activityApi.create(request) }
        if (remoteResult.isFailure) {
            return@runCatching localActivity
        }
        val response = remoteResult.getOrThrow()
        val syncedActivity = response.toDomain()
        activityDao.delete(localActivity.toEntity(synced = false))
        gpsPointDao.deleteByActivityId(localActivity.id)
        activityDao.insert(syncedActivity.toEntity(synced = true))
        gpsPointDao.insertAll(gpsPoints.map { it.toEntity(syncedActivity.id) })
        syncedActivity
    }

    override suspend fun getActivityById(id: String): Result<Activity> = runCatching {
        val remote = runCatching { activityApi.getById(id) }
        if (remote.isSuccess) {
            val domain = remote.getOrThrow().toDomain()
            activityDao.insert(domain.toEntity(synced = true))
            domain
        } else {
            val local = activityDao.getById(id)
                ?: throw NoSuchElementException("Activity not found: $id")
            local.toDomain()
        }
    }

    override suspend fun getMyActivities(page: Int, size: Int): Result<List<Activity>> = runCatching {
        val activities = activityApi.getMine(page = page, size = size).map { it.toDomain() }
        activityDao.insertAll(activities.map { it.toEntity(synced = true) })
        activities
    }.recoverCatching {
        activityDao.getAll().first().map { it.toDomain() }
    }

    override fun getLocalActivities(): Flow<List<Activity>> =
        activityDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun syncUnsyncedActivities(): Result<Unit> = runCatching {
        val unsynced = activityDao.getUnsynced().first()
        for (entity in unsynced) {
            val gpsPoints = gpsPointDao.getByActivityId(entity.id).first()
            val request = CreateActivityRequest(
                sportType = entity.sportType,
                title = entity.title,
                gpsPoints = gpsPoints.map {
                    GpsPointDto(
                        lat = it.lat,
                        lng = it.lng,
                        altitude = it.altitude,
                        speed = it.speed,
                        timestamp = it.timestamp
                    )
                },
                startedAt = entity.startedAt
            )
            val response = activityApi.create(request)
            val syncedActivity = response.toDomain()
            val localGpsPoints = gpsPoints.map {
                GpsPoint(
                    lat = it.lat,
                    lng = it.lng,
                    altitude = it.altitude,
                    speed = it.speed,
                    timestamp = it.timestamp
                )
            }
            activityDao.delete(entity)
            gpsPointDao.deleteByActivityId(entity.id)
            activityDao.insert(syncedActivity.toEntity(synced = true))
            gpsPointDao.insertAll(localGpsPoints.map { it.toEntity(syncedActivity.id) })
        }
    }

    override suspend fun deleteActivity(id: String): Result<Unit> = runCatching {
        activityApi.delete(id)
        val local = activityDao.getById(id)
        if (local != null) {
            activityDao.delete(local)
            gpsPointDao.deleteByActivityId(id)
        }
    }
}

internal fun ActivityEntity.toDomain(): Activity = Activity(
    id = id,
    userId = userId,
    sportType = sportType.toSportType(),
    title = title,
    distanceM = distanceM,
    durationSec = durationSec,
    elevationM = elevationM,
    avgPace = avgPace,
    polyline = polyline,
    startedAt = startedAt,
    createdAt = createdAt
)

internal fun ActivityResponse.toDomain(): Activity = Activity(
    id = id,
    userId = userId,
    sportType = sportType.toSportType(),
    title = title,
    distanceM = distanceM,
    durationSec = durationSec.toLong(),
    elevationM = elevationM,
    avgPace = avgPace,
    polyline = polyline,
    startedAt = startedAt,
    createdAt = createdAt
)

internal fun Activity.toEntity(synced: Boolean): ActivityEntity = ActivityEntity(
    id = id,
    userId = userId,
    sportType = sportType.name,
    title = title,
    distanceM = distanceM,
    durationSec = durationSec,
    elevationM = elevationM,
    avgPace = avgPace,
    polyline = polyline,
    startedAt = startedAt,
    createdAt = createdAt,
    synced = synced
)

internal fun GpsPoint.toEntity(activityId: String): GpsPointEntity = GpsPointEntity(
    activityId = activityId,
    lat = lat,
    lng = lng,
    altitude = altitude,
    speed = speed,
    timestamp = timestamp
)

internal fun GpsPoint.toDto(): GpsPointDto = GpsPointDto(
    lat = lat,
    lng = lng,
    altitude = altitude,
    speed = speed,
    timestamp = timestamp
)

private fun String.toSportType(): SportType = when (trim().lowercase()) {
    "run", "running", "jog", "jogging" -> SportType.Run
    "ride", "riding", "bike", "biking", "cycling", "cycle" -> SportType.Ride
    "hike", "hiking", "trek", "trekking" -> SportType.Hike
    "swim", "swimming" -> SportType.Swim
    "walk", "walking" -> SportType.Walk
    "other" -> SportType.Other
    else -> SportType.Other
}
