package io.jadu.strideSync.tracking

import io.jadu.strideSync.data.remote.api.WebSocketApi
import io.jadu.strideSync.domain.model.GpsPoint
import io.jadu.strideSync.gps.GpsProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class TrackingEngine(
    private val gpsProvider: GpsProvider,
    private val webSocketApi: WebSocketApi,
) {

    enum class RecordingState {
        Idle,
        Recording,
        Paused
    }

    data class TrackingData(
        val state: RecordingState = RecordingState.Idle,
        val elapsedSeconds: Long = 0L,
        val distanceMeters: Double = 0.0,
        val paceSecondsPerKm: Double? = null,
        val gpsPoints: List<GpsPoint> = emptyList(),
        val gpsSignalQuality: GpsSignalQuality = GpsSignalQuality.None
    )

    enum class GpsSignalQuality {
        None,
        Weak,
        Strong
    }

    private val _trackingData = MutableStateFlow(TrackingData())
    val trackingData: StateFlow<TrackingData> = _trackingData.asStateFlow()

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var gpsJob: Job? = null
    private var timerJob: Job? = null

    private val bufferedPoints = mutableListOf<GpsPoint>()
    private val pendingWebSocketPoints = mutableListOf<GpsPoint>()
    private var lastGpsPoint: GpsPoint? = null
    private var startTimeMs: Long = 0L
    private var elapsedBeforePause: Long = 0L

    fun startRecording() {
        if (_trackingData.value.state == RecordingState.Recording) return

        val wasPaused = _trackingData.value.state == RecordingState.Paused
        if (!wasPaused) {
            // Fresh start
            bufferedPoints.clear()
            lastGpsPoint = null
            elapsedBeforePause = 0L
        }

        startTimeMs = kotlin.time.Clock.System.now().toEpochMilliseconds()
        _trackingData.value = _trackingData.value.copy(state = RecordingState.Recording)

        scope.launch {
            connectWebSocket()
            flushPendingWebSocketPoints()
        }

        gpsJob = gpsProvider.observeLocation()
            .onEach { point ->
                onNewGpsPoint(point)
            }
            .launchIn(scope)

        timerJob = scope.launch {
            while (isActive) {
                delay(1000L)
                val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
                val elapsed = elapsedBeforePause + ((now - startTimeMs) / 1000)
                val currentDistance = _trackingData.value.distanceMeters
                val pace = PaceCalculator.secondsPerKm(currentDistance, elapsed)
                _trackingData.value = _trackingData.value.copy(
                    elapsedSeconds = elapsed,
                    paceSecondsPerKm = pace
                )
            }
        }
    }

    fun pauseRecording() {
        if (_trackingData.value.state != RecordingState.Recording) return

        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        elapsedBeforePause += (now - startTimeMs) / 1000

        gpsJob?.cancel()
        gpsJob = null
        timerJob?.cancel()
        timerJob = null

        _trackingData.value = _trackingData.value.copy(state = RecordingState.Paused)
    }

    fun resumeRecording() {
        if (_trackingData.value.state != RecordingState.Paused) return
        startRecording()
    }

    fun stopAndSave(): List<GpsPoint> {
        gpsJob?.cancel()
        gpsJob = null
        timerJob?.cancel()
        timerJob = null
        gpsProvider.stopTracking()
        webSocketApi.disconnect()

        val finalPoints = bufferedPoints.toList()
        _trackingData.value = TrackingData()
        bufferedPoints.clear()
        pendingWebSocketPoints.clear()
        lastGpsPoint = null
        elapsedBeforePause = 0L
        return finalPoints
    }

    private fun onNewGpsPoint(point: GpsPoint) {
        bufferedPoints.add(point)

        val last = lastGpsPoint
        val newDistance = if (last != null) {
            _trackingData.value.distanceMeters + DistanceCalculator.haversineMeters(last, point)
        } else {
            _trackingData.value.distanceMeters
        }
        lastGpsPoint = point

        val signalQuality = when {
            point.speed != null && point.speed > 0 -> GpsSignalQuality.Strong
            point.altitude != null -> GpsSignalQuality.Weak
            else -> GpsSignalQuality.Weak
        }

        val currentElapsed = _trackingData.value.elapsedSeconds
        val pace = PaceCalculator.secondsPerKm(newDistance, currentElapsed)

        _trackingData.value = _trackingData.value.copy(
            distanceMeters = newDistance,
            gpsPoints = bufferedPoints.toList(),
            gpsSignalQuality = signalQuality,
            paceSecondsPerKm = pace
        )

        scope.launch {
            sendPointOverWebSocket(point)
        }
    }

    fun requestPermission(): Boolean {
        return gpsProvider.requestPermission()
    }

    private suspend fun connectWebSocket(): Boolean =
        runCatching {
            webSocketApi.connectTracking { /* server does not currently echo live points */ }
        }.isSuccess

    private suspend fun sendPointOverWebSocket(point: GpsPoint) {
        val connected = connectWebSocket()
        if (!connected) {
            pendingWebSocketPoints.add(point)
            return
        }

        flushPendingWebSocketPoints()
        runCatching {
            webSocketApi.sendPoint(point)
        }.onFailure {
            pendingWebSocketPoints.add(point)
            webSocketApi.disconnect()
        }
    }

    private suspend fun flushPendingWebSocketPoints() {
        if (pendingWebSocketPoints.isEmpty()) return

        val snapshot = pendingWebSocketPoints.toList()
        pendingWebSocketPoints.clear()
        snapshot.forEach { point ->
            runCatching {
                webSocketApi.sendPoint(point)
            }.onFailure {
                pendingWebSocketPoints.add(point)
                webSocketApi.disconnect()
                return
            }
        }
    }
}
