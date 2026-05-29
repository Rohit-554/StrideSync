package io.jadu.strideSync.tracking

object PaceCalculator {

    fun secondsPerKm(distanceMeters: Double, durationSeconds: Long): Double? {
        if (distanceMeters <= 0 || durationSeconds <= 0) return null
        val km = distanceMeters / 1000.0
        return durationSeconds / km
    }
}
