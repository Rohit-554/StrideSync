package io.jadu.strideSync.ui.screens.record

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsBike
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jadu.strideSync.domain.model.SportType
import io.jadu.strideSync.map.PlatformMapView
import io.jadu.strideSync.permissions.Permission
import io.jadu.strideSync.permissions.PermissionStatus
import io.jadu.strideSync.permissions.rememberPermissionController
import io.jadu.strideSync.tracking.TrackingEngine
import io.jadu.strideSync.ui.components.GpsSignalIndicator
import io.jadu.strideSync.ui.components.RecordButton
import io.jadu.strideSync.ui.components.StrideChip
import io.jadu.strideSync.ui.theme.Background
import io.jadu.strideSync.ui.viewmodel.RecordViewModel
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun RecordScreen(
    viewModel: RecordViewModel = koinViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToSummary: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val permissionController = rememberPermissionController()
    val scope = rememberCoroutineScope()
    var locationPermission by remember { mutableStateOf(PermissionStatus.NOT_DETERMINED) }

    LaunchedEffect(Unit) {
        locationPermission = permissionController.checkPermission(Permission.LOCATION)
    }

    RecordScreenContent(
        uiState = uiState,
        locationPermission = locationPermission,
        onNavigateBack = onNavigateBack,
        onStartRecording = {
            scope.launch {
                val status = permissionController.requestPermission(Permission.LOCATION)
                locationPermission = status
                if (status == PermissionStatus.GRANTED) {
                    viewModel.startRecording()
                }
            }
        },
        onOpenPermissionSettings = { permissionController.openAppSettings() },
        onPauseRecording = { viewModel.pauseRecording() },
        onResumeRecording = { viewModel.resumeRecording() },
        onStopRecording = {
            viewModel.stopAndSave()
            onNavigateToSummary()
        },
        onSelectSport = { viewModel.selectSport(it) }
    )
}

@Composable
private fun RecordScreenContent(
    uiState: RecordViewModel.RecordUiState,
    locationPermission: PermissionStatus,
    onNavigateBack: () -> Unit,
    onStartRecording: () -> Unit,
    onOpenPermissionSettings: () -> Unit,
    onPauseRecording: () -> Unit,
    onResumeRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onSelectSport: (SportType) -> Unit
) {
    val isIdle = uiState.state == TrackingEngine.RecordingState.Idle
    val isPaused = uiState.state == TrackingEngine.RecordingState.Paused

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // Top bar
        RecordTopBar(
            onNavigateBack = onNavigateBack,
            showBack = isIdle
        )

        // Sport selector (only when idle)
        if (isIdle) {
            SportTypeSelector(
                selectedSport = uiState.selectedSport,
                onSportSelected = onSelectSport
            )
            PermissionMessage(
                status = locationPermission,
                onOpenSettings = onOpenPermissionSettings,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // GPS indicator (when recording or paused)
        if (!isIdle) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GpsSignalIndicator(quality = uiState.gpsSignalQuality)
                Text(
                    text = if (isPaused) "PAUSED" else "RECORDING",
                    color = if (isPaused) Color(0xFFFFC107) else Color(0xFFFF571B),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        RecordMap(
            gpsPoints = uiState.gpsPoints,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            isRecording = !isIdle
        )

        // Live stats (visible when recording or paused)
        if (!isIdle) {
            LiveStatsSection(
                distanceKm = uiState.distanceKm,
                duration = uiState.duration,
                pace = uiState.pace,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Bottom controls
        BottomControls(
            state = uiState.state,
            onStart = onStartRecording,
            onPause = onPauseRecording,
            onResume = onResumeRecording,
            onStop = onStopRecording,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun RecordTopBar(
    onNavigateBack: () -> Unit,
    showBack: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
        } else {
            Spacer(modifier = Modifier.size(48.dp))
        }
        Text(
            text = "Record",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = {}) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color(0xFF9BA3B2)
            )
        }
    }
}

@Composable
private fun SportTypeSelector(
    selectedSport: SportType,
    onSportSelected: (SportType) -> Unit
) {
    val sports: List<Pair<SportType, ImageVector>> = listOf(
        SportType.Run to Icons.AutoMirrored.Filled.DirectionsRun,
        SportType.Ride to Icons.AutoMirrored.Filled.DirectionsBike,
        SportType.Hike to Icons.Default.Terrain,
        SportType.Walk to Icons.AutoMirrored.Filled.DirectionsWalk
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sports.forEach { (sport, icon) ->
            StrideChip(
                text = sport.name,
                icon = icon,
                isSelected = selectedSport == sport,
                onClick = { onSportSelected(sport) }
            )
        }
    }
}

@Composable
private fun PermissionMessage(
    status: PermissionStatus,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (status) {
        PermissionStatus.DENIED -> Text(
            text = "Location permission is required to record an outdoor activity.",
            color = Color(0xFFFFC107),
            fontSize = 13.sp,
            modifier = modifier
        )
        PermissionStatus.DENIED_ALWAYS -> Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252830)),
            modifier = modifier
        ) {
            Text("Enable Location", color = Color.White)
        }
        else -> Unit
    }
}

@Composable
private fun RecordMap(
    gpsPoints: List<io.jadu.strideSync.domain.model.GpsPoint>,
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    var hasLocationFix by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        PlatformMapView(
            gpsPoints = gpsPoints,
            modifier = Modifier.fillMaxSize(),
            onFirstLocationFix = { hasLocationFix = true }
        )
        when {
            gpsPoints.isEmpty() && !hasLocationFix -> MapLoadingShimmer(
                modifier = Modifier.fillMaxSize()
            )
            gpsPoints.isEmpty() -> EmptyMapOverlay(
                isRecording = isRecording,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun MapLoadingShimmer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "mapShimmer")
    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "mapShimmerProgress"
    )

    val base = Color(0xFF252830)
    val highlight = Color(0xFF3A3F4B)
    val sweep = progress * 2000f

    Box(
        modifier = modifier
            .background(base)
            .background(
                Brush.linearGradient(
                    colors = listOf(base, highlight, base),
                    start = Offset(sweep - 600f, 0f),
                    end = Offset(sweep, 0f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Locating you…",
            color = Color(0xFF9BA3B2),
            fontSize = 14.sp
        )
    }
}

@Composable
private fun EmptyMapOverlay(
    isRecording: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.background(Color(0xFF111318).copy(alpha = 0.18f))
    ) {
        Text(
            text = if (isRecording) "Waiting for GPS..." else "GPS route will appear here",
            color = Color(0xFF9BA3B2),
            fontSize = 14.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .background(Color(0xCC111318), RoundedCornerShape(999.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun LiveStatsSection(
    distanceKm: String,
    duration: String,
    pace: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1F0F0B), RoundedCornerShape(16.dp))
            .padding(20.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LiveStatItem(value = distanceKm, label = "KM")
        StatDivider()
        LiveStatItem(value = duration, label = "TIME")
        StatDivider()
        LiveStatItem(value = pace, label = "PACE")
    }
}

@Composable
private fun LiveStatItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color(0xFFF0F0F0),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = Color(0xFF9BA3B2),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 36.dp)
            .background(Color(0xFF2C1B16))
    )
}

@Composable
private fun BottomControls(
    state: TrackingEngine.RecordingState,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIdle = state == TrackingEngine.RecordingState.Idle

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isIdle) {
            // Idle state: just the big start button
            RecordButton(
                state = state,
                onStart = onStart,
                onStop = onStop
            )
            Text(
                text = "Tap to start recording",
                color = Color(0xFF9BA3B2),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        } else {
            // Recording or paused: record button + pause/resume
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pause/Resume button
                if (state == TrackingEngine.RecordingState.Recording) {
                    Button(
                        onClick = onPause,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF252830),
                            contentColor = Color(0xFFF0F0F0)
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pause,
                            contentDescription = "Pause",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = onResume,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF252830),
                            contentColor = Color(0xFFF0F0F0)
                        ),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.DirectionsRun,
                            contentDescription = "Resume",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Main record button (acts as stop when recording)
                RecordButton(
                    state = state,
                    onStart = onStart,
                    onStop = onStop
                )
            }
        }
    }
}
