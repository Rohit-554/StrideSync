package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object UsersTable : Table("users") {
    val id = uuid("id").autoGenerate()
    val email = varchar("email", 255).uniqueIndex()
    val displayName = varchar("display_name", 100)
    val passwordHash = varchar("password_hash", 255)
    val avatarUrl = varchar("avatar_url", 500).nullable()
    val createdAt = timestamp("created_at").clientDefault { Instant.now() }

    override val primaryKey = PrimaryKey(id)
}
