package io.jadu.strideSync.domain.model

data class Activity(
    val id: String,
    val userId: String,
    val sportType: SportType,
    val title: String,
    val distanceM: Double,
    val durationSec: Long,
    val elevationM: Double,
    val avgPace: Double? = null,
    val polyline: String,
    val startedAt: Long,
    val createdAt: Long
)
