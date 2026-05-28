package io.jadu.strideSync.network

import io.jadu.strideSync.AppConfig
import io.jadu.strideSync.data.preferences.AppPreferences
import io.jadu.strideSync.data.remote.interceptor.authInterceptor
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(appPreferences: AppPreferences, sessionEventBus: SessionEventBus) = HttpClient {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
    install(Logging) {
        level = LogLevel.HEADERS
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 15_000
    }
    install(WebSockets)
    install(authInterceptor(appPreferences))

    // Skip ngrok browser warning for physical device testing
    defaultRequest {
        url(AppConfig.BASE_URL)
        headers.append("ngrok-skip-browser-warning", "true")
    }

    HttpResponseValidator {
        validateResponse { response ->
            if (response.status == HttpStatusCode.Unauthorized) {
                appPreferences.clearAll()
                sessionEventBus.notifyExpired()
                throw SessionExpiredException()
            }
        }
    }
}

class SessionExpiredException : Exception("Session expired. Please log in again.")
