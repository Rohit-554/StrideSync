package io.jadu.strideSync.utils

import io.jadu.strideSync.network.SessionExpiredException
import io.jadu.strideSync.network.circuitbreaker.CircuitBreakerOpenException
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorMapperTest {
    @Test
    fun circuitBreakerOpenExceptionMapsToRetryLaterMessage() {
        val message = CircuitBreakerOpenException("test-api").toUiMessage()

        assertEquals(
            "Server is temporarily unavailable. Please try again shortly.",
            message
        )
    }

    @Test
    fun sessionExpiredExceptionKeepsSessionMessage() {
        val message = SessionExpiredException().toUiMessage()

        assertEquals("Session expired. Please log in again.", message)
    }
}
