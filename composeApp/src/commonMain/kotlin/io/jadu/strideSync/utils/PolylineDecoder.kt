package io.jadu.strideSync.utils

import io.jadu.strideSync.domain.model.GpsPoint

// Spec: https://developers.google.com/maps/documentation/utilities/polylinealgorithm
object PolylineDecoder {

    fun decode(encoded: String): List<GpsPoint> {
        if (encoded.isEmpty()) return emptyList()

        val result = mutableListOf<GpsPoint>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encoded.length) {
            var shift = 0
            var b: Int
            var value = 0
            do {
                b = encoded[index++].code - 63
                value = value or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            lat += if (value and 1 != 0) (value shr 1).inv() else value shr 1

            shift = 0
            value = 0
            do {
                b = encoded[index++].code - 63
                value = value or ((b and 0x1f) shl shift)
                shift += 5
            } while (b >= 0x20)
            lng += if (value and 1 != 0) (value shr 1).inv() else value shr 1

            result.add(GpsPoint(lat = lat / 1e5, lng = lng / 1e5, timestamp = 0L))
        }

        return result
    }
}
