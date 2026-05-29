package io.jadu.strideSync.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.jadu.strideSync.domain.model.GpsPoint

@Composable
expect fun PlatformMapView(
    gpsPoints: List<GpsPoint>,
    modifier: Modifier = Modifier,
    onFirstLocationFix: () -> Unit = {}
)
