package co.ke.xently.features.auth.presentation.resetpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.auth.data.domain.UserDataValidator
import co.ke.xently.features.auth.data.domain.error.RemoteFieldError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.source.UserRepository
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
internal class RequestPasswordResetViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataValidator: UserDataValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(RequestPasswordResetUiState())
    val uiState: StateFlow<RequestPasswordResetUiState> = _uiState.asStateFlow()

    private val _event = Channel<RequestPasswordResetEvent>()
    val event: Flow<RequestPasswordResetEvent> = _event.receiveAsFlow()
    fun onAction(action: RequestPasswordResetAction) {
        when (action) {
            is RequestPasswordResetAction.ChangeEmail -> _uiState.update { it.copy(email = action.email) }

            RequestPasswordResetAction.ClickSubmitCredentials -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }

                    val email = validatedEmail(state)
                    if (!_uiState.value.isFormValid) return@launch

                    when (val result = repository.requestPasswordReset(email = email)) {
                        is Result.Failure -> {
                            when (val error = result.error) {
                                is RemoteFieldError -> {
                                    _uiState.update {
                                        it.copy(emailError = error.errors["email"] ?: emptyList())
                                    }
                                }

                                else -> _event.send(
                                    RequestPasswordResetEvent.Error(
                                        result.error.toUiText(),
                                        result.error
                                    )
                                )
                            }
                        }

                        is Result.Success -> {
                            _event.send(RequestPasswordResetEvent.Success)
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

    private fun validatedEmail(state: RequestPasswordResetUiState): String {
        var email = state.email
        when (val result = dataValidator.validatedEmail(email)) {
            is Result.Failure -> _uiState.update { it.copy(emailError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(emailError = null) }
                email = result.data
            }
        }
        return email
    }
}