package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.GpsPointDto
import io.jadu.strideSync.dto.CreateActivityRequest
import io.jadu.strideSync.repository.GpsPointRepository
import io.jadu.strideSync.service.ActivityService
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.util.UUID

private const val WS_TRACK_PATH = "/ws/track"
private const val BATCH_INTERVAL_MS = 5_000L
private const val DEFAULT_SPORT_TYPE = "run"
private const val DEFAULT_TITLE = "Live Tracking"

fun Route.trackingRoutes(
    activityService: ActivityService,
    gpsPointRepository: GpsPointRepository,
) {
    authenticate("auth-jwt") {
        webSocket(WS_TRACK_PATH) {
            val userId = extractAuthenticatedUserId()
            if (userId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                return@webSocket
            }

            val activityId = createLiveTrackingActivity(activityService, userId)
            if (activityId == null) {
                close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Failed to create activity"))
                return@webSocket
            }

            val buffer = mutableListOf<GpsPointDto>()
            val bufferLock = Any()
            val batchJob = launchBatchPersistJob(gpsPointRepository, activityId, buffer, bufferLock)

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val point = decodeGpsPoint(frame.readText())
                            if (point == null) {
                                send(Frame.Text("""{"error":"Invalid GPS point format"}"""))
                                continue
                            }
                            synchronized(bufferLock) {
                                buffer.add(point)
                            }
                        }

                        else -> Unit
                    }
                }
            } catch (channelClosed: ClosedReceiveChannelException) {
                call.application.environment.log.debug("Tracking socket closed: ${channelClosed.message}")
            } finally {
                batchJob.cancel()
                persistRemainingBatch(gpsPointRepository, activityId, buffer, bufferLock)
                finalizeTrackedActivity(activityService, activityId)
            }
        }
    }
}

private fun io.ktor.server.websocket.DefaultWebSocketServerSession.extractAuthenticatedUserId(): UUID? =
    call.principal<JWTPrincipal>()?.subject?.let { runCatching { UUID.fromString(it) }.getOrNull() }

private suspend fun io.ktor.server.websocket.DefaultWebSocketServerSession.createLiveTrackingActivity(
    activityService: ActivityService,
    userId: UUID,
): UUID? = runCatching {
    activityService.create(
        userId = userId,
        request = CreateActivityRequest(
            sportType = DEFAULT_SPORT_TYPE,
            title = DEFAULT_TITLE,
            gpsPoints = emptyList(),
            startedAt = System.currentTimeMillis(),
        ),
    )
}.mapCatching { UUID.fromString(it.id) }
    .onFailure { call.application.environment.log.error("Failed to create live tracking activity", it) }
    .getOrNull()

private fun io.ktor.server.websocket.DefaultWebSocketServerSession.launchBatchPersistJob(
    gpsPointRepository: GpsPointRepository,
    activityId: UUID,
    buffer: MutableList<GpsPointDto>,
    bufferLock: Any,
): Job = launch {
    while (isActive) {
        delay(BATCH_INTERVAL_MS)
        val batch = drainBatch(buffer, bufferLock)
        if (batch.isEmpty()) continue
        runCatching { gpsPointRepository.batchInsert(activityId, batch) }
            .onFailure { call.application.environment.log.error("Batch insert failed", it) }
    }
}

private fun decodeGpsPoint(payload: String): GpsPointDto? =
    runCatching { Json.decodeFromString<GpsPointDto>(payload) }.getOrNull()

private fun drainBatch(buffer: MutableList<GpsPointDto>, bufferLock: Any): List<GpsPointDto> =
    synchronized(bufferLock) { buffer.toList().also { buffer.clear() } }

private suspend fun io.ktor.server.websocket.DefaultWebSocketServerSession.persistRemainingBatch(
    gpsPointRepository: GpsPointRepository,
    activityId: UUID,
    buffer: MutableList<GpsPointDto>,
    bufferLock: Any,
) {
    val remainingBatch = drainBatch(buffer, bufferLock)
    if (remainingBatch.isEmpty()) return
    runCatching { gpsPointRepository.batchInsert(activityId, remainingBatch) }
        .onFailure { call.application.environment.log.error("Final batch insert failed", it) }
}

private suspend fun io.ktor.server.websocket.DefaultWebSocketServerSession.finalizeTrackedActivity(
    activityService: ActivityService,
    activityId: UUID,
) {
    runCatching { activityService.finalizeActivity(activityId) }
        .onFailure { call.application.environment.log.error("Failed to finalize activity", it) }
}
