package io.jadu.strideSync.service

import at.favre.lib.crypto.bcrypt.BCrypt
import io.jadu.strideSync.config.JwtConfig
import io.jadu.strideSync.dto.AuthResponse
import io.jadu.strideSync.dto.LoginRequest
import io.jadu.strideSync.dto.RegisterRequest
import io.jadu.strideSync.dto.UserResponse
import io.jadu.strideSync.repository.UserRepository

class AuthService(private val userRepository: UserRepository) {

    suspend fun register(request: RegisterRequest): AuthResponse {
        val existing = userRepository.findByEmail(request.email)
        if (existing != null) error("Email already registered")

        val passwordHash = BCrypt.withDefaults().hashToString(12, request.password.toCharArray())

        val userId = userRepository.createUser(
            email = request.email,
            displayName = request.displayName,
            passwordHash = passwordHash,
        )

        val token = JwtConfig.generateToken(userId)
        return AuthResponse(
            token = token,
            user = UserResponse(
                id = userId.toString(),
                displayName = request.displayName,
                email = request.email,
            ),
        )
    }

    suspend fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email)
            ?: error("Invalid email or password")

        val verified = BCrypt.verifyer()
            .verify(request.password.toCharArray(), user.passwordHash)
        if (!verified.verified) error("Invalid email or password")

        val token = JwtConfig.generateToken(user.id)
        return AuthResponse(
            token = token,
            user = UserResponse(
                id = user.id.toString(),
                displayName = user.displayName,
                email = user.email,
                avatarUrl = user.avatarUrl,
            ),
        )
    }
}
