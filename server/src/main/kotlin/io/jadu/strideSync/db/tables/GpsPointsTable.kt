package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table

object GpsPointsTable : Table("gps_points") {
    val id = long("id").autoIncrement()
    val activityId = uuid("activity_id").references(ActivitiesTable.id).index()
    val lat = double("lat")
    val lng = double("lng")
    val altitude = double("altitude").nullable()
    val speed = double("speed").nullable()
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}
