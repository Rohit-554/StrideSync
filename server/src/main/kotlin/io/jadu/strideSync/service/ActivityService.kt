package io.jadu.strideSync.service

import io.jadu.strideSync.dto.ActivityResponse
import io.jadu.strideSync.dto.CreateActivityRequest
import io.jadu.strideSync.repository.ActivityRepository
import io.jadu.strideSync.repository.ActivityRow
import io.jadu.strideSync.repository.GpsPointRepository
import java.time.Instant
import java.util.UUID

class ActivityService(
    private val activityRepository: ActivityRepository,
    private val gpsPointRepository: GpsPointRepository,
) {

    suspend fun create(userId: UUID, request: CreateActivityRequest): ActivityResponse {
        val startedAt = Instant.ofEpochMilli(request.startedAt)
        val activityId = activityRepository.create(
            userId = userId,
            sportType = request.sportType,
            title = request.title,
            startedAt = startedAt,
        )

        if (request.gpsPoints.isNotEmpty()) {
            gpsPointRepository.batchInsert(activityId, request.gpsPoints)
        }

        val distanceM = StatsService.computeDistanceM(request.gpsPoints)
        val durationSec = StatsService.computeDurationSec(request.gpsPoints)
        val elevationM = StatsService.computeElevationGainM(request.gpsPoints)
        val avgPace = StatsService.computeAvgPaceSeckm(distanceM, durationSec)
        val polyline = StatsService.encodePolyline(request.gpsPoints)

        activityRepository.updateStats(
            id = activityId,
            distanceM = distanceM,
            durationSec = durationSec,
            elevationM = elevationM,
            avgPace = avgPace,
            polyline = polyline,
        )

        return activityRepository.findById(activityId)!!.toResponse()
    }

    suspend fun getById(id: UUID): ActivityResponse =
        activityRepository.findById(id)?.toResponse()
            ?: error("Activity not found")

    suspend fun getByUser(userId: UUID, page: Int, size: Int): List<ActivityResponse> =
        activityRepository.findByUser(userId, page, size).map { it.toResponse() }

    suspend fun delete(id: UUID, userId: UUID) {
        val deleted = activityRepository.delete(id, userId)
        if (!deleted) error("Activity not found or not owned by user")
    }

    suspend fun finalizeActivity(activityId: UUID) {
        val points = gpsPointRepository.findByActivity(activityId)
        if (points.isEmpty()) return

        val distanceM = StatsService.computeDistanceM(points)
        val durationSec = StatsService.computeDurationSec(points)
        val elevationM = StatsService.computeElevationGainM(points)
        val avgPace = StatsService.computeAvgPaceSeckm(distanceM, durationSec)
        val polyline = StatsService.encodePolyline(points)

        activityRepository.updateStats(
            id = activityId,
            distanceM = distanceM,
            durationSec = durationSec,
            elevationM = elevationM,
            avgPace = avgPace,
            polyline = polyline,
        )
    }

    private fun ActivityRow.toResponse() = ActivityResponse(
        id = id.toString(),
        userId = userId.toString(),
        sportType = sportType,
        title = title,
        distanceM = distanceM,
        durationSec = durationSec,
        elevationM = elevationM,
        avgPace = avgPace,
        polyline = polyline,
        startedAt = startedAt.toEpochMilli(),
        createdAt = createdAt.toEpochMilli(),
    )
}
