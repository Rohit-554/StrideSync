package io.jadu.strideSync.dto

import kotlinx.serialization.Serializable

@Serializable
data class GpsPointDto(
    val lat: Double,
    val lng: Double,
    val altitude: Double? = null,
    val speed: Double? = null,
    val timestamp: Long,
)

@Serializable
data class CreateActivityRequest(
    val sportType: String,
    val title: String,
    val gpsPoints: List<GpsPointDto>,
    val startedAt: Long, // epoch millis
)

@Serializable
data class ActivityResponse(
    val id: String,
    val userId: String,
    val sportType: String,
    val title: String,
    val distanceM: Double,
    val durationSec: Int,
    val elevationM: Double,
    val avgPace: Double? = null,
    val polyline: String,
    val startedAt: Long,
    val createdAt: Long,
)
