package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object ActivitiesTable : Table("activities") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UsersTable.id)
    val sportType = varchar("sport_type", 20)
    val title = varchar("title", 200)
    val distanceM = double("distance_m")
    val durationSec = integer("duration_sec")
    val elevationM = double("elevation_m")
    val avgPace = double("avg_pace").nullable()
    val polyline = text("polyline")
    val startedAt = timestamp("started_at")
    val createdAt = timestamp("created_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}
