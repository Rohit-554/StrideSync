package io.jadu.strideSync.ui.components

import io.jadu.strideSync.ui.theme.Spacing

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
import io.jadu.strideSync.ui.theme.StrideColors

@Composable
fun GpsSignalIndicator(
    quality: TrackingEngine.GpsSignalQuality,
    modifier: Modifier = Modifier
) {
    val signal = gpsSignalFor(quality)

    Row(
        modifier = modifier
            .background(StrideColors.Surface.copy(alpha = 0.9f), RoundedCornerShape(Spacing.xl))
            .padding(horizontal = Spacing.d10, vertical = Spacing.d6),
        horizontalArrangement = Arrangement.spacedBy(Spacing.d6),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SignalBars(quality = quality, activeColor = signal.color)
        Text(
            text = signal.label,
            color = signal.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

private data class GpsSignal(val label: String, val color: Color)

private fun gpsSignalFor(quality: TrackingEngine.GpsSignalQuality): GpsSignal = when (quality) {
    TrackingEngine.GpsSignalQuality.Strong -> GpsSignal("GPS Strong", StrideColors.Success)
    TrackingEngine.GpsSignalQuality.Weak -> GpsSignal("GPS Weak", StrideColors.Warning)
    TrackingEngine.GpsSignalQuality.None -> GpsSignal("GPS None", StrideColors.TextSecondary)
}

@Composable
private fun SignalBars(
    quality: TrackingEngine.GpsSignalQuality,
    activeColor: Color,
    modifier: Modifier = Modifier
) {
    val activeBars = activeBarCount(quality)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xxs),
        verticalAlignment = Alignment.Bottom
    ) {
        repeat(3) { index ->
            SignalBar(
                index = index,
                activeBars = activeBars,
                activeColor = activeColor
            )
        }
    }
}

private fun activeBarCount(quality: TrackingEngine.GpsSignalQuality): Int = when (quality) {
    TrackingEngine.GpsSignalQuality.None -> 0
    TrackingEngine.GpsSignalQuality.Weak -> 2
    TrackingEngine.GpsSignalQuality.Strong -> 3
}

@Composable
private fun SignalBar(index: Int, activeBars: Int, activeColor: Color) {
    val isActive = index < activeBars
    val barColor = if (isActive) activeColor else StrideColors.SurfaceAlt.copy(alpha = 0.7f)
    val barHeight = barHeightFor(index)

    Box(
        modifier = Modifier
            .width(Spacing.d3)
            .height(barHeight)
            .background(barColor, RoundedCornerShape(Spacing.d1))
    )
}

private fun barHeightFor(index: Int) = when (index) {
    0 -> Spacing.d6
    1 -> Spacing.d10
    else -> Spacing.d14
}
