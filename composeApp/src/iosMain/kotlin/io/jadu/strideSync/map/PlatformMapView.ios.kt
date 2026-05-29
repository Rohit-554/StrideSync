package io.jadu.strideSync.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.UIKitView
import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.cinterop.ExperimentalForeignApi
import platform.WebKit.WKWebView

private const val STRAVA_ORANGE = "#FC4C02"

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun PlatformMapView(
    gpsPoints: List<GpsPoint>,
    modifier: Modifier,
    onFirstLocationFix: () -> Unit
) {
    LaunchedEffect(Unit) { onFirstLocationFix() }

    val html = buildLeafletHtml(gpsPoints)

    UIKitView(
        factory = {
            val webView = WKWebView()
            webView.loadHTMLString(html, baseURL = null)
            webView
        },
        update = { webView ->
            webView.loadHTMLString(buildLeafletHtml(gpsPoints), baseURL = null)
        },
        modifier = modifier
    )
}

private fun buildLeafletHtml(gpsPoints: List<GpsPoint>): String {
    val coordsJson = gpsPoints.joinToString(",") { "[${it.lat},${it.lng}]" }
    val centerLat = if (gpsPoints.isEmpty()) 0.0 else gpsPoints.map { it.lat }.average()
    val centerLng = if (gpsPoints.isEmpty()) 0.0 else gpsPoints.map { it.lng }.average()
    val zoom = if (gpsPoints.isEmpty()) 2 else 14

    return """
        <!DOCTYPE html>
        <html>
        <head>
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css"/>
          <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
          <style>
            html, body, #map { height: 100%; margin: 0; padding: 0; background: #252830; }
          </style>
        </head>
        <body>
          <div id="map"></div>
          <script>
            var map = L.map('map', {zoomControl: false}).setView([$centerLat, $centerLng], $zoom);
            L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
              maxZoom: 19,
              attribution: '&copy; OpenStreetMap contributors'
            }).addTo(map);
            var coords = [$coordsJson];
            if (coords.length > 1) {
              var polyline = L.polyline(coords, {color: '$STRAVA_ORANGE', weight: 4}).addTo(map);
              map.fitBounds(polyline.getBounds(), {padding: [20, 20]});
            }
          </script>
        </body>
        </html>
    """.trimIndent()
}
