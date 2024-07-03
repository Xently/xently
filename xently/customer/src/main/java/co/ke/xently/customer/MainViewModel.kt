package co.ke.xently.customer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.access.control.data.AccessControlRepository
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.source.UserRepository
import co.ke.xently.features.auth.domain.GoogleAuthenticationHandler
import co.ke.xently.features.auth.presentation.utils.asUiText
import co.ke.xently.libraries.data.auth.AuthenticationState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val accessControlRepository: AccessControlRepository,
    private val googleAuthenticationHandler: GoogleAuthenticationHandler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    private val uiState = _uiState.asStateFlow()

    val authenticationState = uiState.map {
        AuthenticationState(isSignOutInProgress = it.isLoading, currentUser = it.user)
    }.stateIn(
        scope = viewModelScope,
        initialValue = AuthenticationState(),
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
    )

    private val _event = Channel<MainEvent>()
    val event: Flow<MainEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            accessControlRepository.findAccessControl().collect {
                _uiState.update { state -> state.copy(canAddShop = it.canAddShop) }
            }
        }
        viewModelScope.launch {
            userRepository.getCurrentUser().collect {
                _uiState.update { state -> state.copy(user = it) }
            }
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            MainAction.ClickSignOut -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = userRepository.signOut()) {
                        is Result.Failure -> {
                            _event.send(
                                MainEvent.Error(
                                    error = result.error.asUiText(),
                                    type = result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            googleAuthenticationHandler.signOut()
                            _event.send(MainEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }
}