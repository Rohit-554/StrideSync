package io.jadu.strideSync.tracking

import io.jadu.strideSync.domain.model.GpsPoint
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.math.PI

object DistanceCalculator {

    private const val EARTH_RADIUS_METERS = 6_371_000.0

    fun haversineMeters(from: GpsPoint, to: GpsPoint): Double {
        val dLat = toRadians(to.lat - from.lat)
        val dLon = toRadians(to.lng - from.lng)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(toRadians(from.lat)) * cos(toRadians(to.lat)) *
            sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    private fun toRadians(degrees: Double): Double = degrees * PI / 180.0

    fun cumulativeDistanceMeters(points: List<GpsPoint>): Double {
        if (points.size < 2) return 0.0
        var total = 0.0
        for (i in 1 until points.size) {
            total += haversineMeters(points[i - 1], points[i])
        }
        return total
    }
}
