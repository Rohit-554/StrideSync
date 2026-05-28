package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.domain.model.GpsPoint
import io.jadu.strideSync.domain.model.SportType
import io.jadu.strideSync.domain.repository.ActivityRepository
import io.jadu.strideSync.tracking.TrackingEngine
import io.jadu.strideSync.utils.toUiMessage
import io.jadu.strideSync.tracking.DistanceCalculator
import io.jadu.strideSync.tracking.TrackingServiceController
import io.jadu.strideSync.utils.Formatters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecordViewModel(
    private val trackingEngine: TrackingEngine,
    private val activityRepository: ActivityRepository,
    private val trackingServiceController: TrackingServiceController
) : ViewModel() {

    data class RecordUiState(
        val state: TrackingEngine.RecordingState = TrackingEngine.RecordingState.Idle,
        val distanceKm: String = "0.00",
        val duration: String = "00:00",
        val pace: String = "—",
        val gpsPoints: List<GpsPoint> = emptyList(),
        val gpsSignalQuality: TrackingEngine.GpsSignalQuality = TrackingEngine.GpsSignalQuality.None,
        val selectedSport: SportType = SportType.Run,
        val isSaving: Boolean = false,
        val saveComplete: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(RecordUiState())
    val uiState: StateFlow<RecordUiState> = _uiState.asStateFlow()

    private var collectedGpsPoints: List<GpsPoint> = emptyList()
    private var completedDistanceMeters: Double = 0.0
    private var completedDurationSeconds: Long = 0L
    private var completedPaceSecondsPerKm: Double? = null
    private var preserveCompletedStats = false
    private var saveJobInFlight = false

    init {
        viewModelScope.launch {
            trackingEngine.trackingData.collect { data ->
                if (preserveCompletedStats && data.state == TrackingEngine.RecordingState.Idle) {
                    return@collect
                }
                _uiState.value = _uiState.value.copy(
                    state = data.state,
                    distanceKm = Formatters.metersToKmString(data.distanceMeters),
                    duration = Formatters.formatDuration(data.elapsedSeconds),
                    pace = data.paceSecondsPerKm?.let { Formatters.formatPace(it) } ?: "—",
                    gpsPoints = data.gpsPoints,
                    gpsSignalQuality = data.gpsSignalQuality
                )
            }
        }
    }

    fun selectSport(sport: SportType) {
        _uiState.value = _uiState.value.copy(selectedSport = sport)
    }

    fun hasLocationPermission(): Boolean = trackingEngine.requestPermission()

    fun startRecording() {
        preserveCompletedStats = false
        trackingServiceController.start()
        trackingEngine.startRecording()
    }

    fun pauseRecording() {
        trackingEngine.pauseRecording()
    }

    fun resumeRecording() {
        trackingEngine.resumeRecording()
    }

    fun stopAndSave() {
        val currentData = trackingEngine.trackingData.value
        completedDistanceMeters = currentData.distanceMeters
        completedDurationSeconds = currentData.elapsedSeconds
        completedPaceSecondsPerKm = currentData.paceSecondsPerKm
        preserveCompletedStats = true
        collectedGpsPoints = trackingEngine.stopAndSave()
        trackingServiceController.stop()
        if (completedDistanceMeters <= 0.0 && collectedGpsPoints.size > 1) {
            completedDistanceMeters = DistanceCalculator.cumulativeDistanceMeters(collectedGpsPoints)
        }
        _uiState.value = _uiState.value.copy(
            state = TrackingEngine.RecordingState.Idle,
            distanceKm = Formatters.metersToKmString(completedDistanceMeters),
            duration = Formatters.formatDuration(completedDurationSeconds),
            pace = completedPaceSecondsPerKm?.let { Formatters.formatPace(it) } ?: "—",
            gpsPoints = collectedGpsPoints
        )
    }

    fun saveActivity(title: String) {
        if (saveJobInFlight || _uiState.value.isSaving || _uiState.value.saveComplete) {
            return
        }
        viewModelScope.launch {
            saveJobInFlight = true
            try {
                _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)

                val currentState = _uiState.value
                val distanceM = completedDistanceMeters
                val durationSec = completedDurationSeconds

                val activity = Activity(
                    id = "local-${kotlin.time.Clock.System.now().toEpochMilliseconds()}",
                    userId = "",
                    sportType = currentState.selectedSport,
                    title = title,
                    distanceM = distanceM,
                    durationSec = durationSec,
                    elevationM = 0.0,
                    avgPace = completedPaceSecondsPerKm,
                    polyline = "",
                    startedAt = kotlin.time.Clock.System.now().toEpochMilliseconds() - (durationSec * 1000),
                    createdAt = kotlin.time.Clock.System.now().toEpochMilliseconds()
                )

                activityRepository.createActivity(activity, collectedGpsPoints)
                    .onSuccess {
                        preserveCompletedStats = false
                        collectedGpsPoints = emptyList()
                        completedDistanceMeters = 0.0
                        completedDurationSeconds = 0L
                        completedPaceSecondsPerKm = null
                        _uiState.value = _uiState.value.copy(isSaving = false, saveComplete = true)
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = error.toUiMessage()
                        )
                    }
            } finally {
                saveJobInFlight = false
            }
        }
    }

    fun discardActivity() {
        trackingEngine.stopAndSave()
        trackingServiceController.stop()
        _uiState.value = RecordUiState()
        collectedGpsPoints = emptyList()
        completedDistanceMeters = 0.0
        completedDurationSeconds = 0L
        completedPaceSecondsPerKm = null
        preserveCompletedStats = false
    }

    fun resetSaveState() {
        _uiState.value = _uiState.value.copy(saveComplete = false, errorMessage = null)
        saveJobInFlight = false
    }

}
