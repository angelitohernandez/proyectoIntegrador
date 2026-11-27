package mx.edu.utez.proyectointegrador.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import mx.edu.utez.proyectointegrador.data.local.Session
import mx.edu.utez.proyectointegrador.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AuthEvent {
    data object NavigateToMain : AuthEvent
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    val sessionState: StateFlow<Session?> = authRepository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.username.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu usuario y contraseña") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(state.username, state.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, password = "") }
                    _events.emit(AuthEvent.NavigateToMain)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }

    fun getServerUrl(): String = authRepository.getServerUrl()

    fun setServerUrl(raw: String): String = authRepository.setServerUrl(raw)
}
