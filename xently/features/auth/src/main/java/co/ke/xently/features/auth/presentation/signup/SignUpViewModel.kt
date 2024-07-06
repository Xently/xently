package co.ke.xently.features.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
                    when (val result = repository.signUp(
                        name = state.name,
                        email = state.email,
                        password = state.password
                    )) {
                        is Result.Failure -> {
                            _event.send(SignUpEvent.Error(result.error.asUiText(), result.error))
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
}