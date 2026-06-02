package io.jadu.strideSync.network

import io.jadu.strideSync.AppConfig
import io.jadu.strideSync.data.preferences.AppPreferences
import io.jadu.strideSync.data.remote.interceptor.authInterceptor
import io.jadu.strideSync.network.circuitbreaker.CircuitBreakerRegistry
import io.jadu.strideSync.network.circuitbreaker.CircuitBreakerOpenException
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.api.Send
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

fun createHttpClient(
    appPreferences: AppPreferences,
    sessionEventBus: SessionEventBus,
    circuitBreakerRegistry: CircuitBreakerRegistry
) = HttpClient {
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
    install(circuitBreakerInterceptor(circuitBreakerRegistry))

    defaultRequest {
        url(AppConfig.BASE_URL)
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

private fun circuitBreakerInterceptor(
    circuitBreakerRegistry: CircuitBreakerRegistry
) = createClientPlugin("CircuitBreakerInterceptor") {
    on(Send) { request ->
        if (request.url.protocol.name.startsWith("ws")) {
            return@on proceed(request)
        }

        val circuitBreaker = circuitBreakerRegistry.get()
        val permit = circuitBreaker.acquirePermit()
        try {
            val call = proceed(request)
            if (call.response.status.isCircuitBreakerFailure()) {
                circuitBreaker.recordFailure(permit)
            } else {
                circuitBreaker.recordSuccess(permit)
            }
            call
        } catch (error: Throwable) {
            if (error.isCircuitBreakerFailure()) {
                circuitBreaker.recordFailure(permit)
            }
            throw error
        }
    }
}

private fun HttpStatusCode.isCircuitBreakerFailure(): Boolean =
    value == 429 || value in 500..504

private fun Throwable.isCircuitBreakerFailure(): Boolean {
    if (this is CircuitBreakerOpenException || this is SessionExpiredException || this is CancellationException) {
        return false
    }

    return when (this) {
        is HttpRequestTimeoutException,
        is ConnectTimeoutException,
        is SocketTimeoutException,
        is IOException -> true
        is ResponseException -> response.status.isCircuitBreakerFailure()
        else -> message.isNetworkFailureMessage()
    }
}

private fun String?.isNetworkFailureMessage(): Boolean =
    this?.let { message ->
        listOf(
            "Unable to resolve host",
            "Failed to connect",
            "SocketTimeoutException",
            "Connection refused",
            "ConnectException",
            "Network is unreachable"
        ).any { message.contains(it) }
    } ?: false
