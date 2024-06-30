package co.ke.xently.features.profile.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ProfileDetailViewModel @Inject constructor(
    private val repository: ProfileStatisticRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileDetailUiState())
    val uiState: StateFlow<ProfileDetailUiState> = _uiState.asStateFlow()

    private val _event = Channel<ProfileDetailEvent>()
    val event: Flow<ProfileDetailEvent> = _event.receiveAsFlow()

    init {
        viewModelScope.launch {
            repository.findStatisticById()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .onCompletion {
                    _uiState.update {
                        it.copy(isLoading = false)
                    }
                }
                .collect { result ->
                    _uiState.update {
                        when (result) {
                            is Result.Failure -> ProfileDetailUiState()
                            is Result.Success -> {
                                ProfileDetailUiState(profileStatistic = result.data)
                            }
                        }
                    }
                }
        }
    }

    fun onAction(action: ProfileDetailAction) {
        when (action) {
            ProfileDetailAction.Refresh -> {

            }
        }
    }
}