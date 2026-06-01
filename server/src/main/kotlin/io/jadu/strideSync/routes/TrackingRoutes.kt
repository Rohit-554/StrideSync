package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.GpsPointDto
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.routing.Route
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import java.util.UUID

private const val WS_TRACK_PATH = "/ws/track"

/**
 * Live tracking relay. This socket streams a recording in real time so followers
 * can watch it live. It deliberately does NOT persist an activity — the durable
 * record is created once, by `POST /activities`, when the athlete saves. Persisting
 * here too would create a duplicate activity for every recording.
 */
fun Route.trackingRoutes() {
    authenticate("auth-jwt") {
        webSocket(WS_TRACK_PATH) {
            val athleteId = authenticatedAthleteId()
            if (athleteId == null) {
                close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Invalid token"))
                return@webSocket
            }
            relayLivePoints()
        }
    }
}

private fun DefaultWebSocketServerSession.authenticatedAthleteId(): UUID? =
    call.principal<JWTPrincipal>()?.subject?.let { runCatching { UUID.fromString(it) }.getOrNull() }

private suspend fun DefaultWebSocketServerSession.relayLivePoints() {
    try {
        for (frame in incoming) {
            if (frame !is Frame.Text) continue
            if (livePointOrNull(frame.readText()) == null) {
                send(Frame.Text("""{"error":"Invalid GPS point format"}"""))
            }
        }
    } catch (channelClosed: ClosedReceiveChannelException) {
        call.application.environment.log.debug("Tracking socket closed: ${channelClosed.message}")
    }
}

private fun livePointOrNull(payload: String): GpsPointDto? =
    runCatching { Json.decodeFromString<GpsPointDto>(payload) }.getOrNull()
