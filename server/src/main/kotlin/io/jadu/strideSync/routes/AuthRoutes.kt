package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.LoginRequest
import io.jadu.strideSync.dto.RegisterRequest
import io.jadu.strideSync.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val response = runCatching { authService.register(request) }
                .getOrElse { e ->
                    call.respond(HttpStatusCode.Conflict, mapOf("error" to (e.message ?: "Registration failed")))
                    return@post
                }
            call.respond(HttpStatusCode.Created, response)
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val response = runCatching { authService.login(request) }
                .getOrElse { e ->
                    call.respond(HttpStatusCode.Unauthorized, mapOf("error" to (e.message ?: "Login failed")))
                    return@post
                }
            call.respond(HttpStatusCode.OK, response)
        }
    }
}
