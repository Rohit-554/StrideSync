package io.jadu.strideSync

import io.jadu.strideSync.db.DatabaseFactory
import io.jadu.strideSync.plugins.configureAuthentication
import io.jadu.strideSync.plugins.configureRouting
import io.jadu.strideSync.plugins.configureSerialization
import io.jadu.strideSync.plugins.configureWebSockets
import io.jadu.strideSync.repository.ActivityRepository
import io.jadu.strideSync.repository.FeedRepository
import io.jadu.strideSync.repository.GpsPointRepository
import io.jadu.strideSync.repository.SocialRepository
import io.jadu.strideSync.repository.UserRepository
import io.jadu.strideSync.service.ActivityService
import io.jadu.strideSync.service.AuthService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.cors.routing.CORS

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()

    val userRepository = UserRepository()
    val authService = AuthService(userRepository)

    val activityRepository = ActivityRepository()
    val gpsPointRepository = GpsPointRepository()
    val activityService = ActivityService(activityRepository, gpsPointRepository)

    val socialRepository = SocialRepository()
    val feedRepository = FeedRepository()

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost()
    }

    configureSerialization()
    configureAuthentication()
    configureWebSockets()
    configureRouting(authService, activityService, socialRepository, feedRepository, userRepository, gpsPointRepository)
}
