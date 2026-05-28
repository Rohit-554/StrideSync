package io.jadu.strideSync.plugins

import io.jadu.strideSync.config.JwtConfig
import io.ktor.server.application.Application
import io.ktor.server.auth.authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuthentication() {
    authentication {
        jwt("auth-jwt") {
            realm = JwtConfig.realm
            verifier(JwtConfig.verifier())
            validate { credential ->
                val subject = credential.payload.subject
                if (subject != null) JWTPrincipal(credential.payload) else null
            }
        }
    }
}
