package io.jadu.strideSync.utils

import kotlin.math.floor

object Formatters {

    fun metersToKmString(meters: Double): String {
        val km = meters / 1000.0
        return "${(km * 10).toInt() / 10.0}"
    }

    fun formatDuration(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return if (hours > 0) {
            "$hours:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        } else {
            "${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}"
        }
    }

    fun formatPace(paceSecPerKm: Double): String {
        val minutes = floor(paceSecPerKm / 60).toInt()
        val seconds = (paceSecPerKm % 60).toInt()
        return "$minutes:${seconds.toString().padStart(2, '0')}/km"
    }

    fun timeAgo(timestamp: Long): String {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        val diffMs = now - timestamp
        val diffSec = diffMs / 1000
        val diffMin = diffSec / 60
        val diffHour = diffMin / 60
        val diffDay = diffHour / 24

        return when {
            diffDay > 30 -> {
                val diffMonth = diffDay / 30
                if (diffMonth > 12) {
                    val diffYear = diffMonth / 12
                    "$diffYear year${if (diffYear > 1) "s" else ""} ago"
                } else {
                    "$diffMonth month${if (diffMonth > 1) "s" else ""} ago"
                }
            }
            diffDay > 0 -> "$diffDay day${if (diffDay > 1) "s" else ""} ago"
            diffHour > 0 -> "$diffHour hour${if (diffHour > 1) "s" else ""} ago"
            diffMin > 0 -> "$diffMin minute${if (diffMin > 1) "s" else ""} ago"
            else -> "Just now"
        }
    }
}
