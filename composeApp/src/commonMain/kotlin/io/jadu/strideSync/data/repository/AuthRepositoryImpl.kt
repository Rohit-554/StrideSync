package io.jadu.strideSync.data.repository

import io.jadu.strideSync.data.preferences.AppPreferences
import io.jadu.strideSync.data.remote.api.AuthApi
import io.jadu.strideSync.data.remote.dto.LoginRequest
import io.jadu.strideSync.data.remote.dto.RegisterRequest
import io.jadu.strideSync.data.remote.dto.UserResponse
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi,
    private val appPreferences: AppPreferences
) : AuthRepository {

    init {
        appPreferences.syncServerBaseUrl()
    }

    override suspend fun register(
        email: String,
        displayName: String,
        password: String
    ): Result<User> = runCatching {
        val request = RegisterRequest(
            email = email,
            displayName = displayName,
            password = password
        )
        val response = authApi.register(request)
        appPreferences.authToken = response.token
        response.user.toDomain().also { appPreferences.storeCurrentUser(it) }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> = runCatching {
        val request = LoginRequest(
            email = email,
            password = password
        )
        val response = authApi.login(request)
        appPreferences.authToken = response.token
        response.user.toDomain().also { appPreferences.storeCurrentUser(it) }
    }

    override suspend fun logout() {
        appPreferences.clearSession()
    }

    override fun isLoggedIn(): Boolean =
        appPreferences.run {
            syncServerBaseUrl()
            authToken != null
        }

    override fun getCurrentUser(): User? {
        // Return a basic user if logged in; in a real app this would come from cached profile data
        return if (isLoggedIn() && appPreferences.userId.isNotBlank()) {
            User(
                id = appPreferences.userId,
                displayName = appPreferences.username,
                email = appPreferences.userEmail,
                avatarUrl = appPreferences.userAvatarUrl
            )
        } else null
    }
}

private fun AppPreferences.storeCurrentUser(user: User) {
    userId = user.id
    username = user.displayName
    userEmail = user.email
    userAvatarUrl = user.avatarUrl
}
