package io.jadu.strideSync.plugins

import io.jadu.strideSync.repository.FeedRepository
import io.jadu.strideSync.repository.SocialRepository
import io.jadu.strideSync.repository.UserRepository
import io.jadu.strideSync.routes.activityRoutes
import io.jadu.strideSync.routes.authRoutes
import io.jadu.strideSync.routes.feedRoutes
import io.jadu.strideSync.routes.socialRoutes
import io.jadu.strideSync.routes.trackingRoutes
import io.jadu.strideSync.service.ActivityService
import io.jadu.strideSync.service.AuthService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

@Serializable
private data class HealthResponse(val status: String)

fun Application.configureRouting(
    authService: AuthService,
    activityService: ActivityService,
    socialRepository: SocialRepository,
    feedRepository: FeedRepository,
    userRepository: UserRepository,
) {
    routing {
        get("/health") {
            call.respond(HttpStatusCode.OK, HealthResponse(status = "ok"))
        }

        authRoutes(authService)
        activityRoutes(activityService)
        trackingRoutes()
        socialRoutes(socialRepository, userRepository)
        feedRoutes(feedRepository)
    }
}
