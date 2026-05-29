package io.jadu.strideSync.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.jadu.strideSync.domain.model.User
import io.jadu.strideSync.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    sealed interface UiState {
        data object Idle : UiState
        data object Loading : UiState
        data class Success(val user: User) : UiState
        data class Error(val message: String) : UiState
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent.asSharedFlow()

    init {
        checkAuthStatus()
    }

    fun login(email: String, password: String) {
        authenticate { authRepository.login(email, password) }
    }

    fun register(email: String, displayName: String, password: String) {
        authenticate { authRepository.register(email, displayName, password) }
    }

    private fun authenticate(authenticationCall: suspend () -> Result<User>) {
        _uiState.value = UiState.Loading
        viewModelScope.launch {
            authenticationCall()
                .onSuccess { user -> _uiState.value = UiState.Success(user) }
                .onFailure { error ->
                    _uiState.value = UiState.Idle
                    _errorEvent.emit(error.message.orEmpty())
                }
        }
    }

    fun checkAuthStatus() {
        if (authRepository.isLoggedIn()) {
            _uiState.value = UiState.Success(
                User(id = "", displayName = "", email = "")
            )
        }
    }
}
