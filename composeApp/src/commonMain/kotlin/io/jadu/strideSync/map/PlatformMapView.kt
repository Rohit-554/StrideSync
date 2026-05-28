package io.jadu.strideSync.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.jadu.strideSync.domain.model.GpsPoint

/**
 * Platform-specific map composable backed by OpenStreetMap tiles.
 * Android: osmdroid MapView
 * iOS: WKWebView rendering Leaflet + OpenStreetMap
 * Desktop: static placeholder
 */
@Composable
expect fun PlatformMapView(
    gpsPoints: List<GpsPoint>,
    modifier: Modifier = Modifier,
    onFirstLocationFix: () -> Unit = {}
)
