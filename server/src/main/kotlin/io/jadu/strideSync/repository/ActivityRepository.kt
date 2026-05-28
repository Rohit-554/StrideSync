package io.jadu.strideSync.repository

import io.jadu.strideSync.db.DatabaseFactory.dbQuery
import io.jadu.strideSync.db.tables.ActivitiesTable
import io.jadu.strideSync.db.tables.GpsPointsTable
import io.jadu.strideSync.dto.GpsPointDto
import org.jetbrains.exposed.sql.ResultRow
import io.jadu.strideSync.db.tables.CommentsTable
import io.jadu.strideSync.db.tables.KudosTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.time.Instant
import java.util.UUID

data class ActivityRow(
    val id: UUID,
    val userId: UUID,
    val sportType: String,
    val title: String,
    val distanceM: Double,
    val durationSec: Int,
    val elevationM: Double,
    val avgPace: Double?,
    val polyline: String,
    val startedAt: Instant,
    val createdAt: Instant,
)

class ActivityRepository {

    suspend fun create(
        userId: UUID,
        sportType: String,
        title: String,
        startedAt: Instant,
    ): UUID = dbQuery {
        ActivitiesTable.insert {
            it[ActivitiesTable.userId] = userId
            it[ActivitiesTable.sportType] = sportType
            it[ActivitiesTable.title] = title
            it[ActivitiesTable.distanceM] = 0.0
            it[ActivitiesTable.durationSec] = 0
            it[ActivitiesTable.elevationM] = 0.0
            it[ActivitiesTable.polyline] = ""
            it[ActivitiesTable.startedAt] = startedAt
        }[ActivitiesTable.id]
    }

    suspend fun updateStats(
        id: UUID,
        distanceM: Double,
        durationSec: Int,
        elevationM: Double,
        avgPace: Double?,
        polyline: String,
    ) = dbQuery {
        ActivitiesTable.update({ ActivitiesTable.id eq id }) {
            it[ActivitiesTable.distanceM] = distanceM
            it[ActivitiesTable.durationSec] = durationSec
            it[ActivitiesTable.elevationM] = elevationM
            it[ActivitiesTable.avgPace] = avgPace
            it[ActivitiesTable.polyline] = polyline
        }
    }

    suspend fun findById(id: UUID): ActivityRow? = dbQuery {
        ActivitiesTable.selectAll()
            .where { ActivitiesTable.id eq id }
            .singleOrNull()
            ?.toActivityRow()
    }

    suspend fun findByUser(userId: UUID, page: Int, size: Int): List<ActivityRow> = dbQuery {
        ActivitiesTable.selectAll()
            .where { ActivitiesTable.userId eq userId }
            .orderBy(ActivitiesTable.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .limit(size).offset((page * size).toLong())
            .map { it.toActivityRow() }
    }

    suspend fun delete(id: UUID, userId: UUID): Boolean = dbQuery {
        val exists = ActivitiesTable.selectAll()
            .where { (ActivitiesTable.id eq id) and (ActivitiesTable.userId eq userId) }
            .singleOrNull() != null
        if (!exists) return@dbQuery false
        // Delete child rows first to satisfy FK constraints
        CommentsTable.deleteWhere { CommentsTable.activityId eq id }
        KudosTable.deleteWhere { KudosTable.activityId eq id }
        GpsPointsTable.deleteWhere { GpsPointsTable.activityId eq id }
        ActivitiesTable.deleteWhere { ActivitiesTable.id eq id }
        true
    }

    private fun ResultRow.toActivityRow() = ActivityRow(
        id = this[ActivitiesTable.id],
        userId = this[ActivitiesTable.userId],
        sportType = this[ActivitiesTable.sportType],
        title = this[ActivitiesTable.title],
        distanceM = this[ActivitiesTable.distanceM],
        durationSec = this[ActivitiesTable.durationSec],
        elevationM = this[ActivitiesTable.elevationM],
        avgPace = this[ActivitiesTable.avgPace],
        polyline = this[ActivitiesTable.polyline],
        startedAt = this[ActivitiesTable.startedAt],
        createdAt = this[ActivitiesTable.createdAt],
    )
}

class GpsPointRepository {

    suspend fun batchInsert(activityId: UUID, points: List<GpsPointDto>) = dbQuery {
        GpsPointsTable.batchInsert(points) { point ->
            this[GpsPointsTable.activityId] = activityId
            this[GpsPointsTable.lat] = point.lat
            this[GpsPointsTable.lng] = point.lng
            this[GpsPointsTable.altitude] = point.altitude
            this[GpsPointsTable.speed] = point.speed
            this[GpsPointsTable.timestamp] = point.timestamp
        }
    }

    suspend fun findByActivity(activityId: UUID): List<GpsPointDto> = dbQuery {
        GpsPointsTable.selectAll()
            .where { GpsPointsTable.activityId eq activityId }
            .orderBy(GpsPointsTable.timestamp to org.jetbrains.exposed.sql.SortOrder.ASC)
            .map { row ->
                GpsPointDto(
                    lat = row[GpsPointsTable.lat],
                    lng = row[GpsPointsTable.lng],
                    altitude = row[GpsPointsTable.altitude],
                    speed = row[GpsPointsTable.speed],
                    timestamp = row[GpsPointsTable.timestamp],
                )
            }
    }
}
