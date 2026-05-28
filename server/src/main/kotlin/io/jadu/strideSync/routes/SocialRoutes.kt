package io.jadu.strideSync.routes

import io.jadu.strideSync.dto.AddCommentRequest
import io.jadu.strideSync.dto.CreateStatusRequest
import io.jadu.strideSync.dto.UserProfileResponse
import io.jadu.strideSync.repository.SocialRepository
import io.jadu.strideSync.repository.UserRepository
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

fun Route.socialRoutes(
    socialRepository: SocialRepository,
    userRepository: UserRepository,
) {
    // GET /users/{id} — public profile
        get("/users/{id}") {
        val targetId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
            ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user id"))

        val user = userRepository.findById(targetId)
            ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User not found"))

        val profile = UserProfileResponse(
            id = user.id.toString(),
            displayName = user.displayName,
            avatarUrl = user.avatarUrl,
            activityCount = socialRepository.activityCount(targetId),
            followerCount = socialRepository.followerCount(targetId),
            followingCount = socialRepository.followingCount(targetId),
        )
        call.respond(HttpStatusCode.OK, profile)
    }

        authenticate("auth-jwt") {
        get("/users/search") {
            val viewerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val query = call.request.queryParameters["q"]?.trim().orEmpty()
            if (query.length < 2) {
                return@get call.respond(HttpStatusCode.OK, emptyList<Any>())
            }
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val size = (call.request.queryParameters["size"]?.toIntOrNull() ?: 20).coerceIn(1, 50)
            val matches = userRepository.searchUsers(query = query, page = page, size = size)
                .filterNot { it.id == viewerId }
            val response = matches.map { socialRepository.buildAthleteSummary(viewerId, it) }
            call.respond(HttpStatusCode.OK, response)
        }

        get("/users/suggestions") {
            val viewerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val limit = (call.request.queryParameters["limit"]?.toIntOrNull() ?: 8).coerceIn(1, 20)
            val suggestions = userRepository.listSuggestedUsers(limit = limit, excludeUserId = viewerId)
                .filterNot { it.id == viewerId }
            val response = suggestions.map { socialRepository.buildAthleteSummary(viewerId, it) }
            call.respond(HttpStatusCode.OK, response)
        }

        get("/statuses") {
            val viewerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            call.respond(HttpStatusCode.OK, socialRepository.getActiveStatuses(viewerId))
        }

        post("/statuses") {
            val viewerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val request = call.receive<CreateStatusRequest>()
            val text = request.text.trim()
            if (text.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Status text cannot be blank"))
            }
            if (text.length > 160) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Status text is too long"))
            }
            val backgroundHex = request.backgroundHex.trim().ifBlank { "#FF571B" }
            if (!backgroundHex.matches(Regex("^#[0-9A-Fa-f]{6}$"))) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid background color"))
            }
            val status = socialRepository.createStatus(viewerId, text, backgroundHex.uppercase())
            call.respond(HttpStatusCode.Created, status)
        }

        // POST /users/{id}/follow
        post("/users/{id}/follow") {
            val followerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val followeeId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user id"))
            if (followerId == followeeId) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Cannot follow yourself"))
            }
            socialRepository.follow(followerId, followeeId)
            call.respond(HttpStatusCode.Created)
        }

        // DELETE /users/{id}/follow
        delete("/users/{id}/follow") {
            val followerId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val followeeId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid user id"))
            socialRepository.unfollow(followerId, followeeId)
            call.respond(HttpStatusCode.NoContent)
        }

        // POST /activities/{id}/kudos
        post("/activities/{id}/kudos") {
            val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val activityId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))
            socialRepository.addKudos(activityId, userId)
            call.respond(HttpStatusCode.Created)
        }

        // DELETE /activities/{id}/kudos
        delete("/activities/{id}/kudos") {
            val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val activityId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@delete call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))
            socialRepository.removeKudos(activityId, userId)
            call.respond(HttpStatusCode.NoContent)
        }

        // POST /activities/{id}/comments
        post("/activities/{id}/comments") {
            val userId = call.principal<JWTPrincipal>()!!.subject!!.let(UUID::fromString)
            val activityId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))
            val request = call.receive<AddCommentRequest>()
            if (request.text.isBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Comment text cannot be blank"))
            }
            val comment = socialRepository.addComment(activityId, userId, request.text.trim())
            call.respond(HttpStatusCode.Created, comment)
        }

        // GET /activities/{id}/comments
        get("/activities/{id}/comments") {
            val activityId = call.parameters["id"]?.let { runCatching { UUID.fromString(it) }.getOrNull() }
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid activity id"))
            call.respond(HttpStatusCode.OK, socialRepository.getComments(activityId))
        }
    }
}
