package io.jadu.strideSync.db

import io.jadu.strideSync.config.DatabaseConfig
import io.jadu.strideSync.db.tables.ActivitiesTable
import io.jadu.strideSync.db.tables.CommentsTable
import io.jadu.strideSync.db.tables.FollowsTable
import io.jadu.strideSync.db.tables.GpsPointsTable
import io.jadu.strideSync.db.tables.KudosTable
import io.jadu.strideSync.db.tables.StatusesTable
import io.jadu.strideSync.db.tables.UsersTable
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    fun init() {
        val dataSource = DatabaseConfig.createDataSource()
        Database.connect(dataSource)

        transaction {
            SchemaUtils.create(
                UsersTable,
                ActivitiesTable,
                GpsPointsTable,
                FollowsTable,
                KudosTable,
                CommentsTable,
                StatusesTable,
            )
        }
    }

    suspend fun <T> dbQuery(block: () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}
