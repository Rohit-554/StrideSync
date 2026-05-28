package io.jadu.strideSync.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val displayName: String,
    val password: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse,
)

@Serializable
data class UserResponse(
    val id: String,
    val displayName: String,
    val email: String,
    val avatarUrl: String? = null,
)
