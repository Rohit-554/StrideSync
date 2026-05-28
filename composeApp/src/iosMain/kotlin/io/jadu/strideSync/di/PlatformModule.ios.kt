package io.jadu.strideSync.di

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import io.jadu.strideSync.gps.GpsProvider
import io.jadu.strideSync.notifications.NotificationScheduler
import io.jadu.strideSync.tracking.IosTrackingServiceController
import io.jadu.strideSync.tracking.TrackingServiceController
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<ObservableSettings> { NSUserDefaultsSettings.Factory().create("catylst_prefs") }

    single { NotificationScheduler() }
    single { GpsProvider() }
    single<TrackingServiceController> { IosTrackingServiceController() }
}
