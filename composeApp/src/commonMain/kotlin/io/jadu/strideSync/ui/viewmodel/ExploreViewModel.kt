package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.AthleteSummary
import io.jadu.strideSync.domain.repository.SocialRepository
import io.jadu.strideSync.utils.toUiMessage
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExploreViewModel(
    private val socialRepository: SocialRepository
) : ViewModel() {

    data class ExploreUiState(
        val query: String = "",
        val suggestedAthletes: List<AthleteSummary> = emptyList(),
        val searchResults: List<AthleteSummary> = emptyList(),
        val isLoading: Boolean = false,
        val isSearching: Boolean = false,
        val errorMessage: String? = null
    ) {
        val showingSearchResults: Boolean
            get() = query.trim().length >= 2
    }

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadSuggestions()
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query, errorMessage = null)
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }

        searchJob = viewModelScope.launch {
            delay(250)
            searchAthletes(query.trim())
        }
    }

    fun loadSuggestions() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            socialRepository.getSuggestedAthletes(limit = 8)
                .onSuccess { athletes ->
                    _uiState.value = _uiState.value.copy(
                        suggestedAthletes = athletes,
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

    fun toggleFollow(athleteId: String) {
        val current = _uiState.value
        val athlete = (current.searchResults + current.suggestedAthletes).firstOrNull { it.id == athleteId } ?: return
        val updatedFollowState = !athlete.isFollowing
        updateAthlete(athleteId, updatedFollowState)

        viewModelScope.launch {
            val result = if (athlete.isFollowing) {
                socialRepository.unfollowUser(athleteId)
            } else {
                socialRepository.followUser(athleteId)
            }

            result.onFailure {
                updateAthlete(athleteId, athlete.isFollowing)
            }
        }
    }

    private fun searchAthletes(query: String) {
        _uiState.value = _uiState.value.copy(isSearching = true, errorMessage = null)
        viewModelScope.launch {
            socialRepository.searchAthletes(query = query, page = 0, size = 20)
                .onSuccess { athletes ->
                    _uiState.value = _uiState.value.copy(
                        searchResults = athletes,
                        isSearching = false
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        isSearching = false,
                        errorMessage = error.toUiMessage()
                    )
                }
        }
    }

    private fun updateAthlete(athleteId: String, isFollowing: Boolean) {
        _uiState.value = _uiState.value.copy(
            suggestedAthletes = _uiState.value.suggestedAthletes.map { athlete ->
                if (athlete.id == athleteId) athlete.copy(isFollowing = isFollowing) else athlete
            },
            searchResults = _uiState.value.searchResults.map { athlete ->
                if (athlete.id == athleteId) athlete.copy(isFollowing = isFollowing) else athlete
            }
        )
    }
}
