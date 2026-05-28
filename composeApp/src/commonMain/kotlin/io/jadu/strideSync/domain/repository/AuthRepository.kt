package io.jadu.strideSync.domain.repository

import io.jadu.strideSync.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun register(
        email: String,
        displayName: String,
        password: String
    ): Result<User>

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun logout()

    fun isLoggedIn(): Boolean

    fun getCurrentUser(): User?
}
