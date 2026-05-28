package io.jadu.strideSync.tracking

import android.content.Context
import androidx.core.content.ContextCompat

class AndroidTrackingServiceController(
    private val context: Context
) : TrackingServiceController {

    override fun start() {
        ContextCompat.startForegroundService(context, TrackingService.startIntent(context))
    }

    override fun stop() {
        context.stopService(TrackingService.stopIntent(context))
    }
}
