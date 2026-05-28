package io.jadu.strideSync.db.tables

import org.jetbrains.exposed.sql.Table

object KudosTable : Table("kudos") {
    val activityId = uuid("activity_id").references(ActivitiesTable.id)
    val userId = uuid("user_id").references(UsersTable.id)

    override val primaryKey = PrimaryKey(activityId, userId)
}
