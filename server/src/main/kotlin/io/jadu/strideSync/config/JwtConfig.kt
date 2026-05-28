package io.jadu.strideSync.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date
import java.util.UUID

object JwtConfig {

    val secret: String = EnvConfig.require("JWT_SECRET")
    val issuer: String = EnvConfig.get("JWT_ISSUER") ?: "stridesync"
    val audience: String = EnvConfig.get("JWT_AUDIENCE") ?: "stridesync-users"
    val realm: String = "StrideSync"
    private val algorithm: Algorithm = Algorithm.HMAC256(secret)
    private const val TOKEN_EXPIRY_MS = 30L * 24 * 60 * 60 * 1000 // 30 days

    fun generateToken(userId: UUID): String = JWT.create()
        .withIssuer(issuer)
        .withAudience(audience)
        .withSubject(userId.toString())
        .withExpiresAt(Date(System.currentTimeMillis() + TOKEN_EXPIRY_MS))
        .sign(algorithm)

    fun verifier() = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()
}
