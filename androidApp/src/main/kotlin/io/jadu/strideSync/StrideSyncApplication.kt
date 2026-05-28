package io.jadu.strideSync

import android.app.Application
import io.jadu.strideSync.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StrideSyncApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@StrideSyncApplication)
            modules(appModule)
        }
    }
}
