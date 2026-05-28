package io.jadu.strideSync.network

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class SessionEventBus {
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired = _sessionExpired.asSharedFlow()

    fun notifyExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}
