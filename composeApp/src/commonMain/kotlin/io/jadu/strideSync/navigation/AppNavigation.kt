package io.jadu.strideSync.navigation

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.jadu.strideSync.network.SessionEventBus
import org.koin.compose.koinInject
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import io.jadu.strideSync.ui.screens.SplashScreen
import io.jadu.strideSync.ui.screens.auth.LoginScreen
import io.jadu.strideSync.ui.screens.auth.RegisterScreen
import io.jadu.strideSync.ui.screens.detail.ActivityDetailScreen
import io.jadu.strideSync.ui.screens.explore.ExploreScreen
import io.jadu.strideSync.ui.screens.feed.FeedScreen
import io.jadu.strideSync.ui.screens.profile.ProfileScreen
import io.jadu.strideSync.ui.screens.record.ActivitySummaryScreen
import io.jadu.strideSync.ui.screens.record.RecordActiveScreen
import io.jadu.strideSync.ui.screens.record.RecordIdleScreen
import io.jadu.strideSync.ui.screens.record.RecordScreen
import io.jadu.strideSync.ui.viewmodel.AuthViewModel
import io.jadu.strideSync.ui.viewmodel.RecordViewModel
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AppNavigation() {
    val authViewModel = koinViewModel<AuthViewModel>()
    val recordViewModel = koinViewModel<RecordViewModel>()
    val recordUiState = recordViewModel.uiState.collectAsStateWithLifecycle().value
    val sessionEventBus = koinInject<SessionEventBus>()

    val backStack = rememberNavBackStack(
        SavedStateConfiguration {
            serializersModule = SerializersModule {
                polymorphic(NavKey::class) {
                    subclass(Screen.Splash::class)
                    subclass(Screen.Login::class)
                    subclass(Screen.Register::class)
                    subclass(Screen.Feed::class)
                    subclass(Screen.Explore::class)
                    subclass(Screen.Profile::class)
                    subclass(Screen.Record::class)
                    subclass(Screen.RecordIdle::class)
                    subclass(Screen.RecordActive::class)
                    subclass(Screen.ActivitySummary::class)
                    subclass(Screen.ActivityDetail::class)
                    subclass(Screen.Home::class)
                    subclass(Screen.Detail::class)
                    subclass(Screen.Permissions::class)
                    subclass(Screen.Notifications::class)
                    subclass(Screen.Preferences::class)
                }
            }
        },
        Screen.Splash
    )

    // Auto-logout on 401 — fires from any API call anywhere in the app
    LaunchedEffect(Unit) {
        sessionEventBus.sessionExpired.collect {
            backStack.clear()
            backStack.add(Screen.Login)
        }
    }

    Crossfade(targetState = backStack.lastOrNull() ?: Screen.Splash) { screen ->
        when (screen) {

            is Screen.Splash -> SplashScreen(
                onGetStarted = {
                    backStack.clear()
                    val isLoggedIn = authViewModel.uiState.value is AuthViewModel.UiState.Success
                    backStack.add(if (isLoggedIn) Screen.Feed else Screen.Login)
                }
            )

            is Screen.Login -> LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                },
                onNavigateToRegister = { backStack.add(Screen.Register) }
            )

            is Screen.Register -> RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                },
                onNavigateToLogin = { backStack.removeLastOrNull() }
            )

            is Screen.Feed -> FeedScreen(
                onNavigateToRecord = { backStack.add(Screen.Record) },
                onNavigateToExplore = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                    backStack.add(Screen.Explore)
                },
                onNavigateToProfile = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                    backStack.add(Screen.Profile)
                },
                onNavigateToActivity = { activityId -> backStack.add(Screen.ActivityDetail(activityId)) }
            )

            is Screen.Explore -> ExploreScreen(
                onNavigateToFeed = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                },
                onNavigateToRecord = { backStack.add(Screen.Record) },
                onNavigateToProfile = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                    backStack.add(Screen.Profile)
                }
            )

            is Screen.Profile -> ProfileScreen(
                onNavigateToFeed = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                },
                onNavigateToExplore = {
                    backStack.clear()
                    backStack.add(Screen.Feed)
                    backStack.add(Screen.Explore)
                },
                onNavigateToRecord = { backStack.add(Screen.Record) },
                onNavigateToLogin = {
                    backStack.clear()
                    backStack.add(Screen.Login)
                },
                onViewActivity = { activityId ->
                    backStack.add(Screen.ActivityDetail(activityId))
                }
            )

            is Screen.Record -> RecordScreen(
                viewModel = recordViewModel,
                onNavigateBack = { backStack.removeLastOrNull() },
                onNavigateToSummary = {
                    backStack.removeLastOrNull()
                    backStack.add(Screen.ActivitySummary)
                }
            )

            is Screen.RecordIdle -> RecordIdleScreen(
                onStartRecording = { backStack.add(Screen.RecordActive) },
                onNavigateBack = { backStack.removeLastOrNull() }
            )

            is Screen.RecordActive -> RecordActiveScreen(
                onStop = {
                    backStack.removeLastOrNull()
                    backStack.add(Screen.ActivitySummary)
                },
                onPause = {}
            )

            is Screen.ActivitySummary -> {
                LaunchedEffect(recordUiState.saveComplete) {
                    if (recordUiState.saveComplete) {
                        recordViewModel.resetSaveState()
                        backStack.clear()
                        backStack.add(Screen.Feed)
                    }
                }

                ActivitySummaryScreen(
                    distanceKm = recordUiState.distanceKm,
                    duration = recordUiState.duration,
                    pace = recordUiState.pace,
                    isSaving = recordUiState.isSaving,
                    errorMessage = recordUiState.errorMessage,
                    onSave = { title ->
                        recordViewModel.saveActivity(title.ifBlank { "Untitled Activity" })
                    },
                    onDiscard = {
                        recordViewModel.discardActivity()
                        backStack.clear()
                        backStack.add(Screen.Feed)
                    }
                )
            }

            is Screen.ActivityDetail -> ActivityDetailScreen(
                activityId = screen.activityId,
                onBack = { backStack.removeLastOrNull() }
            )

            else -> Unit
        }
    }
}
