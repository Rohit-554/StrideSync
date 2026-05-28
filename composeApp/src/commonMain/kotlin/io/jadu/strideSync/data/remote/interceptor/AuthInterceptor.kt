package io.jadu.strideSync.data.remote.interceptor

import io.jadu.strideSync.data.preferences.AppPreferences
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpSendPipeline
import io.ktor.http.HttpHeaders

fun authInterceptor(appPreferences: AppPreferences) = createClientPlugin("AuthInterceptor") {
    onRequest { request, _ ->
        val token = appPreferences.authToken
        if (token != null) {
            request.headers.append(HttpHeaders.Authorization, "Bearer $token")
        }
    }
}
