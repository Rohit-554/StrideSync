package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.value = HomeUiState.Success(
                message = "Welcome to strideSync!",
                items = listOf(
                    "Compose Multiplatform",
                    "Koin DI",
                    "Navigation3",
                    "Ktor Networking",
                    "Room 3.0",
                    "Multiplatform Preferences",
                    "Push Notifications",
                    "Runtime Permissions",
                    "Ktor Server"
                )
            )
        }
    }
}

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val message: String,
        val items: List<String>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
