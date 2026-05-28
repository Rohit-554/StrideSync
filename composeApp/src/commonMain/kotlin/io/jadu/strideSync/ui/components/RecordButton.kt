package io.jadu.strideSync.ui.components

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
import androidx.compose.ui.unit.dp
import io.jadu.strideSync.tracking.TrackingEngine

@Composable
fun RecordButton(
    state: TrackingEngine.RecordingState,
    onStart: () -> Unit,
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = when (state) {
        TrackingEngine.RecordingState.Idle -> Color(0xFF3ECF8E)      // Green
        TrackingEngine.RecordingState.Recording -> Color(0xFFFF571B) // Red
        TrackingEngine.RecordingState.Paused -> Color(0xFFFFC107)    // Yellow
    }

    val icon = when (state) {
        TrackingEngine.RecordingState.Idle -> Icons.Default.PlayArrow
        TrackingEngine.RecordingState.Recording -> Icons.Default.Stop
        TrackingEngine.RecordingState.Paused -> Icons.Default.PlayArrow
    }

    val contentDesc = when (state) {
        TrackingEngine.RecordingState.Idle -> "Start recording"
        TrackingEngine.RecordingState.Recording -> "Stop recording"
        TrackingEngine.RecordingState.Paused -> "Resume recording"
    }

    val scale = if (state == TrackingEngine.RecordingState.Recording) {
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
        pulse
    } else 1f

    Box(
        modifier = modifier
            .size(80.dp)
            .scale(scale)
            .shadow(
                elevation = 16.dp,
                shape = CircleShape,
                ambientColor = containerColor,
                spotColor = containerColor
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = {
                when (state) {
                    TrackingEngine.RecordingState.Idle,
                    TrackingEngine.RecordingState.Paused -> onStart()
                    TrackingEngine.RecordingState.Recording -> onStop()
                }
            },
            modifier = Modifier
                .size(80.dp)
                .background(containerColor, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDesc,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}
