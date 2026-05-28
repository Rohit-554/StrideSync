package io.jadu.strideSync.repository

import io.jadu.strideSync.db.DatabaseFactory.dbQuery
import io.jadu.strideSync.db.tables.UsersTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.like
import org.jetbrains.exposed.sql.SqlExpressionBuilder.neq
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.UUID

data class UserRow(
    val id: UUID,
    val email: String,
    val displayName: String,
    val passwordHash: String,
    val avatarUrl: String?,
)

class UserRepository {

    suspend fun createUser(
        email: String,
        displayName: String,
        passwordHash: String,
    ): UUID = dbQuery {
        UsersTable.insert {
            it[UsersTable.email] = email
            it[UsersTable.displayName] = displayName
            it[UsersTable.passwordHash] = passwordHash
        }[UsersTable.id]
    }

    suspend fun findByEmail(email: String): UserRow? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?.toUserRow()
    }

    suspend fun findById(id: UUID): UserRow? = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .singleOrNull()
            ?.toUserRow()
    }

    suspend fun searchUsers(query: String, page: Int, size: Int): List<UserRow> = dbQuery {
        val normalizedQuery = "%${query.trim().lowercase()}%"
        UsersTable.selectAll()
            .where { UsersTable.displayName.lowerCase() like normalizedQuery }
            .orderBy(UsersTable.displayName to SortOrder.ASC)
            .limit(size)
            .offset((page * size).toLong())
            .map { it.toUserRow() }
    }

    suspend fun listSuggestedUsers(limit: Int, excludeUserId: UUID): List<UserRow> = dbQuery {
        UsersTable.selectAll()
            .where { UsersTable.id neq excludeUserId }
            .orderBy(UsersTable.createdAt to SortOrder.DESC)
            .limit(limit)
            .map { it.toUserRow() }
    }

    private fun ResultRow.toUserRow() = UserRow(
        id = this[UsersTable.id],
        email = this[UsersTable.email],
        displayName = this[UsersTable.displayName],
        passwordHash = this[UsersTable.passwordHash],
        avatarUrl = this[UsersTable.avatarUrl],
    )
}
