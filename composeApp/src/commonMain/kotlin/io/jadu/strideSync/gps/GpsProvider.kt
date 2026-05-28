package io.jadu.strideSync.gps

import io.jadu.strideSync.domain.model.GpsPoint
import kotlinx.coroutines.flow.Flow

expect class GpsProvider {

    fun observeLocation(): Flow<GpsPoint>

    fun requestPermission(): Boolean

    fun stopTracking()
}
