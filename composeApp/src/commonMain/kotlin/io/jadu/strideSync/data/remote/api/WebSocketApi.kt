package io.jadu.strideSync.data.remote.api

import io.jadu.strideSync.AppConfig
import io.jadu.strideSync.data.remote.dto.GpsPointDto
import io.jadu.strideSync.domain.model.GpsPoint
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

private const val TRACKING_PATH = "/ws/track"

class WebSocketApi(
    private val client: HttpClient,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var session: DefaultClientWebSocketSession? = null
    private var incomingJob: Job? = null

    suspend fun connectTracking(onPoint: (GpsPoint) -> Unit) {
        if (session?.isActive == true) return

        val wsUrl = AppConfig.BASE_URL
            .replace("https://", "wss://")
            .replace("http://", "ws://") + TRACKING_PATH

        session = client.webSocketSession(wsUrl)

        incomingJob?.cancel()
        incomingJob = scope.launch {
            val activeSession = session ?: return@launch
            runCatching {
                for (frame in activeSession.incoming) {
                    if (frame is Frame.Text) {
                        decodeGpsPoint(frame.readText())?.let(onPoint)
                    }
                }
            }.onFailure {
                disconnect()
            }
        }
    }

    suspend fun sendPoint(point: GpsPoint) {
        val activeSession = session?.takeIf { it.isActive }
            ?: error("Tracking WebSocket is not connected")

        activeSession.send(Frame.Text(json.encodeToString(point.toDto())))
    }

    fun disconnect() {
        incomingJob?.cancel()
        incomingJob = null

        val activeSession = session
        session = null
        if (activeSession?.isActive == true) {
            scope.launch {
                runCatching { activeSession.close() }
            }
        }
    }

    private fun decodeGpsPoint(payload: String): GpsPoint? =
        runCatching { json.decodeFromString<GpsPointDto>(payload).toDomain() }.getOrNull()
}

private fun GpsPoint.toDto(): GpsPointDto = GpsPointDto(
    lat = lat,
    lng = lng,
    altitude = altitude,
    speed = speed,
    timestamp = timestamp,
)

private fun GpsPointDto.toDomain(): GpsPoint = GpsPoint(
    lat = lat,
    lng = lng,
    altitude = altitude,
    speed = speed,
    timestamp = timestamp,
)
