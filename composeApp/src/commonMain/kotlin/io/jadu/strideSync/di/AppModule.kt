package io.jadu.strideSync.di

import io.jadu.strideSync.data.local.AppDatabase
import io.jadu.strideSync.data.local.createAppDatabase
import io.jadu.strideSync.data.preferences.AppPreferences
import io.jadu.strideSync.data.remote.api.ActivityApi
import io.jadu.strideSync.data.remote.api.AuthApi
import io.jadu.strideSync.data.remote.api.SocialApi
import io.jadu.strideSync.data.remote.api.WebSocketApi
import io.jadu.strideSync.data.repository.ActivityRepositoryImpl
import io.jadu.strideSync.data.repository.AuthRepositoryImpl
import io.jadu.strideSync.data.repository.FeedRepositoryImpl
import io.jadu.strideSync.data.repository.SocialRepositoryImpl
import io.jadu.strideSync.domain.repository.ActivityRepository
import io.jadu.strideSync.domain.repository.AuthRepository
import io.jadu.strideSync.domain.repository.FeedRepository
import io.jadu.strideSync.domain.repository.SocialRepository
import io.jadu.strideSync.network.circuitbreaker.CircuitBreakerRegistry
import io.jadu.strideSync.network.SessionEventBus
import io.jadu.strideSync.network.createHttpClient
import io.jadu.strideSync.tracking.TrackingEngine
import io.jadu.strideSync.ui.viewmodel.ActivityDetailViewModel
import io.jadu.strideSync.ui.viewmodel.AuthViewModel
import io.jadu.strideSync.ui.viewmodel.FeedViewModel
import io.jadu.strideSync.ui.viewmodel.ExploreViewModel
import io.jadu.strideSync.ui.viewmodel.ProfileViewModel
import io.jadu.strideSync.ui.viewmodel.RecordViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun appModule(): Module = module {
    single { SessionEventBus() }
    single { CircuitBreakerRegistry() }
    single<HttpClient> { createHttpClient(get(), get(), get()) }
    single { AuthApi(get()) }
    single { ActivityApi(get()) }
    single { SocialApi(get()) }
    single { WebSocketApi(get()) }

    single<AppDatabase> { createAppDatabase() }
    single { get<AppDatabase>().activityDao() }
    single { get<AppDatabase>().gpsPointDao() }
    single { get<AppDatabase>().feedDao() }
    single { AppPreferences(get()) }

    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
    single<ActivityRepository> { ActivityRepositoryImpl(get(), get(), get()) }
    single<FeedRepository> { FeedRepositoryImpl(get(), get()) }
    single<SocialRepository> { SocialRepositoryImpl(get(), get()) }

    single { TrackingEngine(get(), get()) }

    viewModel { AuthViewModel(get()) }
    viewModel { FeedViewModel(get(), get(), get()) }
    viewModel { ExploreViewModel(get()) }
    viewModel { RecordViewModel(get(), get(), get()) }
    viewModel { ActivityDetailViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(), get()) }
}

val appModule = listOf(
    appModule(),
    platformModule()
)
