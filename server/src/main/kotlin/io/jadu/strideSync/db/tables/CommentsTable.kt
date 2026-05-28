package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant

object CommentsTable : Table("comments") {
    val id = uuid("id").autoGenerate()
    val activityId = uuid("activity_id").references(ActivitiesTable.id).index()
    val userId = uuid("user_id").references(UsersTable.id)
    val text = varchar("text", 500)
    val createdAt = timestamp("created_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}
