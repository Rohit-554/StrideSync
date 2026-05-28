package io.jadu.strideSync.data.remote.api

import io.jadu.strideSync.data.remote.dto.AuthResponse
import io.jadu.strideSync.data.remote.dto.LoginRequest
import io.jadu.strideSync.data.remote.dto.RegisterRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType

private const val PATH_AUTH_REGISTER = "/auth/register"
private const val PATH_AUTH_LOGIN = "/auth/login"

class AuthApi(private val client: HttpClient) {

    suspend fun register(request: RegisterRequest): AuthResponse =
        client.post(PATH_AUTH_REGISTER) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.unwrap()

    suspend fun login(request: LoginRequest): AuthResponse =
        client.post(PATH_AUTH_LOGIN) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.unwrap()

    private suspend fun HttpResponse.unwrap(): AuthResponse {
        if (status.value in 200..299) return body()
        val error = runCatching { body<Map<String, String>>() }
            .getOrNull()?.get("error")
        throw Exception(error ?: "Something went wrong. Please try again.")
    }
}
