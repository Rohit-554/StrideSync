package io.jadu.strideSync.domain.model

data class GpsPoint(
    val lat: Double,
    val lng: Double,
    val altitude: Double? = null,
    val speed: Double? = null,
    val timestamp: Long
)
