package io.jadu.strideSync.repository

import io.jadu.strideSync.db.DatabaseFactory.dbQuery
import io.jadu.strideSync.db.tables.ActivitiesTable
import io.jadu.strideSync.db.tables.CommentsTable
import io.jadu.strideSync.db.tables.FollowsTable
import io.jadu.strideSync.db.tables.KudosTable
import io.jadu.strideSync.db.tables.UsersTable
import io.jadu.strideSync.dto.ActivityResponse
import io.jadu.strideSync.dto.FeedItemResponse
import io.jadu.strideSync.dto.UserResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

class FeedRepository {

    suspend fun getFeed(userId: UUID, page: Int, size: Int): List<FeedItemResponse> = dbQuery {
        // Include own activities + followed users' activities
        val followedIds = FollowsTable
            .select(FollowsTable.followeeId)
            .where { FollowsTable.followerId eq userId }
            .map { it[FollowsTable.followeeId] }

        val visibleUserIds = (followedIds + userId).distinct()

        // Fetch activities from visible users, join with user info
        val activityRows = (ActivitiesTable innerJoin UsersTable)
            .selectAll()
            .where { ActivitiesTable.userId inList visibleUserIds }
            .orderBy(ActivitiesTable.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .limit(size).offset((page * size).toLong())
            .toList()

        activityRows.map { row ->
            val activityId = row[ActivitiesTable.id]

            val kudosCount = KudosTable.selectAll()
                .where { KudosTable.activityId eq activityId }
                .count().toInt()

            val commentCount = CommentsTable.selectAll()
                .where { CommentsTable.activityId eq activityId }
                .count().toInt()

            val hasKudosed = KudosTable.selectAll()
                .where { (KudosTable.activityId eq activityId) and (KudosTable.userId eq userId) }
                .singleOrNull() != null

            FeedItemResponse(
                activity = ActivityResponse(
                    id = activityId.toString(),
                    userId = row[ActivitiesTable.userId].toString(),
                    sportType = row[ActivitiesTable.sportType],
                    title = row[ActivitiesTable.title],
                    distanceM = row[ActivitiesTable.distanceM],
                    durationSec = row[ActivitiesTable.durationSec],
                    elevationM = row[ActivitiesTable.elevationM],
                    avgPace = row[ActivitiesTable.avgPace],
                    polyline = row[ActivitiesTable.polyline],
                    startedAt = row[ActivitiesTable.startedAt].toEpochMilli(),
                    createdAt = row[ActivitiesTable.createdAt].toEpochMilli(),
                ),
                user = UserResponse(
                    id = row[UsersTable.id].toString(),
                    displayName = row[UsersTable.displayName],
                    email = row[UsersTable.email],
                    avatarUrl = row[UsersTable.avatarUrl],
                ),
                kudosCount = kudosCount,
                commentCount = commentCount,
                hasKudosed = hasKudosed,
            )
        }
    }
}
