package io.jadu.strideSync.utils

import io.jadu.strideSync.network.SessionExpiredException
import io.ktor.client.plugins.ResponseException

fun Throwable.toUiMessage(): String = when (this) {
    is SessionExpiredException -> "Session expired. Please log in again."
    is ResponseException -> when (response.status.value) {
        401 -> "Session expired. Please log in again."
        403 -> "You don't have permission to do that."
        404 -> "Not found."
        500, 502, 503 -> "Server error. Please try again later."
        else -> "Something went wrong. Please try again."
    }
    else -> when {
        message?.contains("Unable to resolve host") == true ||
        message?.contains("Failed to connect") == true ||
        message?.contains("SocketTimeoutException") == true ||
        message?.contains("Connection refused") == true ||
        message?.contains("ConnectException") == true ||
        message?.contains("Network is unreachable") == true ->
            "Cannot reach server. Check your connection and retry."
        else -> "Something went wrong. Please try again."
    }
}
