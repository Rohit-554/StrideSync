package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import io.jadu.strideSync.tracking.TrackingEngine
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun RecordButton(
    state: TrackingEngine.RecordingState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = containerColorFor(state)
    val icon = iconFor(state)
    val contentDesc = contentDescriptionFor(state)
    val scale = pulseScaleIfRecording(state)

    Box(
        modifier = modifier
            .size(Spacing.d80)
            .scale(scale)
            .shadow(
                elevation = Spacing.lg,
                shape = CircleShape,
                ambientColor = containerColor,
                spotColor = containerColor
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = { onRecordAction(state, onStart, onStop) },
            modifier = Modifier
                .size(Spacing.d80)
                .background(containerColor, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = Color.White,
                modifier = Modifier.size(Spacing.d40)
            )
        }
    }
}

private fun containerColorFor(state: TrackingEngine.RecordingState): Color = when (state) {
    TrackingEngine.RecordingState.Idle -> StrideColors.Success
    TrackingEngine.RecordingState.Recording -> StrideColors.BrandPrimary
    TrackingEngine.RecordingState.Paused -> StrideColors.Warning
}

private fun iconFor(state: TrackingEngine.RecordingState): ImageVector = when (state) {
    TrackingEngine.RecordingState.Idle -> Icons.Default.PlayArrow
    TrackingEngine.RecordingState.Recording -> Icons.Default.Stop
    TrackingEngine.RecordingState.Paused -> Icons.Default.PlayArrow
}

private fun contentDescriptionFor(state: TrackingEngine.RecordingState): String = when (state) {
    TrackingEngine.RecordingState.Idle -> "Start recording"
    TrackingEngine.RecordingState.Recording -> "Stop recording"
    TrackingEngine.RecordingState.Paused -> "Resume recording"
}

@Composable
private fun pulseScaleIfRecording(state: TrackingEngine.RecordingState): Float {
    if (state != TrackingEngine.RecordingState.Recording) return 1f

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )
    return pulse
}

private fun onRecordAction(
    state: TrackingEngine.RecordingState,
    onStart: () -> Unit,
    onStop: () -> Unit
) {
    when (state) {
        TrackingEngine.RecordingState.Idle,
        TrackingEngine.RecordingState.Paused -> onStart()
        TrackingEngine.RecordingState.Recording -> onStop()
    }
}
