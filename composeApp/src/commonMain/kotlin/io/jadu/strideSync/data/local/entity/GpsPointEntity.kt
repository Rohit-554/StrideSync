package io.jadu.strideSync.data.local.entity

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey

@Entity(
    tableName = GpsPointEntity.TABLE_NAME,
    indices = [Index(value = ["activityId"])]
)
data class GpsPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: String,
    val lat: Double,
    val lng: Double,
    val altitude: Double? = null,
    val speed: Double? = null,
    val timestamp: Long
) {
    companion object {
        const val TABLE_NAME = "gps_points"
    }
}
