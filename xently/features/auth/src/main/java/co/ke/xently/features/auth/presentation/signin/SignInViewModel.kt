package co.ke.xently.features.auth.presentation.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.source.UserRepository
import co.ke.xently.features.auth.domain.GoogleAuthenticationHandler
import co.ke.xently.features.auth.presentation.utils.asUiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SignInViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authenticationHandler: GoogleAuthenticationHandler,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    private val _event = Channel<SignInEvent>()
    val event: Flow<SignInEvent> = _event.receiveAsFlow()

    fun onAction(action: SignInAction) {
        when (action) {
            is SignInAction.ChangeEmail -> _uiState.update { it.copy(email = action.email) }
            is SignInAction.ChangePassword -> _uiState.update { it.copy(password = action.password) }
            SignInAction.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            is SignInAction.FinaliseGoogleSignIn -> {
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(isLoading = true)
                    }
                    when (val result = repository.signInWithGoogle(user = action.user)) {
                        is Result.Failure -> {
                            _event.send(
                                SignInEvent.Error(
                                    result.error.asUiText(),
                                    result.error,
                                )
                            )
                        }

                        is Result.Success -> {
                            // Sign-in can be requested from any screen after an access token is
                            // flagged as expired by the server. Therefore, instead of navigating
                            // to a dedicated screen, we should simply retain the screen the
                            // authentication was requested from, hence creating a good UX.
                            _event.send(SignInEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            is SignInAction.ClickSignInWithGoogle -> {
                viewModelScope.launch {
                    when (val result = authenticationHandler.signIn(action.activityContext)) {
                        is Result.Success -> {
                            _event.send(SignInEvent.GetGoogleAccessToken(result.data))
                        }

                        is Result.Failure -> {
                            _event.send(SignInEvent.Error(result.error.asUiText(), result.error))
                        }
                    }
                }
            }

            SignInAction.ClickSubmitCredentials -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }
                    when (val result =
                        repository.signInWithEmailAndPassword(
                            email = state.email,
                            password = state.password
                        )) {
                        is Result.Failure -> {
                            _event.send(SignInEvent.Error(result.error.asUiText(), result.error))
                        }

                        is Result.Success -> {
                            // Sign-in can be requested from any screen after an access token is
                            // flagged as expired by the server. Therefore, instead of navigating
                            // to a dedicated screen, we should simply retain the screen the
                            // authentication was requested from, hence creating a good UX.
                            _event.send(SignInEvent.Success)
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