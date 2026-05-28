package io.jadu.strideSync.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = ActivityEntity.TABLE_NAME)
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val sportType: String,
    val title: String,
    val distanceM: Double,
    val durationSec: Long,
    val elevationM: Double,
    val avgPace: Double? = null,
    val polyline: String,
    val startedAt: Long,
    val createdAt: Long,
    val synced: Boolean = false
) {
    companion object {
        const val TABLE_NAME = "activities"
    }
}
