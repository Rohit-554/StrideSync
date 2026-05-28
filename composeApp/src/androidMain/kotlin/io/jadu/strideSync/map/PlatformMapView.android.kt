package io.jadu.strideSync.map

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import io.jadu.strideSync.domain.model.GpsPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

private const val STRAVA_ORANGE = "#FC4C02"
private const val ROUTE_STROKE_WIDTH = 10f
private const val DEFAULT_ZOOM = 15.0
private const val BOUNDS_PADDING_FACTOR = 1.35f

@Composable
actual fun PlatformMapView(
    gpsPoints: List<GpsPoint>,
    modifier: Modifier,
    onFirstLocationFix: () -> Unit
) {
    val context = LocalContext.current
    val currentOnFirstFix by rememberUpdatedState(onFirstLocationFix)
    val mapView = remember { buildMapView(context) { currentOnFirstFix() } }

    DisposableEffect(Unit) {
        onDispose {
            mapView.overlays.filterIsInstance<MyLocationNewOverlay>().forEach { it.disableMyLocation() }
            mapView.onDetach()
        }
    }

    AndroidView(
        factory = { mapView },
        update = { map -> renderRoute(map, gpsPoints) },
        modifier = modifier
    )
}

private fun buildMapView(context: Context, onFirstFix: () -> Unit): MapView {
    Configuration.getInstance().apply {
        load(context, context.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE))
        userAgentValue = context.packageName
        osmdroidTileCache = context.cacheDir.resolve("osmdroid")
    }
    val map = MapView(context).apply {
        setTileSource(TileSourceFactory.MAPNIK)
        setMultiTouchControls(true)
        isTilesScaledToDpi = true
        controller.setZoom(DEFAULT_ZOOM)
    }
    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map).apply {
        enableMyLocation()
        enableFollowLocation()
        runOnFirstFix { map.post { onFirstFix() } }
    }
    map.overlays.add(locationOverlay)
    return map
}

private fun renderRoute(map: MapView, gpsPoints: List<GpsPoint>) {
    map.overlays.removeAll { it is Polyline || it is Marker }

    if (gpsPoints.isEmpty()) {
        map.invalidate()
        return
    }

    map.overlays.filterIsInstance<MyLocationNewOverlay>().forEach { it.disableFollowLocation() }

    val geoPoints = gpsPoints.map { GeoPoint(it.lat, it.lng) }
    val orange = Color.parseColor(STRAVA_ORANGE)

    val routeLine = Polyline().apply {
        setPoints(geoPoints)
        outlinePaint.color = orange
        outlinePaint.strokeWidth = ROUTE_STROKE_WIDTH
        outlinePaint.isAntiAlias = true
    }
    map.overlays.add(routeLine)

    val startMarker = Marker(map).apply {
        position = geoPoints.first()
        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        title = "Start"
    }
    map.overlays.add(startMarker)

    if (geoPoints.size > 1) {
        val finishMarker = Marker(map).apply {
            position = geoPoints.last()
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Finish"
        }
        map.overlays.add(finishMarker)

        val bounds = BoundingBox.fromGeoPoints(geoPoints)
        map.post { map.zoomToBoundingBox(bounds.increaseByScale(BOUNDS_PADDING_FACTOR), true) }
    } else {
        map.controller.setZoom(DEFAULT_ZOOM)
        map.controller.setCenter(geoPoints.first())
    }

    map.invalidate()
}
