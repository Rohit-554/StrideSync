package io.jadu.strideSync.gps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

actual class GpsProvider(private val context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    @SuppressLint("MissingPermission")
    actual fun observeLocation(): Flow<GpsPoint> = callbackFlow {
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000L).apply {
            setMinUpdateIntervalMillis(500L)
            setWaitForAccurateLocation(true)
        }.build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { location ->
                    val point = GpsPoint(
                        lat = location.latitude,
                        lng = location.longitude,
                        altitude = location.altitude.takeIf { it != 0.0 },
                        speed = location.speed.takeIf { it >= 0f }?.toDouble(),
                        timestamp = location.time
                    )
                    trySend(point)
                }
            }
        }

        locationCallback = callback
        fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())

        awaitClose {
            fusedClient.removeLocationUpdates(callback)
            locationCallback = null
        }
    }

    actual fun requestPermission(): Boolean {
        val fine = PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = PackageManager.PERMISSION_GRANTED == context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine || coarse
    }

    actual fun stopTracking() {
        locationCallback?.let {
            fusedClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }
}
