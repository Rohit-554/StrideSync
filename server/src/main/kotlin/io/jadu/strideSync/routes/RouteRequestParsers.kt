package io.jadu.strideSync.routes

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import java.util.UUID

suspend fun ApplicationCall.authenticatedUserIdOrRespond(): UUID? {
    val subject = principal<JWTPrincipal>()?.subject ?: run {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Missing token subject"))
        return null
    }

    return runCatching { UUID.fromString(subject) }.getOrElse {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid token subject"))
        null
    }
}

fun ApplicationCall.pathUuidOrNull(parameterName: String): UUID? =
    parameters[parameterName]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
