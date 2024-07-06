package co.ke.xently.features.profile.presentation.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.ke.xently.features.profile.data.domain.ProfileDataValidator
import co.ke.xently.features.profile.data.domain.ProfileStatistic
import co.ke.xently.features.profile.data.domain.error.Result
import co.ke.xently.features.profile.data.source.ProfileStatisticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileEditDetailViewModel @Inject constructor(
    private val repository: ProfileStatisticRepository,
    private val dataValidator: ProfileDataValidator,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileEditDetailUiState())
    val uiState: StateFlow<ProfileEditDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileEditDetailEvent>()
    val event: Flow<ProfileEditDetailEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.findStatisticById()
                .onStart { _uiState.update { it.copy(isLoading = true, disableFields = true) } }
                .onCompletion {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            disableFields = false
                        )
                    }
                }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Failure -> ProfileEditDetailUiState()
                            is Result.Success -> {
                                ProfileEditDetailUiState(profileStatistic = result.data)
                            }
                        }
                    }
                }
        }
    }

    fun onAction(action: ProfileEditDetailAction) {
        when (action) {
            is ProfileEditDetailAction.ChangeName -> {
                _uiState.update {
                    it.copy(name = action.name)
                }
            }

            ProfileEditDetailAction.ClickSave -> {
                viewModelScope.launch {
                    val state = _uiState.updateAndGet {
                        it.copy(
                            isLoading = true,
                            nameError = null,
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

        when (val result = dataValidator.validatedName(state.name)) {
            is Result.Failure -> _uiState.update { it.copy(nameError = listOf(result.error)) }
            is Result.Success -> {
                _uiState.update { it.copy(nameError = null) }
            }
        }

        return profile
    }
}