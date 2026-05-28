package io.jadu.strideSync.di

import android.content.Context.MODE_PRIVATE
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import io.jadu.strideSync.gps.GpsProvider
import io.jadu.strideSync.notifications.NotificationScheduler
import io.jadu.strideSync.tracking.AndroidTrackingServiceController
import io.jadu.strideSync.tracking.TrackingServiceController
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<ObservableSettings> {
        SharedPreferencesSettings(
            androidContext().getSharedPreferences("catylst_prefs", MODE_PRIVATE)
        )
    }

    single { NotificationScheduler(androidContext()) }
    single { GpsProvider(androidContext()) }
    single<TrackingServiceController> { AndroidTrackingServiceController(androidContext()) }
}
