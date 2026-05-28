package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.GpsPointDto
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
            val userId = call.principal<JWTPrincipal>()?.subject
                ?.let { runCatching { UUID.fromString(it) }.getOrNull() }

            if (userId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                return@webSocket
            }

            val activityId = runCatching {
                activityService.create(
                    userId = userId,
                    request = io.jadu.strideSync.dto.CreateActivityRequest(
                        sportType = DEFAULT_SPORT_TYPE,
                        title = DEFAULT_TITLE,
                        gpsPoints = emptyList(),
                        startedAt = System.currentTimeMillis(),
                    ),
                )
            }.getOrElse { e ->
                close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "Failed to create activity: ${e.message}"))
                return@webSocket
            }.id.let { UUID.fromString(it) }

            val buffer = mutableListOf<GpsPointDto>()
            val bufferLock = Any()

            val batchJob = launch {
                while (isActive) {
                    delay(BATCH_INTERVAL_MS)
                    val batch = synchronized(bufferLock) {
                        if (buffer.isEmpty()) return@synchronized emptyList()
                        buffer.toList().also { buffer.clear() }
                    }
                    if (batch.isNotEmpty()) {
                        runCatching {
                            gpsPointRepository.batchInsert(activityId, batch)
                        }.onFailure { e ->
                            call.application.environment.log.error("Batch insert failed", e)
                        }
                    }
                }
            }

            try {
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
                            val text = frame.readText()
                            val point = runCatching {
                                Json.decodeFromString<GpsPointDto>(text)
                            }.getOrElse {
                                send(Frame.Text("""{"error":"Invalid GPS point format"}"""))
                                continue
                            }
                            synchronized(bufferLock) {
                                buffer.add(point)
                            }
                        }

                        else -> { /* ignore non-text frames */ }
                    }
                }
            } catch (_: ClosedReceiveChannelException) {
                // Client disconnected normally
            } finally {
                batchJob.cancel()

                val remaining = synchronized(bufferLock) {
                    buffer.toList().also { buffer.clear() }
                }
                if (remaining.isNotEmpty()) {
                    runCatching {
                        gpsPointRepository.batchInsert(activityId, remaining)
                    }.onFailure { e ->
                        call.application.environment.log.error("Final batch insert failed", e)
                    }
                }

                runCatching {
                    activityService.finalizeActivity(activityId)
                }.onFailure { e ->
                    call.application.environment.log.error("Failed to finalize activity", e)
                }
            }
        }
    }
}
