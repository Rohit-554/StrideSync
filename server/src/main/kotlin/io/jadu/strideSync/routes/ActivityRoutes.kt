package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.CreateActivityRequest
import io.jadu.strideSync.service.ActivityService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import java.util.UUID

fun Route.activityRoutes(activityService: ActivityService) {
    route("/activities") {

        // GET /activities/{id} — public
        get("/{id}") {
            val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))

            val response = runCatching { activityService.getById(id) }
                .getOrElse { e ->
                    call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "Not found")))
                    return@get
                }
            call.respond(HttpStatusCode.OK, response)
        }

        authenticate("auth-jwt") {

            // POST /activities
            post {
                val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
                val request = call.receive<CreateActivityRequest>()

                val response = runCatching { activityService.create(userId, request) }
                    .getOrElse { e ->
                        call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (e.message ?: "Create failed")))
                        return@post
                    }
                call.respond(HttpStatusCode.Created, response)
            }

            // GET /activities?page=0&size=20 — own activities
            get {
                val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
                val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                val size = (call.request.queryParameters["size"]?.toIntOrNull() ?: 20).coerceIn(1, 100)

                val activities = activityService.getByUser(userId, page, size)
                call.respond(HttpStatusCode.OK, activities)
            }

            // DELETE /activities/{id}
            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
                val id = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))

                runCatching { activityService.delete(id, userId) }
                    .onFailure { e ->
                        call.respond(HttpStatusCode.NotFound, mapOf("error" to (e.message ?: "Not found")))
                        return@delete
                    }
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}
