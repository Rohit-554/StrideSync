package io.jadu.strideSync.data.local.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = CachedFeedEntity.TABLE_NAME)
data class CachedFeedEntity(
    @PrimaryKey
    val id: String,
    val jsonPayload: String,
    val cachedAt: Long
) {
    companion object {
        const val TABLE_NAME = "cached_feed"
    }
}
