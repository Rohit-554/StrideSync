package io.jadu.strideSync.gps

import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

actual class GpsProvider {

    actual fun observeLocation(): Flow<GpsPoint> = emptyFlow()

    actual fun requestPermission(): Boolean = false

    actual fun stopTracking() = Unit
}
