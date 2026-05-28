package io.jadu.strideSync.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen : NavKey {

    // ── Auth flow ────────────────────────────────────────────────────────────
    @Serializable data object Splash : Screen
    @Serializable data object Login : Screen
    @Serializable data object Register : Screen

    // ── Main tabs ────────────────────────────────────────────────────────────
    @Serializable data object Feed : Screen
    @Serializable data object Explore : Screen
    @Serializable data object Profile : Screen

    // ── Record flow ──────────────────────────────────────────────────────────
    @Serializable data object Record : Screen
    @Serializable data object RecordIdle : Screen
    @Serializable data object RecordActive : Screen
    @Serializable data object ActivitySummary : Screen

    // ── Detail ───────────────────────────────────────────────────────────────
    @Serializable data class ActivityDetail(val activityId: String) : Screen

    // ── Legacy demo screens ───────────────────────────────────────────────────
    @Serializable data object Home : Screen
    @Serializable data class Detail(val id: Long, val title: String) : Screen
    @Serializable data object Permissions : Screen
    @Serializable data object Notifications : Screen
    @Serializable data object Preferences : Screen
}
