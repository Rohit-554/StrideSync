package io.jadu.strideSync.network.circuitbreaker

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Clock

data class CircuitBreakerConfig(
    val failureThreshold: Int = 5,
    val openDurationMillis: Long = 30_000,
    val halfOpenMaxCalls: Int = 1
) {
    init {
        require(failureThreshold > 0) { "failureThreshold must be greater than 0" }
        require(openDurationMillis > 0) { "openDurationMillis must be greater than 0" }
        require(halfOpenMaxCalls > 0) { "halfOpenMaxCalls must be greater than 0" }
    }
}

sealed interface CircuitBreakerState {
    data object Closed : CircuitBreakerState
    data class Open(val openedAtMillis: Long) : CircuitBreakerState
    data object HalfOpen : CircuitBreakerState
}

class CircuitBreakerOpenException(
    serviceName: String
) : Exception("Circuit breaker is open for $serviceName")


// Request ticket
class CircuitBreakerPermit internal constructor(
    internal val generation: Long,
    internal val state: CircuitBreakerState
)

class CircuitBreaker(
    private val serviceName: String,
    private val config: CircuitBreakerConfig = CircuitBreakerConfig(),
    private val currentTimeMillis: () -> Long = { Clock.System.now().toEpochMilliseconds() }
) {
    private val mutex = Mutex()
    private var state: CircuitBreakerState = CircuitBreakerState.Closed
    private var failureCount: Int = 0
    private var halfOpenInFlight: Int = 0
    private var generation: Long = 0

    suspend fun acquirePermit(): CircuitBreakerPermit = mutex.withLock {
        when (val currentState = state) {
            CircuitBreakerState.Closed ->
                CircuitBreakerPermit(generation = generation, state = currentState)

            is CircuitBreakerState.Open -> {
                val elapsedMillis = currentTimeMillis() - currentState.openedAtMillis
                if (elapsedMillis < config.openDurationMillis) {
                    throw CircuitBreakerOpenException(serviceName)
                }

                transitionToHalfOpen()
                acquireHalfOpenPermit()
            }

            CircuitBreakerState.HalfOpen ->
                acquireHalfOpenPermit()
        }
    }

    suspend fun recordSuccess(permit: CircuitBreakerPermit) {
        mutex.withLock {
            when (permit.state) {
                CircuitBreakerState.Closed -> {
                    if (state == CircuitBreakerState.Closed) {
                        failureCount = 0
                    }
                }

                CircuitBreakerState.HalfOpen -> {
                    if (state == CircuitBreakerState.HalfOpen && permit.generation == generation) {
                        transitionToClosed()
                    }
                }

                is CircuitBreakerState.Open -> Unit
            }
        }
    }

    suspend fun recordFailure(permit: CircuitBreakerPermit) {
        mutex.withLock {
            when (permit.state) {
                CircuitBreakerState.Closed -> {
                    if (state == CircuitBreakerState.Closed) {
                        failureCount += 1
                        if (failureCount >= config.failureThreshold) {
                            transitionToOpen()
                        }
                    }
                }

                CircuitBreakerState.HalfOpen -> {
                    if (state == CircuitBreakerState.HalfOpen && permit.generation == generation) {
                        transitionToOpen()
                    }
                }

                is CircuitBreakerState.Open -> Unit
            }
        }
    }

    suspend fun currentState(): CircuitBreakerState = mutex.withLock { state }

    private fun acquireHalfOpenPermit(): CircuitBreakerPermit {
        if (halfOpenInFlight >= config.halfOpenMaxCalls) {
            throw CircuitBreakerOpenException(serviceName)
        }
        halfOpenInFlight += 1
        return CircuitBreakerPermit(generation = generation, state = CircuitBreakerState.HalfOpen)
    }

    private fun transitionToClosed() {
        state = CircuitBreakerState.Closed
        failureCount = 0
        halfOpenInFlight = 0
        generation += 1
    }

    private fun transitionToOpen() {
        state = CircuitBreakerState.Open(openedAtMillis = currentTimeMillis())
        halfOpenInFlight = 0
        generation += 1
    }

    private fun transitionToHalfOpen() {
        state = CircuitBreakerState.HalfOpen
        halfOpenInFlight = 0
        generation += 1
    }
}
