package io.jadu.strideSync.routes

import io.jadu.strideSync.repository.FeedRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.feedRoutes(feedRepository: FeedRepository) {
    authenticate("auth-jwt") {
        get("/feed") {
            val userId = call.authenticatedUserIdOrRespond() ?: return@get
            val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
            val size = (call.request.queryParameters["size"]?.toIntOrNull() ?: 20).coerceIn(1, 100)
            val items = feedRepository.getFeed(userId, page, size)
            call.respond(HttpStatusCode.OK, items)
        }
    }
}
