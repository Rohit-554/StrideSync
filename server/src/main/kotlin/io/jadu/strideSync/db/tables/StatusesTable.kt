package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object StatusesTable : Table("statuses") {
    val id = uuid("id").autoGenerate()
    val userId = uuid("user_id").references(UsersTable.id).index()
    val text = varchar("text", 160)
    val backgroundHex = varchar("background_hex", 16)
    val createdAt = timestamp("created_at").default(Instant.now())
    val expiresAt = timestamp("expires_at")

    override val primaryKey = PrimaryKey(id)
}
