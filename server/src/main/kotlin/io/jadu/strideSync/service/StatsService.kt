package io.jadu.strideSync.service

import io.jadu.strideSync.dto.GpsPointDto
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object StatsService {

    private const val EARTH_RADIUS_M = 6_371_000.0

    fun computeDistanceM(points: List<GpsPointDto>): Double {
        if (points.size < 2) return 0.0
        return points.zipWithNext().sumOf { (a, b) -> haversine(a.lat, a.lng, b.lat, b.lng) }
    }

    fun computeDurationSec(points: List<GpsPointDto>): Int {
        if (points.size < 2) return 0
        return ((points.last().timestamp - points.first().timestamp) / 1000).toInt()
    }

    fun computeElevationGainM(points: List<GpsPointDto>): Double {
        if (points.size < 2) return 0.0
        return points.zipWithNext().sumOf { (a, b) ->
            val gain = (b.altitude ?: 0.0) - (a.altitude ?: 0.0)
            if (gain > 0) gain else 0.0
        }
    }

    fun computeAvgPaceSeckm(distanceM: Double, durationSec: Int): Double? {
        if (distanceM < 1.0) return null
        return durationSec / (distanceM / 1000.0)
    }

    /** Google Encoded Polyline Algorithm (precision 1e-5). */
    fun encodePolyline(points: List<GpsPointDto>): String {
        val sb = StringBuilder()
        var prevLat = 0
        var prevLng = 0
        for (point in points) {
            val lat = Math.round(point.lat * 1e5).toInt()
            val lng = Math.round(point.lng * 1e5).toInt()
            sb.append(encodeValue(lat - prevLat))
            sb.append(encodeValue(lng - prevLng))
            prevLat = lat
            prevLng = lng
        }
        return sb.toString()
    }

    private fun encodeValue(value: Int): String {
        var v = if (value < 0) (value shl 1).inv() else value shl 1
        val sb = StringBuilder()
        while (v >= 0x20) {
            sb.append(((0x20 or (v and 0x1f)) + 63).toChar())
            v = v ushr 5
        }
        sb.append((v + 63).toChar())
        return sb.toString()
    }

    private fun haversine(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLng / 2).pow(2)
        return EARTH_RADIUS_M * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
