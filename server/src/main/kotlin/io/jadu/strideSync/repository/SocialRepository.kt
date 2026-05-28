package io.jadu.strideSync.repository

import io.jadu.strideSync.db.DatabaseFactory.dbQuery
import io.jadu.strideSync.db.tables.ActivitiesTable
import io.jadu.strideSync.db.tables.CommentsTable
import io.jadu.strideSync.db.tables.FollowsTable
import io.jadu.strideSync.db.tables.KudosTable
import io.jadu.strideSync.db.tables.StatusesTable
import io.jadu.strideSync.db.tables.UsersTable
import io.jadu.strideSync.dto.AthleteSummaryResponse
import io.jadu.strideSync.dto.CommentResponse
import io.jadu.strideSync.dto.StatusResponse
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.greater
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SocialRepository {

    // ── Statuses ──────────────────────────────────────────────────────────────

    suspend fun createStatus(userId: UUID, text: String, backgroundHex: String): StatusResponse = dbQuery {
        val now = Instant.now()
        val expiresAt = now.plus(24, ChronoUnit.HOURS)

        StatusesTable.deleteWhere { StatusesTable.userId eq userId }

        val id = StatusesTable.insert {
            it[StatusesTable.userId] = userId
            it[StatusesTable.text] = text
            it[StatusesTable.backgroundHex] = backgroundHex
            it[StatusesTable.createdAt] = now
            it[StatusesTable.expiresAt] = expiresAt
        }[StatusesTable.id]

        val user = UsersTable.selectAll()
            .where { UsersTable.id eq userId }
            .single()

        StatusResponse(
            id = id.toString(),
            userId = userId.toString(),
            displayName = user[UsersTable.displayName],
            avatarUrl = user[UsersTable.avatarUrl],
            text = text,
            backgroundHex = backgroundHex,
            createdAt = now.toEpochMilli(),
            expiresAt = expiresAt.toEpochMilli(),
            isOwn = true
        )
    }

    suspend fun getActiveStatuses(viewerId: UUID): List<StatusResponse> = dbQuery {
        val now = Instant.now()
        val followedIds = FollowsTable.select(FollowsTable.followeeId)
            .where { FollowsTable.followerId eq viewerId }
            .map { it[FollowsTable.followeeId] }

        val visibleIds = (followedIds + viewerId).distinct()
        if (visibleIds.isEmpty()) {
            return@dbQuery emptyList()
        }

        (StatusesTable innerJoin UsersTable)
            .selectAll()
            .where {
                (StatusesTable.userId inList visibleIds) and
                    (StatusesTable.expiresAt greater now)
            }
            .orderBy(StatusesTable.createdAt to org.jetbrains.exposed.sql.SortOrder.DESC)
            .map { row ->
                StatusResponse(
                    id = row[StatusesTable.id].toString(),
                    userId = row[StatusesTable.userId].toString(),
                    displayName = row[UsersTable.displayName],
                    avatarUrl = row[UsersTable.avatarUrl],
                    text = row[StatusesTable.text],
                    backgroundHex = row[StatusesTable.backgroundHex],
                    createdAt = row[StatusesTable.createdAt].toEpochMilli(),
                    expiresAt = row[StatusesTable.expiresAt].toEpochMilli(),
                    isOwn = row[StatusesTable.userId] == viewerId
                )
            }
            .distinctBy { it.userId }
            .sortedWith(compareByDescending<StatusResponse> { it.isOwn }.thenByDescending { it.createdAt })
    }

    // ── Follow / Unfollow ────────────────────────────────────────────────────

    suspend fun follow(followerId: UUID, followeeId: UUID) = dbQuery {
        val alreadyFollowing = FollowsTable.selectAll()
            .where { (FollowsTable.followerId eq followerId) and (FollowsTable.followeeId eq followeeId) }
            .singleOrNull()
        if (alreadyFollowing == null) {
            FollowsTable.insert {
                it[FollowsTable.followerId] = followerId
                it[FollowsTable.followeeId] = followeeId
            }
        }
    }

    suspend fun unfollow(followerId: UUID, followeeId: UUID) = dbQuery {
        FollowsTable.deleteWhere {
            (FollowsTable.followerId eq followerId) and (FollowsTable.followeeId eq followeeId)
        }
    }

    suspend fun followerCount(userId: UUID): Int = dbQuery {
        FollowsTable.selectAll()
            .where { FollowsTable.followeeId eq userId }
            .count().toInt()
    }

    suspend fun followingCount(userId: UUID): Int = dbQuery {
        FollowsTable.selectAll()
            .where { FollowsTable.followerId eq userId }
            .count().toInt()
    }

    // ── Kudos ────────────────────────────────────────────────────────────────

    suspend fun addKudos(activityId: UUID, userId: UUID) = dbQuery {
        val exists = KudosTable.selectAll()
            .where { (KudosTable.activityId eq activityId) and (KudosTable.userId eq userId) }
            .singleOrNull()
        if (exists == null) {
            KudosTable.insert {
                it[KudosTable.activityId] = activityId
                it[KudosTable.userId] = userId
            }
        }
    }

    suspend fun removeKudos(activityId: UUID, userId: UUID) = dbQuery {
        KudosTable.deleteWhere {
            (KudosTable.activityId eq activityId) and (KudosTable.userId eq userId)
        }
    }

    suspend fun kudosCount(activityId: UUID): Int = dbQuery {
        KudosTable.selectAll()
            .where { KudosTable.activityId eq activityId }
            .count().toInt()
    }

    suspend fun hasKudosed(activityId: UUID, userId: UUID): Boolean = dbQuery {
        KudosTable.selectAll()
            .where { (KudosTable.activityId eq activityId) and (KudosTable.userId eq userId) }
            .singleOrNull() != null
    }

    // ── Comments ─────────────────────────────────────────────────────────────

    suspend fun addComment(activityId: UUID, userId: UUID, text: String): CommentResponse = dbQuery {
        val id = CommentsTable.insert {
            it[CommentsTable.activityId] = activityId
            it[CommentsTable.userId] = userId
            it[CommentsTable.text] = text
        }[CommentsTable.id]

        val displayName = UsersTable.selectAll()
            .where { UsersTable.id eq userId }
            .single()[UsersTable.displayName]

        val row = CommentsTable.selectAll()
            .where { CommentsTable.id eq id }
            .single()

        CommentResponse(
            id = id.toString(),
            userId = userId.toString(),
            displayName = displayName,
            text = text,
            createdAt = row[CommentsTable.createdAt].toEpochMilli(),
        )
    }

    suspend fun getComments(activityId: UUID): List<CommentResponse> = dbQuery {
        (CommentsTable innerJoin UsersTable)
            .selectAll()
            .where { CommentsTable.activityId eq activityId }
            .orderBy(CommentsTable.createdAt to org.jetbrains.exposed.sql.SortOrder.ASC)
            .map { row ->
                CommentResponse(
                    id = row[CommentsTable.id].toString(),
                    userId = row[CommentsTable.userId].toString(),
                    displayName = row[UsersTable.displayName],
                    text = row[CommentsTable.text],
                    createdAt = row[CommentsTable.createdAt].toEpochMilli(),
                )
            }
    }

    suspend fun commentCount(activityId: UUID): Int = dbQuery {
        CommentsTable.selectAll()
            .where { CommentsTable.activityId eq activityId }
            .count().toInt()
    }

    // ── User profile stats ───────────────────────────────────────────────────

    suspend fun activityCount(userId: UUID): Int = dbQuery {
        ActivitiesTable.selectAll()
            .where { ActivitiesTable.userId eq userId }
            .count().toInt()
    }

    suspend fun isFollowing(followerId: UUID, followeeId: UUID): Boolean = dbQuery {
        FollowsTable.selectAll()
            .where { (FollowsTable.followerId eq followerId) and (FollowsTable.followeeId eq followeeId) }
            .singleOrNull() != null
    }

    suspend fun buildAthleteSummary(viewerId: UUID, user: UserRow): AthleteSummaryResponse {
        return AthleteSummaryResponse(
            id = user.id.toString(),
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            followerCount = followerCount(user.id),
            activityCount = activityCount(user.id),
            isFollowing = isFollowing(viewerId, user.id)
        )
    }
}
