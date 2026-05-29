package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.Activity
import io.jadu.strideSync.domain.model.Comment
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.ActivityRepository
import io.jadu.strideSync.domain.repository.SocialRepository
import io.jadu.strideSync.utils.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivityDetailViewModel(
    private val activityRepository: ActivityRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    data class ActivityDetailUiState(
        val activity: Activity? = null,
        val athlete: User? = null,
        val comments: List<Comment> = emptyList(),
        val kudosCount: Int = 0,
        val hasKudosed: Boolean = false,
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val commentText: String = ""
    )

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    fun loadActivity(activityId: String) {
        setLoadingState()
        viewModelScope.launch {
            activityRepository.getActivityById(activityId)
                .onSuccess { activity ->
                    _uiState.value = _uiState.value.copy(activity = activity, isLoading = false)
                    loadActivitySocialData(activity)
                }
                .onFailure { error -> showError(error.toUiMessage(), isLoading = false) }
        }
    }

    fun toggleKudos(activityId: String) {
        viewModelScope.launch {
            val current = _uiState.value
            socialRepository.toggleKudos(activityId, current.hasKudosed)
                .onSuccess { hasKudosed ->
                    _uiState.value = current.copy(
                        hasKudosed = hasKudosed,
                        kudosCount = if (hasKudosed) current.kudosCount + 1 else (current.kudosCount - 1).coerceAtLeast(0)
                    )
                }
        }
    }

    private suspend fun loadActivitySocialData(activity: Activity) {
        socialRepository.getUserProfile(activity.userId)
            .onSuccess { profile ->
                _uiState.value = _uiState.value.copy(athlete = profile.user)
            }
        socialRepository.getComments(activity.id)
            .onSuccess { comments ->
                _uiState.value = _uiState.value.copy(comments = comments)
            }
    }

    fun onCommentTextChange(text: String) {
        _uiState.value = _uiState.value.copy(commentText = text)
    }

    fun addComment(activityId: String) {
        val text = _uiState.value.commentText.trim()
        if (text.isEmpty()) return

        viewModelScope.launch {
            socialRepository.addComment(activityId, text)
                .onSuccess { comment ->
                    val current = _uiState.value
                    _uiState.value = current.copy(
                        comments = current.comments + comment,
                        commentText = ""
                    )
                }
                .onFailure { error ->
                    showError(error.toUiMessage())
                }
        }
    }

    private fun setLoadingState() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
    }

    private fun showError(message: String, isLoading: Boolean = _uiState.value.isLoading) {
        _uiState.value = _uiState.value.copy(isLoading = isLoading, errorMessage = message)
    }
}
