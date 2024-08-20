package co.ke.xently.features.profile.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.profile.data.domain.ProfileDataValidator
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.Result
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
internal class ProfileEditDetailViewModel @Inject constructor(
    private val dataValidator: ProfileDataValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditDetailUiState())
    val uiState: StateFlow<ProfileEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditDetailEvent>()
    val event: Flow<ProfileEditDetailEvent> = _event.receiveAsFlow()

    fun onAction(action: ProfileEditDetailAction) {
        when (action) {
            is ProfileEditDetailAction.ChangeFirstName -> {
                _uiState.update {
                    it.copy(firstName = action.name)
                }
            }

            is ProfileEditDetailAction.ChangeLastName -> {
                _uiState.update {
                    it.copy(lastName = action.name)
                }
            }

            is ProfileEditDetailAction.ChangeEmail -> {
                _uiState.update {
                    it.copy(email = action.email)
                }
            }

            ProfileEditDetailAction.ClickSave -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(
                            isLoading = true,
                            firstNameError = null,
                        )
                    }
                    val profile = validatedProfile(state)

                    if (_uiState.value.isFormValid) {
                        // TODO: Save updates to profile
                    }
                }.invokeOnCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
            }
        }
    }

    private fun validatedProfile(state: ProfileEditDetailUiState): ProfileStatistic {
        val profile = state.profileStatistic

        when (val result = dataValidator.validatedName(state.firstName)) {
            is Result.Failure -> _uiState.update { it.copy(firstNameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(firstNameError = null) }
            }
        }
        when (val result = dataValidator.validatedName(state.lastName)) {
            is Result.Failure -> _uiState.update { it.copy(lastNameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(lastNameError = null) }
            }
        }
        when (val result = dataValidator.validatedEmail(state.email)) {
            is Result.Failure -> _uiState.update { it.copy(emailError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(emailError = null) }
            }
        }

        return profile
    }
}