package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.FeedItem
import io.jadu.strideSync.domain.model.StatusItem
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.AuthRepository
import io.jadu.strideSync.domain.repository.FeedRepository
import io.jadu.strideSync.domain.repository.SocialRepository
import io.jadu.strideSync.utils.toUiMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FeedViewModel(
    private val authRepository: AuthRepository,
    private val feedRepository: FeedRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 20
        private const val DEFAULT_STATUS_BACKGROUND = "#FF571B"
    }

    sealed interface FeedUiState {
        data object Loading : FeedUiState
        data class Success(
            val items: List<FeedItem>,
            val hasMore: Boolean,
            val isRefreshing: Boolean = false,
            val isLoadingMore: Boolean = false
        ) : FeedUiState
        data class Error(val message: String) : FeedUiState
        data object Empty : FeedUiState
    }

    data class StoryUiState(
        val currentUser: User? = null,
        val statuses: List<StatusItem> = emptyList(),
        val selectedStatus: StatusItem? = null,
        val isComposerOpen: Boolean = false,
        val composerText: String = "",
        val composerBackgroundHex: String = DEFAULT_STATUS_BACKGROUND,
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow<FeedUiState>(FeedUiState.Loading)
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    private val _storyUiState = MutableStateFlow(
        StoryUiState(currentUser = authRepository.getCurrentUser())
    )
    val storyUiState: StateFlow<StoryUiState> = _storyUiState.asStateFlow()

    private val loadedItems = mutableListOf<FeedItem>()
    private var currentPage = 0
    private var hasMorePages = true
    private var isLoadingMore = false

    init {
        loadFeed()
        loadStatuses()
    }

    fun loadFeed() {
        loadedItems.clear()
        currentPage = 0
        hasMorePages = true
        _uiState.value = FeedUiState.Loading
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            loadStatuses()
            val currentItems = (uiState.value as? FeedUiState.Success)?.items.orEmpty()
            if (currentItems.isNotEmpty()) {
                _uiState.value = FeedUiState.Success(
                    items = currentItems,
                    hasMore = hasMorePages,
                    isRefreshing = true
                )
            }

            feedRepository.loadFeedPage(page = 0, size = PAGE_SIZE)
                .onSuccess { items ->
                    loadedItems.clear()
                    loadedItems.addAll(items)
                    currentPage = 0
                    hasMorePages = items.size >= PAGE_SIZE
                    _uiState.value = if (items.isEmpty()) {
                        FeedUiState.Empty
                    } else {
                        FeedUiState.Success(items = loadedItems.toList(), hasMore = hasMorePages)
                    }
                }
                .onFailure { error ->
                    _uiState.value = FeedUiState.Error(error.toUiMessage())
                }
        }
    }

    fun openStatus(status: StatusItem) {
        _storyUiState.value = _storyUiState.value.copy(
            selectedStatus = status,
            isComposerOpen = false,
            errorMessage = null
        )
    }

    fun openMyStatus() {
        val existing = _storyUiState.value.statuses.firstOrNull { it.isOwn }
        if (existing != null) {
            openStatus(existing)
        } else {
            _storyUiState.value = _storyUiState.value.copy(
                isComposerOpen = true,
                selectedStatus = null,
                errorMessage = null
            )
        }
    }

    fun openComposerForUpdate() {
        val current = _storyUiState.value
        val existing = current.statuses.firstOrNull { it.isOwn }
        _storyUiState.value = current.copy(
            selectedStatus = null,
            isComposerOpen = true,
            composerText = existing?.text.orEmpty(),
            composerBackgroundHex = existing?.backgroundHex ?: DEFAULT_STATUS_BACKGROUND,
            errorMessage = null
        )
    }

    fun dismissStatusViewer() {
        _storyUiState.value = _storyUiState.value.copy(selectedStatus = null)
    }

    fun dismissComposer() {
        _storyUiState.value = _storyUiState.value.copy(
            isComposerOpen = false,
            composerText = "",
            composerBackgroundHex = DEFAULT_STATUS_BACKGROUND,
            errorMessage = null
        )
    }

    fun onComposerTextChange(text: String) {
        _storyUiState.value = _storyUiState.value.copy(
            composerText = text.take(160),
            errorMessage = null
        )
    }

    fun onComposerBackgroundChange(backgroundHex: String) {
        _storyUiState.value = _storyUiState.value.copy(
            composerBackgroundHex = backgroundHex,
            errorMessage = null
        )
    }

    fun submitStatus() {
        val current = _storyUiState.value
        val text = current.composerText.trim()
        if (text.isEmpty()) {
            _storyUiState.value = current.copy(errorMessage = "Write something for your status")
            return
        }

        viewModelScope.launch {
            _storyUiState.value = current.copy(isSubmitting = true, errorMessage = null)
            socialRepository.createStatus(text, current.composerBackgroundHex)
                .onSuccess { created ->
                    val updatedStatuses = (_storyUiState.value.statuses.filterNot { it.userId == created.userId } + created)
                        .sortedWith(compareByDescending<StatusItem> { it.isOwn }.thenByDescending { it.createdAt })
                    _storyUiState.value = _storyUiState.value.copy(
                        statuses = updatedStatuses,
                        selectedStatus = created,
                        isComposerOpen = false,
                        composerText = "",
                        composerBackgroundHex = DEFAULT_STATUS_BACKGROUND,
                        isSubmitting = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _storyUiState.value = _storyUiState.value.copy(
                        isSubmitting = false,
                        errorMessage = error.toUiMessage()
                    )
                }
        }
    }

    private fun loadStatuses() {
        viewModelScope.launch {
            _storyUiState.value = _storyUiState.value.copy(currentUser = authRepository.getCurrentUser())
            socialRepository.getStatuses()
                .onSuccess { statuses ->
                    _storyUiState.value = _storyUiState.value.copy(statuses = statuses, errorMessage = null)
                }
                .onFailure { error ->
                    _storyUiState.value = _storyUiState.value.copy(errorMessage = error.toUiMessage())
                }
        }
    }

    fun loadMore() {
        val current = _uiState.value as? FeedUiState.Success ?: return
        if (isLoadingMore || !current.hasMore) return

        isLoadingMore = true
        _uiState.value = current.copy(isLoadingMore = true)
        viewModelScope.launch {
            feedRepository.loadFeedPage(page = currentPage + 1, size = PAGE_SIZE)
                .onSuccess { items ->
                    val newItems = items.filterNot { incoming ->
                        loadedItems.any { it.activity.id == incoming.activity.id }
                    }
                    loadedItems.addAll(newItems)
                    currentPage += 1
                    hasMorePages = items.size >= PAGE_SIZE
                    _uiState.value = FeedUiState.Success(
                        items = loadedItems.toList(),
                        hasMore = hasMorePages,
                        isLoadingMore = false
                    )
                }
                .onFailure {
                    _uiState.value = current.copy(isLoadingMore = false)
                }
            isLoadingMore = false
        }
    }

    fun toggleKudos(activityId: String) {
        viewModelScope.launch {
            val current = _uiState.value
            val item = (current as? FeedUiState.Success)?.items?.firstOrNull { it.activity.id == activityId }
                ?: return@launch
            socialRepository.toggleKudos(activityId, item.hasKudosed)
                .onSuccess { hasKudosed ->
                    val currentItems = current.items
                    val updatedItems = currentItems.map { item ->
                        if (item.activity.id == activityId) {
                            item.copy(
                                kudosCount = if (hasKudosed) item.kudosCount + 1 else (item.kudosCount - 1).coerceAtLeast(0),
                                hasKudosed = hasKudosed
                            )
                        } else {
                            item
                        }
                    }
                    _uiState.value = current.copy(items = updatedItems)
                    feedRepository.refreshFeed()
                }
                .onFailure { /* Silently fail — UI stays in optimistic state */ }
        }
    }
}
