package io.jadu.strideSync.network.circuitbreaker

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class CircuitBreakerTest {
    private var nowMillis = 1_000L

    private fun circuitBreaker(
        failureThreshold: Int = 5,
        openDurationMillis: Long = 30_000,
        halfOpenMaxCalls: Int = 1
    ) = CircuitBreaker(
        serviceName = "test-api",
        config = CircuitBreakerConfig(
            failureThreshold = failureThreshold,
            openDurationMillis = openDurationMillis,
            halfOpenMaxCalls = halfOpenMaxCalls
        ),
        currentTimeMillis = { nowMillis }
    )

    @Test
    fun closedAllowsCallsAndRecordsSuccess() = runTest {
        val breaker = circuitBreaker()

        val permit = breaker.acquirePermit()
        breaker.recordSuccess(permit)

        assertEquals(CircuitBreakerState.Closed, breaker.currentState())
    }

    @Test
    fun opensAfterConfiguredConsecutiveFailures() = runTest {
        val breaker = circuitBreaker(failureThreshold = 2)

        breaker.recordFailure(breaker.acquirePermit())
        assertEquals(CircuitBreakerState.Closed, breaker.currentState())

        breaker.recordFailure(breaker.acquirePermit())
        assertIs<CircuitBreakerState.Open>(breaker.currentState())
    }

    @Test
    fun openFailsFastBeforeCooldownExpires() = runTest {
        val breaker = circuitBreaker(failureThreshold = 1, openDurationMillis = 10_000)

        breaker.recordFailure(breaker.acquirePermit())

        assertFailsWith<CircuitBreakerOpenException> {
            breaker.acquirePermit()
        }
    }

    @Test
    fun cooldownMovesOpenBreakerToHalfOpenTrial() = runTest {
        val breaker = circuitBreaker(failureThreshold = 1, openDurationMillis = 10_000)

        breaker.recordFailure(breaker.acquirePermit())
        nowMillis += 10_000

        val permit = breaker.acquirePermit()

        assertEquals(CircuitBreakerState.HalfOpen, permit.state)
        assertEquals(CircuitBreakerState.HalfOpen, breaker.currentState())
    }

    @Test
    fun halfOpenSuccessClosesAndResetsFailures() = runTest {
        val breaker = circuitBreaker(failureThreshold = 2, openDurationMillis = 10_000)

        breaker.recordFailure(breaker.acquirePermit())
        breaker.recordFailure(breaker.acquirePermit())
        nowMillis += 10_000

        val trialPermit = breaker.acquirePermit()
        breaker.recordSuccess(trialPermit)

        assertEquals(CircuitBreakerState.Closed, breaker.currentState())

        breaker.recordFailure(breaker.acquirePermit())
        assertEquals(CircuitBreakerState.Closed, breaker.currentState())
    }

    @Test
    fun halfOpenFailureReopens() = runTest {
        val breaker = circuitBreaker(failureThreshold = 1, openDurationMillis = 10_000)

        breaker.recordFailure(breaker.acquirePermit())
        nowMillis += 10_000

        breaker.recordFailure(breaker.acquirePermit())

        assertIs<CircuitBreakerState.Open>(breaker.currentState())
    }

    @Test
    fun halfOpenAllowsOnlyConfiguredTrialCalls() = runTest {
        val breaker = circuitBreaker(failureThreshold = 1, openDurationMillis = 10_000)

        breaker.recordFailure(breaker.acquirePermit())
        nowMillis += 10_000

        breaker.acquirePermit()

        assertFailsWith<CircuitBreakerOpenException> {
            breaker.acquirePermit()
        }
    }

    @Test
    fun olderClosedSuccessDoesNotCloseBreakerAfterConcurrentFailureOpenedIt() = runTest {
        val breaker = circuitBreaker(failureThreshold = 1)

        val olderPermit = breaker.acquirePermit()
        val failingPermit = breaker.acquirePermit()

        breaker.recordFailure(failingPermit)
        breaker.recordSuccess(olderPermit)

        assertIs<CircuitBreakerState.Open>(breaker.currentState())
    }
}
