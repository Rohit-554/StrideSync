package io.jadu.strideSync.gps

import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.Foundation.timeIntervalSince1970
import platform.darwin.NSObject

@OptIn(ExperimentalForeignApi::class)
actual class GpsProvider {

    private var locationManager: CLLocationManager? = null
    private var locationDelegate: CLLocationManagerDelegateProtocol? = null

    actual fun observeLocation(): Flow<GpsPoint> = callbackFlow {
        val manager = CLLocationManager()
        val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
                val location = didUpdateLocations.lastOrNull() as? CLLocation ?: return
                val lat = location.coordinate.useContents { this.latitude }
                val lng = location.coordinate.useContents { this.longitude }
                val point = GpsPoint(
                    lat = lat,
                    lng = lng,
                    altitude = location.altitude.takeIf { it != 0.0 },
                    speed = location.speed.takeIf { it >= 0 }?.toDouble(),
                    timestamp = (location.timestamp.timeIntervalSince1970 * 1000).toLong()
                )
                trySend(point)
            }

            override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
                // Silently ignore errors; the flow will just not emit
            }
        }

        locationDelegate = delegate
        manager.delegate = delegate
        manager.setDesiredAccuracy(kCLLocationAccuracyBest)
        manager.setDistanceFilter(5.0)
        manager.pausesLocationUpdatesAutomatically = false
        ensureAuthorized(manager)
        manager.startUpdatingLocation()

        locationManager = manager

        awaitClose {
            manager.stopUpdatingLocation()
            manager.delegate = null
            locationManager = null
            locationDelegate = null
        }
    }

    actual fun requestPermission(): Boolean {
        val manager = CLLocationManager()
        return when (manager.authorizationStatus()) {
            kCLAuthorizationStatusAuthorizedWhenInUse,
            kCLAuthorizationStatusAuthorizedAlways -> true
            kCLAuthorizationStatusNotDetermined -> {
                manager.requestWhenInUseAuthorization()
                false
            }
            else -> false
        }
    }

    actual fun stopTracking() {
        locationManager?.stopUpdatingLocation()
        locationManager?.delegate = null
        locationManager = null
        locationDelegate = null
    }

    private fun ensureAuthorized(manager: CLLocationManager) {
        if (manager.authorizationStatus() == kCLAuthorizationStatusNotDetermined) {
            manager.requestWhenInUseAuthorization()
        }
    }
}
