package io.jadu.strideSync.data.preferences

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.getStringOrNullFlow
import io.jadu.strideSync.AppConfig
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalSettingsApi::class)
class AppPreferences(private val settings: ObservableSettings) {

    val authTokenFlow: Flow<String?> = settings.getStringOrNullFlow(KEY_AUTH_TOKEN)

    var authToken: String?
        get() = settings.getStringOrNull(KEY_AUTH_TOKEN)
        set(value) = if (value != null) settings.putString(KEY_AUTH_TOKEN, value)
                     else settings.remove(KEY_AUTH_TOKEN)

    var onboardingComplete: Boolean
        get() = settings.getBoolean(KEY_ONBOARDING, defaultValue = false)
        set(value) = settings.putBoolean(KEY_ONBOARDING, value)

    var username: String
        get() = settings.getString(KEY_USERNAME, defaultValue = "")
        set(value) = settings.putString(KEY_USERNAME, value)

    var userId: String
        get() = settings.getString(KEY_USER_ID, defaultValue = "")
        set(value) = settings.putString(KEY_USER_ID, value)

    var userEmail: String
        get() = settings.getString(KEY_USER_EMAIL, defaultValue = "")
        set(value) = settings.putString(KEY_USER_EMAIL, value)

    var userAvatarUrl: String?
        get() = settings.getStringOrNull(KEY_USER_AVATAR_URL)
        set(value) = if (value != null) settings.putString(KEY_USER_AVATAR_URL, value)
                     else settings.remove(KEY_USER_AVATAR_URL)

    fun syncServerBaseUrl() {
        val savedBaseUrl = settings.getStringOrNull(KEY_SERVER_BASE_URL)
        if (savedBaseUrl == AppConfig.BASE_URL) return

        clearSession()
        settings.putString(KEY_SERVER_BASE_URL, AppConfig.BASE_URL)
    }

    fun clearSession() {
        authToken = null
        userId = ""
        username = ""
        userEmail = ""
        userAvatarUrl = null
    }

    fun clearAll() = settings.clear()

    companion object {
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_ONBOARDING = "onboarding_complete"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_AVATAR_URL = "user_avatar_url"
        private const val KEY_SERVER_BASE_URL = "server_base_url"
    }
}
