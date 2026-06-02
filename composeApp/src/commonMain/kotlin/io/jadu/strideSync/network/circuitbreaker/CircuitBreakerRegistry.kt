package io.jadu.strideSync.network.circuitbreaker

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

private const val DEFAULT_SERVICE_NAME = "stride-sync-api"

class CircuitBreakerRegistry(
    private val defaultConfig: CircuitBreakerConfig = CircuitBreakerConfig()
) {
    private val mutex = Mutex()
    private val breakers = mutableMapOf<String, CircuitBreaker>()

    // Mutex don't let other coroutines to interfere until one is finished - eg. feed, profile, activities etc.
    suspend fun get(serviceName: String = DEFAULT_SERVICE_NAME): CircuitBreaker =
        mutex.withLock {
            breakers.getOrPut(serviceName) {
                CircuitBreaker(
                    serviceName = serviceName,
                    config = defaultConfig
                )
            }
        }
}
