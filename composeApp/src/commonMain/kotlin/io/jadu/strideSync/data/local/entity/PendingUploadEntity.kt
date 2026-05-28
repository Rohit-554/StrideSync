package io.jadu.strideSync.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = PendingUploadEntity.TABLE_NAME)
data class PendingUploadEntity(
    @PrimaryKey
    val id: String,
    val activityJson: String,
    val gpsPointsJson: String,
    val createdAt: Long
) {
    companion object {
        const val TABLE_NAME = "pending_uploads"
    }
}
