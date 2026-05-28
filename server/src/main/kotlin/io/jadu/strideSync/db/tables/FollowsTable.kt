package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object FollowsTable : Table("follows") {
    val followerId = uuid("follower_id").references(UsersTable.id)
    val followeeId = uuid("followee_id").references(UsersTable.id)
    val createdAt = timestamp("created_at").default(Instant.now())

    override val primaryKey = PrimaryKey(followerId, followeeId)
}
