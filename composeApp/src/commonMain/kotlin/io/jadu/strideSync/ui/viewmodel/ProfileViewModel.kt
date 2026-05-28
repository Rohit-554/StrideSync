package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.ActivityRepository
import io.jadu.strideSync.domain.repository.AuthRepository
import io.jadu.strideSync.domain.repository.SocialRepository
import io.jadu.strideSync.utils.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val activityRepository: ActivityRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    data class ProfileUiState(
        val user: User? = null,
        val activityCount: Int = 0,
        val followerCount: Int = 0,
        val followingCount: Int = 0,
        val recentActivities: List<Activity> = emptyList(),
        val isLoading: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            // Get current user from auth repository
            val currentUser = authRepository.getCurrentUser()
                ?: run {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Not logged in")
                    return@launch
                }

            socialRepository.getUserProfile(currentUser.id)
                .onSuccess { profile ->
                    _uiState.value = _uiState.value.copy(
                        user = profile.user,
                        activityCount = profile.activityCount,
                        followerCount = profile.followerCount,
                        followingCount = profile.followingCount
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(user = currentUser)
                }

            activityRepository.getMyActivities(page = 0, size = 20)
                .onSuccess { activities ->
                    _uiState.value = _uiState.value.copy(
                        recentActivities = activities.take(5),
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = error.toUiMessage()
                    )
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }
}
