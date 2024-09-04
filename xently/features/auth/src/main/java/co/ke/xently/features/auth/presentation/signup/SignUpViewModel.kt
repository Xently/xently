package co.ke.xently.features.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.auth.data.domain.SignUpRequest
import co.ke.xently.features.auth.data.domain.UserDataValidator
import co.ke.xently.features.auth.data.domain.error.RemoteFieldError
import co.ke.xently.features.auth.data.domain.error.Result
import co.ke.xently.features.auth.data.source.UserRepository
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
internal class SignUpViewModel @Inject constructor(
    private val repository: UserRepository,
    private val dataValidator: UserDataValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _event = Channel<SignUpEvent>()
    val event: Flow<SignUpEvent> = _event.receiveAsFlow()
    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.ChangeName -> _uiState.update { it.copy(name = action.name) }
            is SignUpAction.ChangeEmail -> _uiState.update { it.copy(email = action.email) }
            is SignUpAction.ChangePassword -> _uiState.update { it.copy(password = action.password) }

            SignUpAction.ClickSubmitCredentials -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(isLoading = true)
                    }

                    val request = validatedSignUpRequest(state)

                    if (!_uiState.value.isFormValid) return@launch

                    when (val result = repository.signUp(request = request)) {
                        is Result.Failure -> {
                            when (val error = result.error) {
                                is RemoteFieldError -> {
                                    _uiState.update {
                                        val firstNameErrors = error.errors["firstName"]
                                            ?: emptyList()
                                        val lastNameErrors = error.errors["lastName"]
                                            ?: emptyList()
                                        it.copy(
                                            passwordError = error.errors["password"]
                                                ?: emptyList(),
                                            emailError = error.errors["emailAddress"]
                                                ?: emptyList(),
                                            nameError = firstNameErrors + lastNameErrors,
                                        )
                                    }
                                }

                                else -> _event.send(
                                    SignUpEvent.Error(
                                        result.error.asUiText(),
                                        result.error
                                    )
                                )
                            }
                        }

                        is Result.Success -> {
                            _event.send(SignUpEvent.Success)
                        }
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }

            SignUpAction.TogglePasswordVisibility -> _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
        }
    }

    private fun validatedSignUpRequest(state: SignUpUiState): SignUpRequest {
        var request = SignUpRequest(
            firstName = null,
            lastName = null,
            emailAddress = state.email,
            password = state.password,
        )

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
                request = request.copy(
                    lastName = result.data.lastName,
                    firstName = result.data.firstName,
                )
            }
        }

        when (val result = dataValidator.validatedEmail(state.email)) {
            is Result.Failure -> _uiState.update { it.copy(emailError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(emailError = null) }
                request = request.copy(emailAddress = result.data)
            }
        }

        when (val result = dataValidator.validatedPassword(state.password)) {
            is Result.Failure -> _uiState.update { it.copy(passwordError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(passwordError = null) }
                request = request.copy(password = result.data)
            }
        }

        return request
    }
}