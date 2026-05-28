package io.jadu.strideSync.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.strideSync.tracking.TrackingEngine

@Composable
fun GpsSignalIndicator(
    quality: TrackingEngine.GpsSignalQuality,
    modifier: Modifier = Modifier
) {
    val (label, color) = when (quality) {
        TrackingEngine.GpsSignalQuality.Strong -> "GPS Strong" to Color(0xFF3ECF8E)
        TrackingEngine.GpsSignalQuality.Weak -> "GPS Weak" to Color(0xFFFFC107)
        TrackingEngine.GpsSignalQuality.None -> "GPS None" to Color(0xFF9BA3B2)
    }

    Row(
        modifier = modifier
            .background(Color(0xFF1F2530).copy(alpha = 0.9f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SignalBars(quality = quality, activeColor = color)
        Text(
            text = label,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SignalBars(
    quality: TrackingEngine.GpsSignalQuality,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val activeBars = when (quality) {
        TrackingEngine.GpsSignalQuality.None -> 0
        TrackingEngine.GpsSignalQuality.Weak -> 2
        TrackingEngine.GpsSignalQuality.Strong -> 3
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(3) { index ->
            val isActive = index < activeBars
            val barColor = if (isActive) activeColor else Color(0xFF3A3F4B)
            val barHeight = when (index) {
                0 -> 6.dp
                1 -> 10.dp
                else -> 14.dp
            }
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(barHeight)
                    .background(barColor, RoundedCornerShape(1.dp))
            )
        }
    }
}
