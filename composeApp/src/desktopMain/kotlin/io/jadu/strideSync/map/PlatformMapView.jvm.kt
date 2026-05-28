package io.jadu.strideSync.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import io.jadu.strideSync.domain.model.GpsPoint

@Composable
actual fun PlatformMapView(
    gpsPoints: List<GpsPoint>,
    modifier: Modifier,
    onFirstLocationFix: () -> Unit
) {
    LaunchedEffect(Unit) { onFirstLocationFix() }

    Box(
        modifier = modifier.background(Color(0xFF252830)),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Map not supported on desktop", color = Color(0xFF9BA3B2))
    }
}
